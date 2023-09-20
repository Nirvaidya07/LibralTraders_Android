/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webkul.mobikul.BuildConfig
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.*
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants.DEFAULT_FCM_TOPICS
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CATEGORY
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CUSTOM_COLLECTION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_NOTIFICATION_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_USER_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_USER_NAME
import com.webkul.mobikul.helpers.BundleKeysHelper.KEY_ACCOUNT_TYPE
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.helpers.Utils
import java.io.InputStream
import java.net.URL
import java.util.*

private const val TAG = "FCMMsgReceiverService"

const val NOTIFICATION_CHANNEL_ORDERS = "orders"
const val NOTIFICATION_CHANNEL_OFFERS = "offers"
const val NOTIFICATION_CHANNEL_ABANDONED_CART = "abandonedCart"

private const val NOTIFICATION_TYPE_PRODUCT = "product"
private const val NOTIFICATION_TYPE_CATEGORY = "category"
private const val NOTIFICATION_TYPE_OTHERS = "others"
private const val NOTIFICATION_TYPE_CUSTOM = "custom"
private const val NOTIFICATION_TYPE_ORDER = "order"
private const val NOTIFICATION_TYPE_CHAT = "chat"
private const val NOTIFICATION_TYPE_SELLER_CHAT = "chatNotification"
private const val NOTIFICATION_TYPE_ORDER_PICKUP = "orderPickedUp"
private const val NOTIFICATION_TYPE_SELLER_ORDER = "sellerOrder"
private const val NOTIFICATION_TYPE_SELLER_PRODUCT_APPROVAL = "productApproval"
private const val NOTIFICATION_TYPE_SELLER_APPROVAL = "sellerApproval"

