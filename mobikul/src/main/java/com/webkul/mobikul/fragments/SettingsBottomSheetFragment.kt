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

package com.webkul.mobikul.fragments

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentSettingsBottomSheetBinding
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_ABANDONED_CART
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_OFFERS
import com.webkul.mobikul.firebase.NOTIFICATION_CHANNEL_ORDERS
import com.webkul.mobikul.handlers.SettingsBottomSheetHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.models.homepage.SettingsBottomSheetModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SettingsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentSettingsBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.handler = SettingsBottomSheetHandler(this)
    }

    fun isNotificationChannelEnabled(@Nullable channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelId)
                return channel?.importance != NotificationManager.IMPORTANCE_NONE
            }
            return false
        } else {
            return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        }
    }

    override fun onResume() {
        super.onResume()
        crossCheckPhoneSettings()
        setLastUpdatedTime()
    }

    private fun crossCheckPhoneSettings() {
        if (!isNotificationChannelEnabled(NOTIFICATION_CHANNEL_ORDERS)) {
            AppSharedPref.setOrderNotificationsEnabled(requireContext(), false)
        }
        if (!isNotificationChannelEnabled(NOTIFICATION_CHANNEL_OFFERS)) {
            AppSharedPref.setOfferNotificationsEnabled(requireContext(), false)
        }
        if (!isNotificationChannelEnabled(NOTIFICATION_CHANNEL_ABANDONED_CART)) {
            AppSharedPref.setAbandonedCartNotificationsEnabled(requireContext(), false)
        }
        mContentViewBinding.data = SettingsBottomSheetModel(this)
    }

    private fun setLastUpdatedTime() {
        val installed = File(requireContext().packageManager.getApplicationInfo(requireContext().packageName, 0).sourceDir).lastModified()
        mContentViewBinding.updatedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(installed))
    }
}