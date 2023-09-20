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

package com.webkul.mobikul.models.homepage

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.webkul.mobikul.BR
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_ABANDONED_CART
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_OFFERS
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_ORDERS
import com.webkul.mobikul.fragments.SettingsBottomSheetFragment
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref


class SettingsBottomSheetModel(val mFragmentContext: SettingsBottomSheetFragment) : BaseObservable() {

    var allNotification: Boolean = AppSharedPref.getNotificationsEnabled(mFragmentContext.requireContext())
        @Bindable get() = field
        set(value) {
            field = value
            AppSharedPref.setNotificationsEnabled(mFragmentContext.requireContext(), field)
            notifyPropertyChanged(BR.allNotification)
        }

    var offersNotification: Boolean = AppSharedPref.getOfferNotificationsEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            if (field) {
                if (mFragmentContext.isNotificationChannelEnabled(NOTIFICATION_CHANNEL_OFFERS)) {
                    AppSharedPref.setOfferNotificationsEnabled(mFragmentContext.requireContext(), field)
                } else {
                    showGoToSettingsDialog()
                    mFragmentContext.mContentViewBinding.offersSwitch.isChecked = false
                }
            } else {
                AppSharedPref.setOfferNotificationsEnabled(mFragmentContext.requireContext(), field)
            }
        }

    var ordersNotification: Boolean = AppSharedPref.getOrderNotificationsEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            if (field) {
                if (mFragmentContext.isNotificationChannelEnabled(NOTIFICATION_CHANNEL_ORDERS)) {
                    AppSharedPref.setOrderNotificationsEnabled(mFragmentContext.requireContext(), field)
                } else {
                    showGoToSettingsDialog()
                    mFragmentContext.mContentViewBinding.ordersSwitch.isChecked = false
                }
            } else {
                AppSharedPref.setOrderNotificationsEnabled(mFragmentContext.requireContext(), field)
            }
        }

    var abandonedCartNotification: Boolean = AppSharedPref.getAbandonedCartNotificationsEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            if (field) {
                if (mFragmentContext.isNotificationChannelEnabled(NOTIFICATION_CHANNEL_ABANDONED_CART)) {
                    AppSharedPref.setAbandonedCartNotificationsEnabled(mFragmentContext.requireContext(), field)
                } else {
                    showGoToSettingsDialog()
                    mFragmentContext.mContentViewBinding.abandonedCartSwitch.isChecked = false
                }
            } else {
                AppSharedPref.setAbandonedCartNotificationsEnabled(mFragmentContext.requireContext(), field)
            }
        }

    var notificationSound: Boolean = AppSharedPref.getNotificationSoundEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            AppSharedPref.setNotificationSoundEnabled(mFragmentContext.requireContext(), field)
        }

    var recentlyViewedProducts: Boolean = AppSharedPref.getRecentlyViewedProductsEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            AppSharedPref.setRecentlyViewedProductsEnabled(mFragmentContext.requireContext(), field)
            if (mFragmentContext.context is HomeActivity) {
                (mFragmentContext.context as HomeActivity).setupRecentlyViewedCarouselsLayout()
            }
        }

    var searchHistory: Boolean = AppSharedPref.getSearchHistoryEnabled(mFragmentContext.requireContext())
        set(value) {
            field = value
            AppSharedPref.setSearchHistoryEnabled(mFragmentContext.requireContext(), field)
        }

    private fun showGoToSettingsDialog() {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.requireContext().getString(R.string.error),
                mFragmentContext.requireContext().getString(R.string.enable_notifications_error),
                false,
                mFragmentContext.requireContext().getString(R.string.go_to_settings),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                abandonedCartNotification = false
                goToSettings()
            },
                mFragmentContext.requireContext().getString(R.string.later),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                abandonedCartNotification = false
            })
    }

    fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", mFragmentContext.requireActivity().packageName, null)
        mFragmentContext.startActivity(intent)
    }
}