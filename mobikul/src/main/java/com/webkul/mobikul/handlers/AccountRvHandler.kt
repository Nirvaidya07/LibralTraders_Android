package com.webkul.mobikul.handlers

import android.content.Intent
import android.os.Looper
import com.webkul.mobikul.activities.*
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FROM_ORDER_AGAIN
import com.webkul.mobikul.helpers.ConstantsHelper.RC_QR_LOGIN
import com.webkul.mobikul.models.user.AccountRvModel
import com.webkul.mobikul.models.user.AccountRvModel.Companion.ACCOUNT_INFORMATION
import com.webkul.mobikul.models.user.AccountRvModel.Companion.ADDRESS_BOOK
import com.webkul.mobikul.models.user.AccountRvModel.Companion.DASHBOARD
import com.webkul.mobikul.models.user.AccountRvModel.Companion.DOWNLOADABLE_PRODUCTS
import com.webkul.mobikul.models.user.AccountRvModel.Companion.ORDERS
import com.webkul.mobikul.models.user.AccountRvModel.Companion.ORDER_AGAIN
import com.webkul.mobikul.models.user.AccountRvModel.Companion.PRODUCT_REVIEWS
import com.webkul.mobikul.models.user.AccountRvModel.Companion.QR_CODE_LOGIN
import com.webkul.mobikul.models.user.AccountRvModel.Companion.WISH_LIST
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration
import zendesk.chat.ChatEngine
import zendesk.chat.VisitorInfo
import zendesk.messaging.MessagingActivity

/**
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

class AccountRvHandler(val mFragmentContext: NavDrawerStartFragment) {

    fun onClickRvItem(type: Int) {
        mFragmentContext.context?.let { context->
        if (mFragmentContext.isAdded) {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                when (type) {
                    DASHBOARD -> {
                        context.startActivity(Intent(context, DashboardActivity::class.java))
                    }
                    WISH_LIST -> {
                        context.startActivity(Intent(context, MyWishListActivity::class.java))
                    }
                    ORDERS -> {
                        val intent = Intent(context, MyOrdersActivity::class.java)
                        intent.putExtra(BUNDLE_KEY_FROM_ORDER_AGAIN,false)
                        context.startActivity(intent)
                    }
                    ORDER_AGAIN -> {
                        val intent = Intent(context, MyOrdersActivity::class.java)
                        intent.putExtra(BUNDLE_KEY_FROM_ORDER_AGAIN,true)
                        context.startActivity(intent)
                    }
                    DOWNLOADABLE_PRODUCTS -> {
                        context.startActivity(Intent(context, MyDownloadableProductsActivity::class.java))
                    }
                    PRODUCT_REVIEWS -> {
                        context.startActivity(Intent(context, MyReviewListActivity::class.java))
                    }
                    ADDRESS_BOOK -> {
                        context.startActivity(Intent(context, AddressBookActivity::class.java))
                    }
                    ACCOUNT_INFORMATION -> {
                        context.startActivity(Intent(context, AccountInfoActivity::class.java))
                    }
                    QR_CODE_LOGIN -> {
                        (context as BaseActivity).openScanner(RC_QR_LOGIN)
                    }
                    AccountRvModel.ZOPIM_CHAT -> {
                        callZendeskChat()
                    }
                }
            }, 300)
            (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
        }
    }
    }

    fun callZendeskChat(){
        val profileProvider = Chat.INSTANCE.providers()!!.profileProvider()
        val chatProvider = Chat.INSTANCE.providers()!!.chatProvider()

        val visitorInfo = VisitorInfo.builder()
                .withName(mFragmentContext.context?.let { AppSharedPref.getCustomerName(it) })
                .withEmail(mFragmentContext.context?.let { AppSharedPref.getCustomerEmail(it) })
                .build()
        profileProvider.setVisitorInfo(visitorInfo, null)
        //   chatProvider.setDepartment("Department name", null)

        val chatConfiguration = ChatConfiguration.builder()
                .withAgentAvailabilityEnabled(false)
                .withOfflineFormEnabled(true)
                .build()

        MessagingActivity.builder()
                .withEngines(ChatEngine.engine())
                .show(mFragmentContext.requireContext(), chatConfiguration)
    }
}