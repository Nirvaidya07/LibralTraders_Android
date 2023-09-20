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

import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.activities.OrderPlacedActivity
import com.webkul.mobikul.adapters.CountryCodeSpinnerAdapter
import com.webkul.mobikul.databinding.FragmentSignupBottomSheetBinding
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_KEYBOARD_OBSERVER
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.CountryCodeListItem
import com.webkul.mobikul.models.CountryCodeListingResponseModel
import com.webkul.mobikul.models.checkout.SaveOrderResponseModel
import com.webkul.mobikul.models.user.SignUpFormModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import java.util.regex.Pattern


class SignUpBottomSheetFragment : FullScreenBottomSheetDialogFragment(), MessageListener {

    lateinit var mContentViewBinding: FragmentSignupBottomSheetBinding

    companion object {
        fun newInstance(saveOrderResponseModel: SaveOrderResponseModel): SignUpBottomSheetFragment {
            val signUpBottomSheetFragment = SignUpBottomSheetFragment()
            val args = Bundle()
            args.putParcelable(BundleKeysHelper.BUNDLE_KEY_SAVE_ORDER_RESPONSE, saveOrderResponseModel)
            signUpBottomSheetFragment.arguments = args
            return signUpBottomSheetFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mContentViewBinding.handler = (context?.applicationContext as MobikulApplication).getSignUpBottomSheetHandler(this)

       callCreateFormApi()
        // Remove Space FromShop URL #556
        mContentViewBinding.shopUrlEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                val textEntered =  mContentViewBinding.shopUrlEt.text.toString()

                if (textEntered.isNotEmpty() && textEntered.contains(" ")) {
                    mContentViewBinding.shopUrlEt.setText( mContentViewBinding.shopUrlEt.text.toString().replace(" ", ""));
                    mContentViewBinding.shopUrlEt.setSelection( mContentViewBinding.shopUrlEt.text!!.length);
                }
            }})
    }

    private fun callCreateFormApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("createAccountFormData" + AppSharedPref.getStoreId(requireContext()))
        ApiConnection.createAccountFormData(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<SignUpFormModel>(requireContext(), false) {
                    override fun onNext(signUpFormModel: SignUpFormModel) {
                        super.onNext(signUpFormModel)
                        mContentViewBinding.loading = false
                        if (signUpFormModel.success) {
                            onSuccessfulResponse(signUpFormModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(signUpFormModel: SignUpFormModel) {
        if (AppSharedPref.getIsMobileLoginEnabled(requireContext())) {
            mContentViewBinding.otpLoginLayout.visibility = View.VISIBLE
            mContentViewBinding.createAccountForm.visibility = View.GONE
            callCountryCodeApi()
            setUpOTPVerification()
        } else {
            mContentViewBinding.createAccountForm.visibility = View.VISIBLE
        }
        setupFormData(signUpFormModel)

    }

    private fun setupFormData(signUpFormModel: SignUpFormModel) {
        mContentViewBinding.data = signUpFormModel
        checkForPreSetData()
        setUpPrefix()
        setUpSuffix()
        setUpGender()
        setupPasswordTextWatcher()

        if (ENABLE_KEYBOARD_OBSERVER)
            attachKeyboardListeners()
    }

    private fun checkForPreSetData() {
        if (arguments != null) {
            val saveOrderResponseModel = requireArguments().getParcelable<SaveOrderResponseModel>(BundleKeysHelper.BUNDLE_KEY_SAVE_ORDER_RESPONSE)?:SaveOrderResponseModel()
            mContentViewBinding.data!!.prefix = saveOrderResponseModel.customerDetails?.prefix
            mContentViewBinding.data!!.firstName = saveOrderResponseModel.customerDetails?.firstname
            mContentViewBinding.data!!.lastName = saveOrderResponseModel.customerDetails?.lastname
            mContentViewBinding.data!!.suffix = saveOrderResponseModel.customerDetails?.suffix
            mContentViewBinding.data!!.emailAddr = saveOrderResponseModel.customerDetails?.email
            mContentViewBinding.data!!.orderId=saveOrderResponseModel.orderId
            mContentViewBinding.gotoSignInLayout.visibility = View.GONE
        }
    }

    private fun setUpPrefix() {
        if (mContentViewBinding.data!!.isPrefixVisible && mContentViewBinding.data!!.prefixHasOptions) {
            val prefixSpinnerData = ArrayList<String>()
            prefixSpinnerData.add(getString(R.string.select_prefix))
            for (prefixIterator in 0 until mContentViewBinding.data!!.prefixOptions.size) {
                prefixSpinnerData.add(mContentViewBinding.data!!.prefixOptions[prefixIterator])
            }

            mContentViewBinding.prefixSp.adapter = context?.let { ArrayAdapter<String>(it, R.layout.custom_spinner_item, prefixSpinnerData) }
            mContentViewBinding.prefixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mContentViewBinding.data!!.prefix = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            mContentViewBinding.prefixSp.setSelection(mContentViewBinding.data!!.getPrefixPosition())
        }
    }

    private fun setUpSuffix() {
        if (mContentViewBinding.data!!.isSuffixVisible && mContentViewBinding.data!!.suffixHasOptions) {
            val suffixSpinnerData = ArrayList<String>()
            suffixSpinnerData.add(getString(R.string.select_suffix))
            for (prefixIterator in 0 until mContentViewBinding.data!!.suffixOptions.size) {
                suffixSpinnerData.add(mContentViewBinding.data!!.suffixOptions[prefixIterator])
            }

            mContentViewBinding.suffixSp.adapter = context?.let { ArrayAdapter<String>(it, R.layout.custom_spinner_item, suffixSpinnerData) }
            mContentViewBinding.suffixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mContentViewBinding.data!!.suffix = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            mContentViewBinding.suffixSp.setSelection(mContentViewBinding.data!!.getSuffixPosition())
        }
    }

        private fun setUpGender() {
        if (mContentViewBinding.data!!.isGenderVisible) {
            val genderSpinnerData = ArrayList<String>()
            genderSpinnerData.add(getString(R.string.select_gender))
            genderSpinnerData.add(resources.getString(R.string.male))
            genderSpinnerData.add(resources.getString(R.string.female))


            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.custom_spinner_item,
                    genderSpinnerData)

            mContentViewBinding.genderSp.setAdapter(adapter)

            mContentViewBinding.genderSp.onItemClickListener = AdapterView.OnItemClickListener { parent, arg1, position, id ->
                mContentViewBinding.data!!.gender = position
            }
        }
    }

    private fun setupPasswordTextWatcher() {

        mContentViewBinding.password.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.toString().length < 8) {
                    mContentViewBinding.passValidImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_dot_grey))
                    mContentViewBinding.passLengthValidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_secondary))
                } else {
                    mContentViewBinding.passValidImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_dot_green))
                    mContentViewBinding.passLengthValidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.five_star_color))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun onErrorResponse(error: Throwable) {
        if (!NetworkHelper.isNetworkAvailable(requireContext()) || (error is HttpException && error.code() == 304)) {
            loadOfflineData()
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    context as BaseActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(requireContext(), error),
                    false,
                    getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mContentViewBinding.loading = true
                    callCreateFormApi()
                }
                    , getString(R.string.dismiss)
                    , { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
        }
    }

    private fun loadOfflineData() {
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread((context as BaseActivity).mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotEmpty()) {
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, SignUpFormModel::class.java))
                        }

                    }
                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        (context as BaseActivity).mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (keyboardListenersAttached) {
            mContentViewBinding.signUpBottomSheet.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff: Int
        if (context is LoginAndSignUpActivity)
            heightDiff = Utils.screenHeight - (context as LoginAndSignUpActivity).mContentViewBinding.root.height
        else
            heightDiff = Utils.screenHeight - (context as OrderPlacedActivity).mContentViewBinding.root.height
        if (heightDiff < 200) {
            onHideKeyboard()
        } else {
            onShowKeyboard(heightDiff)
        }
    }

    private var keyboardListenersAttached = false

    protected fun onShowKeyboard(keyboardHeight: Int) {
        val layoutParams = mContentViewBinding.keyboardHeightLayout.layoutParams
        layoutParams.height = keyboardHeight
        mContentViewBinding.keyboardHeightLayout.layoutParams = layoutParams
    }

    protected fun onHideKeyboard() {
        val layoutParams = mContentViewBinding.keyboardHeightLayout.layoutParams
        layoutParams.height = 0
        mContentViewBinding.keyboardHeightLayout.layoutParams = layoutParams
    }

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        mContentViewBinding.signUpBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }

    private fun callCountryCodeApi() {
        mContentViewBinding.loading = true
        ApiConnection.countryCodeApi(requireContext()!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CountryCodeListingResponseModel>(requireContext()!!, true) {
                    override fun onNext(countryCodeResponseModel: CountryCodeListingResponseModel) {
                        super.onNext(countryCodeResponseModel)
                        mContentViewBinding.loading = false
                        ToastHelper.showToast(context, countryCodeResponseModel.message)
                        if (countryCodeResponseModel.success) {
                            setupDialCodeData(countryCodeResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                    }
                })
    }

    private fun setupDialCodeData(jsonResponse: CountryCodeListingResponseModel) {

        try {
            val customDropDownAdapter = jsonResponse.country_code?.let { CountryCodeSpinnerAdapter(context!!.applicationContext, it) }
            mContentViewBinding.spinnerCountryCode.adapter = customDropDownAdapter

            setDefaultCountryCodeSelection(jsonResponse.country_code)

            mContentViewBinding.spinnerCountryCode.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mContentViewBinding.data!!.dialCode = jsonResponse.country_code!![position].dial_code!!


                    if (mContentViewBinding.data!!.dialCode == "+91") {
                        mContentViewBinding.editMobileNumber.filters = arrayOf(InputFilter.LengthFilter(10))
                    } else if (mContentViewBinding.data!!.dialCode == "+965") {
                        mContentViewBinding.editMobileNumber.filters = arrayOf(InputFilter.LengthFilter(8))
                    } else {
                        mContentViewBinding.editMobileNumber.filters = arrayOf(InputFilter.LengthFilter(15))
                    }

                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun setDefaultCountryCodeSelection(countryCodeData: ArrayList<CountryCodeListItem>?) {
        var countryCodePosition = 0
        for (countrycodeIterator in 0 until countryCodeData?.size!!) {
            if (countryCodeData[countrycodeIterator].dial_code == "+91") {
                countryCodePosition = countrycodeIterator
                mContentViewBinding.editMobileNumber.filters = arrayOf(InputFilter.LengthFilter(10))
                break
            } else {
                mContentViewBinding.editMobileNumber.filters = arrayOf(InputFilter.LengthFilter(12))
            }
        }

        mContentViewBinding.spinnerCountryCode.setSelection(countryCodePosition)
    }

    private fun setUpOTPVerification() {
        mContentViewBinding.resendTv.paintFlags = mContentViewBinding.resendTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mContentViewBinding.resendBtn.paintFlags = mContentViewBinding.resendTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        var otpHint = ""
        for (i in 0 until AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()) {
            otpHint += "*"
        }
        mContentViewBinding.otpEt.hint = otpHint


        mContentViewBinding.otpEt.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()))
        mContentViewBinding.otpEt.addTextChangedListener(GenericTextWatcher(mContentViewBinding.otpEt))
    }

    override fun messageReceived(message: String?) {
        if (message != null) {
            var otpRegx = ""
            for (i in 0 until AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()) {
                otpRegx += "\\d"
            }
            val m = Pattern.compile(otpRegx).matcher(message)
            while (m.find()) {
                mContentViewBinding.otpEt.setText(m.group().trim())
                break
            }
        }
    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {

        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            if (text.length == AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()) {
                verifyOtp(mContentViewBinding.otpEt.text.toString())
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }
    }

    private fun verifyOtp(otp: String) {
        if (otp.length == AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()) {
            mContentViewBinding.loading = true
            Utils.hideKeyboard(mContentViewBinding.otpEt)
            ApiConnection.verifyOtp(context!!, mContentViewBinding.verificationPhone!!.trim(), "", otp, "register")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(context!!, true) {
                        override fun onNext(responseModel: BaseModel) {
                            super.onNext(responseModel)
                            mContentViewBinding.loading = false
                            if (responseModel.success) {
                                mContentViewBinding.data?.mobile = mContentViewBinding.verificationPhone
                                mContentViewBinding.mobile.visibility = View.GONE
                                mContentViewBinding.otpLoginLayout.visibility = View.GONE
                                mContentViewBinding.createAccountForm.visibility = View.VISIBLE

                            } else {
                                ToastHelper.showToast(context, responseModel.message)
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContentViewBinding.loading = false
                            onErrorResponse(e, false)
                        }
                    })
        }
    }

    private fun onErrorResponse(error: Throwable, verifyOtp: Boolean) {
        AlertDialogHelper.showNewCustomDialog(
                context as LoginAndSignUpActivity,
                getString(R.string.error),
                NetworkHelper.getErrorMessage(context, error),
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    verifyOtp(mContentViewBinding.otpEt.text.toString())
                }
                , getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }
}