package com.webkul.mobikul.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityLoginAndSignUpBinding
import com.webkul.mobikul.handlers.LoginAndSignUpActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_LOGIN_OR_REGISTER_PAGE
import com.webkul.mobikul.helpers.ConstantsHelper.OPEN_LOGIN_PAGE
import com.webkul.mobikul.helpers.ConstantsHelper.OPEN_SIGN_UP_PAGE
import com.webkul.mobikul.models.user.SignUpFormModel
import com.webkul.mobikul.models.user.SignUpResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

open class LoginAndSignUpActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityLoginAndSignUpBinding

    // Used only for Social Login
    var mSocialLoginClass: Class<*>? = null
    var mSocialLoginClassInstance: Any? = null

    private var mSocialLoginData: SignUpFormModel = SignUpFormModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_and_sign_up)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        checkForSocialLogin()
        mContentViewBinding.handler = LoginAndSignUpActivityHandler(this)

        if (intent.hasExtra(BUNDLE_KEY_LOGIN_OR_REGISTER_PAGE)) {
            if (intent.getIntExtra(BUNDLE_KEY_LOGIN_OR_REGISTER_PAGE, 0) == OPEN_LOGIN_PAGE) {
                mContentViewBinding.handler?.onClickLogin()
            } else if (intent.getIntExtra(BUNDLE_KEY_LOGIN_OR_REGISTER_PAGE, 0) == OPEN_SIGN_UP_PAGE) {
                mContentViewBinding.handler?.onClickSignUp()
            }
        }
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.sign_in_or_register)
        super.initSupportActionBar()
    }

    private fun checkForSocialLogin() {
        try {
            mSocialLoginClass = Class.forName("com.webkul.mobikul.mobikulsociallogin.MobikulSocialLoginHelper")
            mSocialLoginClassInstance = mSocialLoginClass!!.newInstance()
            val addSocialLoginButtonsMethod = mSocialLoginClass!!.getDeclaredMethod("addSocialLoginButtons", Activity::class.java, Int::class.java)
            addSocialLoginButtonsMethod.invoke(mSocialLoginClassInstance, this, R.id.social_login_container)

            mContentViewBinding.socialLoginDivider.visibility = View.VISIBLE
            mContentViewBinding.socialLoginContainer.visibility = View.VISIBLE
        } catch (x: Exception) {
            x.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val socialLoginOnActivityResult = mSocialLoginClass!!.getDeclaredMethod("onActivityResult", Int::class.java, Int::class.java, Intent::class.java)
            socialLoginOnActivityResult.invoke(mSocialLoginClassInstance, requestCode, resultCode, data)
        } catch (x: Exception) {
            x.printStackTrace()
        }
    }

    open fun socialLoginResponse(socialLoginData: JSONObject?) {
        if (socialLoginData != null) {
            mSocialLoginData.isSocial = 1
            try {
                mSocialLoginData.firstName = socialLoginData.getString("firstName")
                mSocialLoginData.lastName = socialLoginData.getString("lastName")
                mSocialLoginData.emailAddr = socialLoginData.getString("email")
                mSocialLoginData.password = socialLoginData.getString("password")
                mSocialLoginData.pictureURL = socialLoginData.getString("pictureURL")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callSignUpApi()
        }
    }

    private fun callSignUpApi() {
        mContentViewBinding.loading = true
        ApiConnection.signUp(this, mSocialLoginData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<SignUpResponseModel>(this, true) {
                    override fun onNext(signUpResponseModel: SignUpResponseModel) {
                        super.onNext(signUpResponseModel)
                        mContentViewBinding.loading = false
                        ToastHelper.showToast(this@LoginAndSignUpActivity, signUpResponseModel.message)
                        if (signUpResponseModel.success) {
                            FirebaseAnalyticsHelper.logSignUpEvent(signUpResponseModel.customerName, signUpResponseModel.customerEmail)
                            updateSharedPref(signUpResponseModel)
                            finishActivity()
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                NetworkHelper.getErrorMessage(this, error),
                false,
                getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callSignUpApi()
                }
                , getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    open fun updateSharedPref(signUpResponseModel: SignUpResponseModel) {
        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(this, AppSharedPref.CUSTOMER_PREF)
        customerDataSharedPref.putBoolean(AppSharedPref.KEY_LOGGED_IN, true)
        customerDataSharedPref.putInt(AppSharedPref.KEY_QUOTE_ID, 0)
        customerDataSharedPref.putInt(AppSharedPref.KEY_CART_COUNT, signUpResponseModel.cartCount)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_TOKEN, signUpResponseModel.customerToken)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_ID, signUpResponseModel.customerId)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_NAME, signUpResponseModel.customerName)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_EMAIL, signUpResponseModel.customerEmail)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_IMAGE_URL, signUpResponseModel.profileImage)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_IMAGE_DOMINANT_COLOR, signUpResponseModel.profileDominantColor)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_BANNER_URL, signUpResponseModel.bannerImage)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_BANNER_DOMINANT_COLOR, signUpResponseModel.bannerDominantColor)
        try {
            customerDataSharedPref.putBoolean("isSeller", signUpResponseModel.isSeller)
            customerDataSharedPref.putBoolean("isPendingSeller", signUpResponseModel.isPending)
            customerDataSharedPref.putBoolean("isAdmin", signUpResponseModel.isAdmin)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        customerDataSharedPref.apply()
    }

    fun finishActivity() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}