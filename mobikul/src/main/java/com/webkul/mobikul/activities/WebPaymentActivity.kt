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

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityWebPaymentBinding
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CANCEL_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FAILURE_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_START_PAYMENT_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_SUCCESS_URL
import com.webkul.mobikul.helpers.ToastHelper
import java.util.*

class WebPaymentActivity : BaseActivity() {

    private lateinit var mBinding: ActivityWebPaymentBinding

    private var mMakePaymentUrl: String? = null
    private var mPaymentSuccessUrl: ArrayList<String> = ArrayList()
    private var mPaymentCancelUrl: ArrayList<String> = ArrayList()
    private var mPaymentFailureUrl: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_payment)
        startInitialization()
        setupWebView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        getUrlData()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_web_payment)
        super.initSupportActionBar()
    }

    private fun getUrlData() {
        mMakePaymentUrl = intent.extras?.getString(BUNDLE_KEY_START_PAYMENT_URL)
        mPaymentSuccessUrl = intent?.extras?.getStringArrayList(BUNDLE_KEY_SUCCESS_URL) as ArrayList<String>
        mPaymentCancelUrl = intent?.extras?.getStringArrayList(BUNDLE_KEY_CANCEL_URL) as ArrayList<String>
        mPaymentFailureUrl = intent?.extras?.getStringArrayList(BUNDLE_KEY_FAILURE_URL) as ArrayList<String>
    }

    private fun setupWebView() {
        mBinding.paymentWebview.requestFocus(View.FOCUS_DOWN)
        mBinding.paymentWebview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, authorizationUrl: String): Boolean {
                Log.d("MAKE_PAYMENT", "shouldOverrideUrlLoading: $authorizationUrl")
                if (mPaymentSuccessUrl.contains(authorizationUrl)) {
                    Log.d("MAKE_PAYMENT", "shouldOverrideUrlLoading: Payment Successful")
                    setResultDataOkay()
                    return true
                }
                if (mPaymentCancelUrl.contains(authorizationUrl) || mPaymentFailureUrl.contains(authorizationUrl)) {
                    Log.d("MAKE_PAYMENT", "shouldOverrideUrlLoading: Payment Denied")
                    setResultDataCancel()
                    return true
                }
                return false
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null)
        } else {
            CookieManager.getInstance().removeAllCookie()
        }

        mMakePaymentUrl?.let { mBinding.paymentWebview.loadUrl(it) }
        mBinding.paymentWebview.settings.javaScriptEnabled = true
//        mBinding.paymentWebview.settings.builtInZoomControls = true;
//        mBinding.paymentWebview.settings.userAgentString = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        mBinding.paymentWebview.settings.domStorageEnabled = true
    }

    private fun setResultDataOkay() {
        ToastHelper.showToast(this, getString(R.string.payment_successful))
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun setResultDataCancel() {
        ToastHelper.showToast(this, getString(R.string.payment_denied))
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialogHelper.showNewCustomDialog(this
                , getString(R.string.cancel_payment)
                , getString(R.string.cancel_payment_alert_msg)
                , true
                , getString(R.string.cancel_payment),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    setResultDataCancel()
                }
                , getString(R.string.dismiss))

    }
}