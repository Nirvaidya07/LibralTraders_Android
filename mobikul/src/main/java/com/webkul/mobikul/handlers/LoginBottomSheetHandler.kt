package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.fragments.ForgotPasswordDialogFragment
import com.webkul.mobikul.fragments.LoginBottomSheetFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.AppSharedPref.Companion.CUSTOMER_PREF
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CART_COUNT
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_BANNER_DOMINANT_COLOR
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_BANNER_URL
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_EMAIL
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_ID
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_IMAGE_DOMINANT_COLOR
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_IMAGE_URL
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_NAME
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_TOKEN
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_LOGGED_IN
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_QUOTE_ID
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressDetailsData
import com.webkul.mobikul.models.user.LoginFormModel
import com.webkul.mobikul.models.user.LoginResponseModel
import com.webkul.mobikul.models.user.SignUpFormModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

open class LoginBottomSheetHandler(val mFragmentContext: LoginBottomSheetFragment) {
    var mResendTimer: CountDownTimer? = null

    fun onClickCancelBtn() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.emailTil)
        mFragmentContext.dismiss()
    }

    fun onClickBackClick() {
        mFragmentContext.mContentViewBinding.loading = false
        mFragmentContext.mContentViewBinding.editMobileNumber.isEnabled = true
        mFragmentContext.mContentViewBinding.mobileLayout.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.otpLayout.visibility = View.GONE
        mFragmentContext.mContentViewBinding.ivCancel.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.ivBack.visibility = View.GONE
    }

    fun onClickLogin(loginFormModel: LoginFormModel) {
        if (loginFormModel.isFormValidated(mFragmentContext)) {
            Utils.hideKeyboard(mFragmentContext.mContentViewBinding.emailTil)
            login(loginFormModel)
        }
    }

    fun login(loginFormModel: LoginFormModel) {
        mFragmentContext.mContentViewBinding.loading = true
        Log.d("TAG", "login: "+loginFormModel)
        ApiConnection.login(mFragmentContext.context!!, loginFormModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<LoginResponseModel>(mFragmentContext.context!!, true) {
                    override fun onNext(loginResponseModel: LoginResponseModel) {
                        super.onNext(loginResponseModel)
                        Log.d("TAG", "onNext:token "+loginResponseModel.customerToken)
                        mFragmentContext.mContentViewBinding.loading = false
                        if (loginResponseModel.success) {
                            checkFingerprintData(loginFormModel.isFingerPrintEnable(mFragmentContext), loginResponseModel, loginFormModel.username, loginFormModel.password)
                        } else {
                            ToastHelper.showToast(mFragmentContext.context!!, loginResponseModel.message)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mFragmentContext.mContentViewBinding.loading = false
                        onErrorResponse(e, loginFormModel)
                    }
                })
    }

    open fun updateSharedPref(loginResponseModel: LoginResponseModel) {
        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(mFragmentContext.context!!, CUSTOMER_PREF)
        customerDataSharedPref.putBoolean(KEY_LOGGED_IN, true)
        customerDataSharedPref.putInt(KEY_QUOTE_ID, 0)
        customerDataSharedPref.putInt(KEY_CART_COUNT, loginResponseModel.cartCount)
        customerDataSharedPref.putString(KEY_CUSTOMER_TOKEN, loginResponseModel.customerToken)
        customerDataSharedPref.putString(KEY_CUSTOMER_ID, loginResponseModel.customerId)
        customerDataSharedPref.putString(KEY_CUSTOMER_NAME, loginResponseModel.customerName)
        customerDataSharedPref.putString(KEY_CUSTOMER_EMAIL, loginResponseModel.customerEmail)
        customerDataSharedPref.putString(KEY_CUSTOMER_IMAGE_URL, loginResponseModel.profileImage)
        customerDataSharedPref.putString(KEY_CUSTOMER_IMAGE_DOMINANT_COLOR, loginResponseModel.profileDominantColor)
        customerDataSharedPref.putString(KEY_CUSTOMER_BANNER_URL, loginResponseModel.bannerImage)
        customerDataSharedPref.putString(KEY_CUSTOMER_BANNER_DOMINANT_COLOR, loginResponseModel.bannerDominantColor)
        customerDataSharedPref.apply()
    }

    private fun onErrorResponse(error: Throwable, loginFormModel: LoginFormModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.context!!, error),
                false,
                mFragmentContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickLogin(loginFormModel)
                }
                , mFragmentContext.getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun onClickOtpLogin() {
        mFragmentContext.mContentViewBinding.otpLogin = true
        mFragmentContext.mContentViewBinding.otpLoginLayout.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.loginLayout.visibility = View.GONE
        mFragmentContext.mContentViewBinding.loginTitleTv.text = mFragmentContext.getString(R.string.sign_in_with_mobile)
    }

    fun onClickEmailLogin() {
        mFragmentContext.mContentViewBinding.otpLogin = false
        mFragmentContext.mContentViewBinding.otpLoginLayout.visibility = View.GONE
        mFragmentContext.mContentViewBinding.loginLayout.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.loginTitleTv.text = mFragmentContext.getString(R.string.sign_in_with_email)
    }

    fun onClickSignUp() {
        mFragmentContext.dismiss()
        (mFragmentContext.context as LoginAndSignUpActivity).mContentViewBinding.handler!!.onClickSignUp()
    }

    fun onClickForgotPassword(email: String) {
        ForgotPasswordDialogFragment.newInstance(email).show(mFragmentContext.childFragmentManager, ForgotPasswordDialogFragment::class.java.simpleName)
    }


    private fun checkFingerprintData(isFingerPrintEnable: Boolean, loginResponseModel: LoginResponseModel, userName: String, userPassword: String) {
        if (isFingerPrintEnable) {
            if (AppSharedPref.getCustomerFingerUserName(mFragmentContext.context!!).isBlank() || AppSharedPref.getCustomerFingerPassword(mFragmentContext.context!!).isBlank()) {
                AlertDialogHelper.showNewCustomDialog(mFragmentContext.context as BaseActivity?
                        , mFragmentContext.context?.getString(R.string.finger_print_login)
                        , mFragmentContext.context?.getString(R.string.finger_print_login_set_msg)
                        , false
                        , mFragmentContext.context?.getString(R.string.yes)
                        , DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    setFingerprintData(userName, userPassword)
                    goToDashboard(loginResponseModel)
                }
                        , mFragmentContext.context?.getString(R.string.no)
                        , DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    goToDashboard(loginResponseModel)
                })
            } else if (AppSharedPref.getCustomerFingerUserName(mFragmentContext.context!!) != userName || AppSharedPref.getCustomerFingerPassword(mFragmentContext.context!!) != userPassword) {
                AlertDialogHelper.showNewCustomDialog(mFragmentContext.context as BaseActivity?
                        , mFragmentContext.context?.getString(R.string.finger_print_login)
                        , mFragmentContext.context?.getString(R.string.finger_print_login_update_msg)
                        , false
                        , mFragmentContext.context?.getString(R.string.yes)
                        , DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    setFingerprintData(userName, userPassword)
                    goToDashboard(loginResponseModel)
                }
                        , mFragmentContext.context?.getString(R.string.no)
                        , DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    goToDashboard(loginResponseModel)
                })
            } else {
                goToDashboard(loginResponseModel)
            }
        } else {
            goToDashboard(loginResponseModel)
        }
    }

    private fun goToDashboard(loginResponseModel: LoginResponseModel) {
        FirebaseAnalyticsHelper.logLoginEvent(loginResponseModel.customerName)
        ToastHelper.showToast(mFragmentContext.context!!, mFragmentContext.getString(R.string.logged_in))
        updateSharedPref(loginResponseModel)
        AppSharedPref.setCustomerCachedNewAddress(mFragmentContext.context!!, AddressDetailsData())
        (mFragmentContext.context as LoginAndSignUpActivity).finishActivity()
    }

    private fun setFingerprintData(userName: String, userPassword: String) {
        mFragmentContext.context?.let { AppSharedPref.setCustomerFingerUserName(it, userName) }
        mFragmentContext.context?.let { AppSharedPref.setCustomerFingerPassword(it, userPassword) }
    }

    fun onClickConfirm() {

        /* Checking Mobile */

        if (mFragmentContext.mContentViewBinding.data!!.mobile.isNullOrBlank()) {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = mFragmentContext.getString(R.string.mobile) + " " + mFragmentContext.getString(R.string.is_required)
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.editMobileNumber)
            mFragmentContext.mContentViewBinding.editMobileNumber.requestFocus()
        } else if (!android.util.Patterns.PHONE.matcher(mFragmentContext.mContentViewBinding.data!!.mobile!!.trim()).matches() ||  mFragmentContext.mContentViewBinding.data!!.mobile.trim().length < 10) {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = mFragmentContext.getString(R.string.enter_a_valid) + " " + mFragmentContext.getString(R.string.mobile)
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.editMobileNumber)
            mFragmentContext.mContentViewBinding.editMobileNumber.requestFocus()
        } else if (mFragmentContext.mContentViewBinding.data!!.dialCode == "+91" &&  (mFragmentContext.mContentViewBinding.data!!.mobile?:"").trim().length != 10) {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = mFragmentContext.getString(R.string.enter_a_valid) + " " + mFragmentContext.getString(R.string.mobile)
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.editMobileNumber)
            mFragmentContext.mContentViewBinding.editMobileNumber.requestFocus()
        } else if (mFragmentContext.mContentViewBinding.data!!.dialCode == "+965" &&  (mFragmentContext.mContentViewBinding.data!!.mobile?:"").trim().length != 8) {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = mFragmentContext.getString(R.string.enter_a_valid) + " " + mFragmentContext.getString(R.string.mobile)
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.editMobileNumber)
            mFragmentContext.mContentViewBinding.editMobileNumber.requestFocus()
        } else {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = null
            mFragmentContext.mContentViewBinding.verificationPhone = mFragmentContext.mContentViewBinding.data!!.dialCode+mFragmentContext.mContentViewBinding.data!!.mobile.trim()
            sendOtp()
        }


    }

    private fun sendOtp(resend: Boolean = false) {
        Log.d("testLOG", mFragmentContext?.mContentViewBinding?.verificationPhone?:"")
        mFragmentContext.mContentViewBinding.loading = true
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.otpEt)

        ApiConnection.sendOtp(mFragmentContext.requireContext(),  mFragmentContext.mContentViewBinding.verificationPhone!!,mFragmentContext.mContentViewBinding.data!!.email, "login", resend)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                    override fun onNext(responseModel: BaseModel) {
                        super.onNext(responseModel)
                        mFragmentContext.mContentViewBinding.loading = false
                        ToastHelper.showToast(mFragmentContext.requireContext(), responseModel.message)
                        if (responseModel.success) {
                            setupOtpLayout()
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mFragmentContext.mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    fun setupOtpLayout() {
        mFragmentContext.mContentViewBinding.loading = false
        mFragmentContext.mContentViewBinding.editMobileNumber.isEnabled = false
        mFragmentContext.mContentViewBinding.mobileLayout.visibility = View.GONE
        mFragmentContext.mContentViewBinding.otpLayout.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.ivCancel.visibility = View.GONE
        mFragmentContext.mContentViewBinding.ivBack.visibility = View.VISIBLE
        startTimer(ApplicationConstants.DEFAULT_OTP_RESEND_TIME_IN_MIN)
        setupSMSRetriever()
    }

    private fun setupSMSRetriever() {
        val client = SmsRetriever.getClient(mFragmentContext.activity!!)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d("DEBUG", "SMS retriever is working now")
//            SmsListener.bindListener(mFragmentContext!!)
        }

        task.addOnFailureListener {
            Log.d("DEBUG", "SMS retriever is not working")
        }
    }

    private fun startTimer(finishInMins: Int) {
        if (mResendTimer != null) {
            mResendTimer!!.cancel()
        }
        mResendTimer = object : CountDownTimer(finishInMins.toLong() * 60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (60 * 1000) % 60
                val seconds = millisUntilFinished / 1000 % 60
                mFragmentContext.mContentViewBinding.resend = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                mFragmentContext.mContentViewBinding.resend = ""
                cancel()
            }
        }.start()
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.requireContext(), error),
                false,
                mFragmentContext.getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickConfirm()
                }
                , mFragmentContext.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun onClickMobile() {
        mFragmentContext.mContentViewBinding.editMobileNumber.isEnabled = true
        mFragmentContext.mContentViewBinding.otpLayout.visibility = View.GONE
        mFragmentContext.mContentViewBinding.mobileLayout.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.ivCancel.visibility = View.VISIBLE
        mFragmentContext.mContentViewBinding.ivBack.visibility = View.GONE
    }

    fun onClickResend() {
        sendOtp(true)
    }

    fun onCLickSubmitOTP() {
        if(mFragmentContext.mContentViewBinding.otpEt.text != null && mFragmentContext.mContentViewBinding.otpEt.text?.length != 4) {
            ToastHelper.showToast(mFragmentContext.requireContext(), "Please enter valid OTP.")
        } else {
            mFragmentContext.verifyOtp(mFragmentContext.mContentViewBinding.otpEt.text.toString())
        }
    }

    fun  onClickOtpScreen() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.emailTil)

    }

}