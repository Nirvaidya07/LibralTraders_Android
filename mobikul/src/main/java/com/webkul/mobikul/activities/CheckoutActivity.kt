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

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.razorpay.PaymentResultListener
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityCheckoutBinding
import com.webkul.mobikul.fragments.PaymentInfoFragment
import com.webkul.mobikul.fragments.ShippingInfoFragment
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_VIRTUAL_CART
import com.webkul.mobikul.helpers.FirebaseAnalyticsHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.checkout.CheckoutAddressInfoResponseModel

class CheckoutActivity : BaseActivity(), PaymentInfoFragment.OnDetachInterface, PaymentResultListener {

    lateinit var mContentViewBinding: ActivityCheckoutBinding
    var mIsVirtual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout)
        initSupportActionBar()
        startInitialization()
        FirebaseAnalyticsHelper.logCheckoutBeginEvent()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        mIsVirtual = intent.getBooleanExtra(BUNDLE_KEY_IS_VIRTUAL_CART, false)
        if (mIsVirtual) {
            setupPaymentInfoFragment()
        } else {
            setupShippingInfoFragment()
        }
    }

    private fun setupShippingInfoFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, ShippingInfoFragment(), ShippingInfoFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun setupPaymentInfoFragment(shippingMethod: String = "", checkoutAddressData: CheckoutAddressInfoResponseModel? = null) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val paymentInfoFragment = PaymentInfoFragment.newInstance(shippingMethod, checkoutAddressData)

        paymentInfoFragment.setOnDetachInterface(this)
        fragmentTransaction.add(R.id.container, paymentInfoFragment, PaymentInfoFragment::class.java.simpleName)

        if (checkoutAddressData != null)
            fragmentTransaction.addToBackStack(PaymentInfoFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onFragmentDetached() {
        val shippingInfoFragment: ShippingInfoFragment? = supportFragmentManager.findFragmentByTag(ShippingInfoFragment::class.java.simpleName) as? ShippingInfoFragment
        shippingInfoFragment?.let {
            it.callApi()
        }
    }

    override fun onPaymentError(p0: Int, p1: String) {
        mContentViewBinding.loading = false
        Log.d("TEST_LOG",  "$p0 onPaymentError: $p1" )
        ToastHelper.showToast(this, getString(R.string.payment_denied))
    }

    override fun onPaymentSuccess(p0: String) {
        val paymentInfoFragment: PaymentInfoFragment? = supportFragmentManager.findFragmentByTag(PaymentInfoFragment::class.java.simpleName) as? PaymentInfoFragment
        paymentInfoFragment?.mContentViewBinding?.handler?.transactionId=p0
        paymentInfoFragment?.mContentViewBinding?.handler?.status=1
        paymentInfoFragment?.mContentViewBinding?.handler?.placeOrder()
    }

}