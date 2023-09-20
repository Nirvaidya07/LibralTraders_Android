/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.textfield.TextInputEditText
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.activities.OrderPlacedActivity
import com.webkul.mobikul.fragments.SignUpBottomSheetFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressDetailsData
import com.webkul.mobikul.models.user.SignUpFormModel
import com.webkul.mobikul.models.user.SignUpResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

open class SignUpBottomSheetHandler(val mFragmentContext: SignUpBottomSheetFragment) {

    var mResendTimer: CountDownTimer? = null

    fun onClickCancelBtn() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.prefix)
        mFragmentContext.dismiss()
    }

    fun onClickSignUp(signUpFormModel: SignUpFormModel?) {
        if(signUpFormModel!=null) {
            if (signUpFormModel.isFormValidated(mFragmentContext)) {
                Utils.hideKeyboard(mFragmentContext.mContentViewBinding.prefix)
                mFragmentContext.mContentViewBinding.loading = true
                ApiConnection.signUp(mFragmentContext.requireContext(), signUpFormModel)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<SignUpResponseModel>(
                        mFragmentContext.requireContext(),
                        true
                    ) {
                        override fun onNext(signUpResponseModel: SignUpResponseModel) {
                            super.onNext(signUpResponseModel)
                            mFragmentContext.mContentViewBinding.loading = false
                            ToastHelper.showToast(
                                mFragmentContext.requireContext(),
                                signUpResponseModel.message
                            )
                            if (signUpResponseModel.success) {
                                FirebaseAnalyticsHelper.logSignUpEvent(
                                    signUpResponseModel.customerName,
                                    signUpResponseModel.customerEmail
                                )
                                AppSharedPref.setCustomerCachedNewAddress(
                                    mFragmentContext.requireContext(),
                                    AddressDetailsData()
                                )
                                updateSharedPref(signUpResponseModel)
                                if (mFragmentContext.context is LoginAndSignUpActivity)
                                    (mFragmentContext.context as LoginAndSignUpActivity).finishActivity()
                                else if (mFragmentContext.context is OrderPlacedActivity) {
                                    (mFragmentContext.context as OrderPlacedActivity).onCreateAccountSuccess()
                                    mFragmentContext.dismiss()
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mFragmentContext.mContentViewBinding.loading = false
                            onErrorResponse(e, signUpFormModel)
                        }
                    })
            }
        }
    }

    private fun onErrorResponse(error: Throwable, signUpFormModel: SignUpFormModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.requireContext() as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.requireContext(), error),
                false,
                mFragmentContext.getString(R.string.try_again),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                onClickSignUp(signUpFormModel)
            }
                , mFragmentContext.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    open fun updateSharedPref(signUpResponseModel: SignUpResponseModel) {
        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(mFragmentContext.requireContext(), AppSharedPref.CUSTOMER_PREF)
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
        customerDataSharedPref.apply()
    }

    fun onClickLogin() {
        mFragmentContext.dismiss()
        (mFragmentContext.context as LoginAndSignUpActivity).mContentViewBinding.handler!!.onClickLogin()
    }

    fun onClickDatePicker(v: View, selectedDate: String, dateFormat: String) {
        val dobCalendar = Calendar.getInstance()
        var selectedYear = dobCalendar.get(Calendar.YEAR)
        var selectedMonth = dobCalendar.get(Calendar.MONTH)
        var selectedDay = dobCalendar.get(Calendar.DAY_OF_MONTH)
        try {
            if (selectedDate.isNotBlank()) {
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                val d = sdf.parse(selectedDate)
                dobCalendar.time = d
                selectedYear = dobCalendar.get(Calendar.YEAR)
                selectedMonth = dobCalendar.get(Calendar.MONTH)
                selectedDay = dobCalendar.get(Calendar.DAY_OF_MONTH)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val dateOfBirth = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dobCalendar.set(Calendar.YEAR, year)
            dobCalendar.set(Calendar.MONTH, month)
            dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dob = SimpleDateFormat(dateFormat, Locale.US)
            (v as TextInputEditText).setText(dob.format(dobCalendar.time))
        }

        val datePickerDialog = DatePickerDialog(mFragmentContext.requireContext(), R.style.AlertDialogTheme, dateOfBirth, selectedYear, selectedMonth, selectedDay)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, mFragmentContext.getString(R.string.ok), datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, mFragmentContext.getString(R.string.cancel), datePickerDialog)
        datePickerDialog.show()
    }

    fun onClickConfirm() {

        /* Checking Mobile */

        if (mFragmentContext.mContentViewBinding.data!!.mobile.isNullOrBlank()) {
            mFragmentContext.mContentViewBinding.editMobileNumber.error = mFragmentContext.getString(R.string.mobile) + " " + mFragmentContext.getString(R.string.is_required)
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.editMobileNumber)
            mFragmentContext.mContentViewBinding.editMobileNumber.requestFocus()
        } else if (!android.util.Patterns.PHONE.matcher(mFragmentContext.mContentViewBinding.data!!.mobile!!.trim()).matches() ||  (mFragmentContext.mContentViewBinding.data!!.mobile?:"").trim().length < 7) {
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
            mFragmentContext.mContentViewBinding.verificationPhone = mFragmentContext.mContentViewBinding.data!!.dialCode+(mFragmentContext.mContentViewBinding.data!!.mobile?:"").trim()
            sendOtp()
        }


    }

    private fun sendOtp(resend: Boolean = false) {
        Log.d("testLOG", mFragmentContext?.mContentViewBinding?.verificationPhone?:"")
        mFragmentContext.mContentViewBinding.loading = true
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.otpEt)

        ApiConnection.sendOtp(mFragmentContext.requireContext(),  mFragmentContext.mContentViewBinding.verificationPhone!!,"", "register", resend)
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
    }

    fun  onClickOtpScreen() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.firstName)

    }

    fun onClickResend() {
        sendOtp(true)
    }
}