class FCMMessageReceiverService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        addNotificationChannels()
        Handler(Looper.getMainLooper()).postDelayed({
            subscribeTopics()
            Utils.sendRegistrationTokenToServer(this, token)
        }, 10000)
    }

    private fun subscribeTopics() {
        val pubSub = FirebaseMessaging.getInstance()
        for (topic in DEFAULT_FCM_TOPICS) {
            pubSub.subscribeToTopic(topic)
            if (BuildConfig.DEBUG) {
                pubSub.subscribeToTopic(topic + "_local")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "FCMMessageReceiverService onMessageReceived: $remoteMessage")
        Log.d(TAG, "FCMMessageReceiverService onMessageReceived: data " + remoteMessage?.data)

        try {
            val data = remoteMessage?.data
            if (data != null && data.isNotEmpty()) {
                val notificationId = data["id"]
                val notificationType = data["notificationType"]
                val notificationTitle = data["title"]
                val notificationBody = data["body"]
                val notificationBanner = data["banner_url"]

                var intent: Intent? = null
                if (AppSharedPref.getNotificationsEnabled(this)) {
                    when (notificationType) {
                        NOTIFICATION_TYPE_PRODUCT -> {
                            if (AppSharedPref.getOfferNotificationsEnabled(this)) {
                                intent = (applicationContext as MobikulApplication).getProductDetailsActivity(this)
                                intent.putExtra(BUNDLE_KEY_PRODUCT_ID, data["productId"])
                                intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, data["productName"])
                            }
                        }
                        NOTIFICATION_TYPE_CATEGORY -> {
                            if (AppSharedPref.getOfferNotificationsEnabled(this)) {
                                intent = Intent(this, CatalogActivity::class.java)
                                intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_CATEGORY)
                                intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, data["categoryName"])
                                intent.putExtra(BUNDLE_KEY_CATALOG_ID, data["categoryId"])
                            }
                        }
                        NOTIFICATION_TYPE_OTHERS -> {
                            if (AppSharedPref.getOfferNotificationsEnabled(this)) {
                                intent = Intent(this, OtherNotificationActivity::class.java)
                            }
                        }
                        NOTIFICATION_TYPE_CUSTOM -> {
                            if (AppSharedPref.getOfferNotificationsEnabled(this)) {
                                intent = Intent(this, CatalogActivity::class.java)
                                intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_CUSTOM_COLLECTION)
                                intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, notificationTitle)
                                intent.putExtra(BUNDLE_KEY_CATALOG_ID, notificationId)
                            }
                        }
                        NOTIFICATION_TYPE_ORDER_PICKUP,
                        NOTIFICATION_TYPE_ORDER -> {
                            if (AppSharedPref.getOrderNotificationsEnabled(this)) {
                                intent = Intent(this, OrderDetailsActivity::class.java)
                                intent.putExtra(BUNDLE_KEY_INCREMENT_ID, data["incrementId"])
                            }
                        }
                        NOTIFICATION_TYPE_CHAT -> {
                            var accountType: String? = null
                            if (data.containsKey("accountType"))
                                accountType = data["accountType"]
                            val notificationSenderId = data["senderId"]
                            intent = Intent(this, DeliveryChatActivity::class.java)
                            intent.putExtra(KEY_ACCOUNT_TYPE, accountType)
                            intent.putExtra(BUNDLE_KEY_USER_ID, notificationId)
                            if (notificationSenderId != "deliveryAdmin")
                                intent.putExtra(BUNDLE_KEY_USER_NAME, notificationTitle)
                        }
                        NOTIFICATION_TYPE_SELLER_CHAT -> {
                            intent = (applicationContext as MobikulApplication).getSellerChatActivity(this)
                            intent?.putExtra("user_name", data["name"])
                            intent?.putExtra("user_id", data["customerToken"])
                            if (data.containsKey("apiKey")) {
                                data["apiKey"]?.let { AppSharedPref.setApiKey(applicationContext, it) }
                            }
                            if (data.containsKey("tokens")) {
                                intent?.putExtra("token", data["tokens"])
                            }
                        }
                        NOTIFICATION_TYPE_SELLER_ORDER -> {
                            intent = (applicationContext as MobikulApplication).getSellerOrderDetailsActivity(this)
                            intent?.putExtra(BUNDLE_KEY_INCREMENT_ID, data["incrementId"])
                        }
                        NOTIFICATION_TYPE_SELLER_PRODUCT_APPROVAL -> {
                            intent = Intent(this, ProductDetailsActivity::class.java)
                            intent.putExtra(BUNDLE_KEY_PRODUCT_ID, data["productId"])
                            intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, data["productName"])
                        }
                        NOTIFICATION_TYPE_SELLER_APPROVAL -> {
                            if (AppSharedPref.isLoggedIn(this) && AppSharedPref.getCustomerToken(this) == data["sellerId"]) {
                                intent = (applicationContext as MobikulApplication).getSellerDashboardActivity(this)
                            }
                        }
                    }
                }
                if (intent != null) {
                    intent.putExtra(BUNDLE_KEY_NOTIFICATION_ID, notificationId)
                    intent.putExtra(BUNDLE_KEY_FROM_NOTIFICATION, true)
                    sendNotification(notificationType!!, notificationTitle!!, notificationBody!!, notificationBanner, notificationId.toString(), intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param notificationContent FCM message body received.
     * @param notificationId
     * @param intent
     */
    private fun sendNotification(notificationType: String, notificationTitle: String, notificationContent: String, banner: String?, notificationId: String, intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT)

        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder: NotificationCompat.Builder = when (notificationType) {
            NOTIFICATION_TYPE_ORDER ->
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ORDERS)
            else ->
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_OFFERS)
        }

        notificationBuilder.setSmallIcon(R.drawable.ic_status)
        notificationBuilder.setContentTitle(notificationTitle)
        notificationBuilder.setContentText(notificationContent)
        notificationBuilder.setLargeIcon(icon)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        if(AppSharedPref.getNotificationSoundEnabled(this)) {
            notificationBuilder.setSound(defaultSoundUri)
            Log.d("notification_sound", "notification sound enabled")
        }else{
            notificationBuilder.setSound(null)
            notificationBuilder.setDefaults(0)
        notificationBuilder.setSilent(true)
            Log.d("notification_sound", "notification sound disabled")
        }
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.priority =
            if(AppSharedPref.getNotificationSoundEnabled(this)) {
                NotificationCompat.PRIORITY_HIGH
            }else{
                NotificationCompat.PRIORITY_LOW
            }


        try {
            if (banner == null || banner.isEmpty()) {
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle()
                        .bigText(notificationContent)
                        .setBigContentTitle(notificationTitle))
            } else {
                notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeStream(URL(banner).content as InputStream)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId.hashCode()+ System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun addNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val offersNotifications = NotificationChannel(NOTIFICATION_CHANNEL_OFFERS, resources.getString(R.string.offers), NotificationManager.IMPORTANCE_DEFAULT)
            val ordersNotifications = NotificationChannel(NOTIFICATION_CHANNEL_ORDERS, resources.getString(R.string.orders), NotificationManager.IMPORTANCE_DEFAULT)
            val abandonedCartNotifications = NotificationChannel(NOTIFICATION_CHANNEL_ABANDONED_CART, resources.getString(R.string.abandoned_cart), NotificationManager.IMPORTANCE_DEFAULT)

            val channels = ArrayList<NotificationChannel>()
            channels.add(offersNotifications)
            channels.add(ordersNotifications)
            channels.add(abandonedCartNotifications)

            notificationManager.createNotificationChannels(channels)
        }
    }
}