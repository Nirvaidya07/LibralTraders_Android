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

package com.webkul.mobikul.broadcast_receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.libraltraders.android.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_ABANDONED_CART
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.LocaleUtils


class AbandonedCartAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 12345
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            LocaleUtils.updateConfig(context)

            val cartIntent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            cartIntent.putExtra(BundleKeysHelper.BUNDLE_KEY_OPEN_CART, true)

            val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, cartIntent, PendingIntent.FLAG_ONE_SHOT)

            val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ABANDONED_CART)
                    .setSmallIcon(R.drawable.ic_status)
                    .setContentTitle(context.getString(R.string.your_cart_misses_you))
                    .setContentText(context.getString(R.string.products_waiting_in_cart_msg))
                    .setLargeIcon(icon)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationBuilder.setStyle(NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.products_waiting_in_cart_msg))
                    .setBigContentTitle(context.getString(R.string.your_cart_misses_you)))

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(34555, notificationBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}