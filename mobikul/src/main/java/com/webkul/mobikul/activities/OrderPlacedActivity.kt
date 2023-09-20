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

package com.webkul.mobikul.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityOrderPlacedBinding
import com.webkul.mobikul.handlers.OrderPlaceActivityHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.models.user.AddressDetailsData

class OrderPlacedActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityOrderPlacedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)

        mContentViewBinding.data = intent.getParcelableExtra(BundleKeysHelper.BUNDLE_KEY_SAVE_ORDER_RESPONSE)
        mContentViewBinding.handler = OrderPlaceActivityHandler()

        AppSharedPref.setCustomerCachedNewAddress(this, AddressDetailsData())
    }

    fun onCreateAccountSuccess() {
        mContentViewBinding.msg.text = getString(R.string.thank_you_for_registering)
        mContentViewBinding.email.visibility = View.GONE
        mContentViewBinding.createAccountBtn.visibility = View.GONE
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}