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

package com.webkul.mobikul.handlers

import android.content.Intent
import android.view.View
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.activities.OtherNotificationActivity
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CATEGORY
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CUSTOM_COLLECTION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_NOTIFICATION_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.models.extra.NotificationList


class NotificationActivityHandler {

    fun onClickNotification(view: View, notificationList: NotificationList) {
        val intent: Intent?
        when (notificationList.notificationType) {
            "product" -> {
                intent = (view.context?.applicationContext as MobikulApplication).getProductDetailsActivity(view.context)
                intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, notificationList.dominantColor)
                intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, notificationList.productName)
                intent.putExtra(BUNDLE_KEY_PRODUCT_ID, notificationList.productId)
                view.context.startActivity(intent)
            }
            "category" -> {
                intent = Intent(view.context, CatalogActivity::class.java)
                intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_CATEGORY)
                intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, notificationList.categoryName)
                intent.putExtra(BUNDLE_KEY_CATALOG_ID, notificationList.categoryId)
                view.context.startActivity(intent)
            }
            "others" -> {
                intent = Intent(view.context, OtherNotificationActivity::class.java)
                intent.putExtra(BUNDLE_KEY_NOTIFICATION_ID, notificationList.id)
                view.context.startActivity(intent)
            }
            "custom" -> {
                intent = Intent(view.context, CatalogActivity::class.java)
                intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_CUSTOM_COLLECTION)
                intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, notificationList.title)
                intent.putExtra(BUNDLE_KEY_CATALOG_ID, notificationList.id)
                view.context.startActivity(intent)
            }
            else -> {
            }
        }
    }
}