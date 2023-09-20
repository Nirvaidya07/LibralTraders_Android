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

import android.view.View
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.fragments.NotificationBottomSheetFragment


class EmptyFragmentHandler {

    fun onClickContinueShopping(view: View) {
        if (view.context is BaseActivity) {
            val cartBottomSheet = (view.context as BaseActivity).supportFragmentManager.findFragmentByTag(CartBottomSheetFragment::class.java.simpleName)
            val notificationBottomSheet = (view.context as BaseActivity).supportFragmentManager.findFragmentByTag(NotificationBottomSheetFragment::class.java.simpleName)
            if (cartBottomSheet != null && cartBottomSheet is CartBottomSheetFragment) {
                cartBottomSheet.dismiss()
            } else if (notificationBottomSheet != null && notificationBottomSheet is NotificationBottomSheetFragment) {
                notificationBottomSheet.dismiss()
            } else {
                (view.context as BaseActivity).finish()
            }
        }
    }
}