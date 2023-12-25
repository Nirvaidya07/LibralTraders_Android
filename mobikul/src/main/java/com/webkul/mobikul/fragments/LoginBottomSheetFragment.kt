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

package com.webkul.mobikul.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Paint
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.databinding.FragmentLoginBottomSheetBinding
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_KEYBOARD_OBSERVER
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_LOGIN_EMAIL_ID
import com.webkul.mobikul.models.CountryCodeListingResponseModel
import com.webkul.mobikul.models.user.LoginFormModel
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import com.fasterxml.jackson.databind.ObjectMapper
import com.webkul.mobikul.adapters.CountryCodeSpinnerAdapter
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.CountryCodeListItem
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStream
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.regex.Pattern


class LoginBottomSheetFragment : FullScreenBottomSheetDialogFragment(), MessageListener {

    lateinit var mContentViewBinding: FragmentLoginBottomSheetBinding

    private val KEY_NAME = "key"
    private lateinit var mCancellationSignal: CancellationSignal
    private var mCipher: Cipher? = null
    private var mKeyStore: KeyStore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       if (AppSharedPref.getIsMobileLoginEnabled(context!!)) {
           setUpOTPVerification()
       }
        setupFormData()
        setupFingerPrintLogin()
    }

    private fun setupFormData() {
        val loginFormModel = LoginFormModel()
        loginFormModel.username = ApplicationConstants.DEMO_USERNAME
        loginFormModel.password = ApplicationConstants.DEMO_PASSWORD

        if (AppSharedPref.getIsMobileLoginEnabled(requireContext())) {
            mContentViewBinding.otpLogin = true
            mContentViewBinding.otpLoginLayout.visibility = View.VISIBLE
            mContentViewBinding.loginLayout.visibility = View.GONE
            mContentViewBinding.loginTitleTv.text = getString(R.string.sign_in_with_mobile)
            callCountryCodeApi()
        } else {
            mContentViewBinding.signInWithMobileBt.visibility = View.GONE
        }

        if((requireActivity() as LoginAndSignUpActivity).intent?.hasExtra(BUNDLE_KEY_LOGIN_EMAIL_ID)==true){
            loginFormModel.username=(requireActivity() as LoginAndSignUpActivity).intent?.getStringExtra(BUNDLE_KEY_LOGIN_EMAIL_ID)?:""
            loginFormModel.password = ""
        }

        mContentViewBinding.data = loginFormModel
        mContentViewBinding.handler = (context?.applicationContext as MobikulApplication).getLoginBottomSheetHandler(this)

        if (ENABLE_KEYBOARD_OBSERVER)
            attachKeyboardListeners()
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
//            val myJsonFile: InputStream = this.activity!!.assets.open("dial_code_data.json")
//            val size: Int = myJsonFile.available()
//            val buffer = ByteArray(size)
//            myJsonFile.read(buffer)
//            myJsonFile.close()
//            val myJsonData = String(buffer, Charset.defaultCharset())
//            val jsonResponse: CountryCodeListingResponseModel = ObjectMapper().readValue(myJsonData, CountryCodeListingResponseModel::class.java)

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
       // mContentViewBinding.otpEt.addTextChangedListener(GenericTextWatcher(mContentViewBinding.otpEt))
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



    override fun onDestroy() {
        super.onDestroy()
        if (keyboardListenersAttached) {
            mContentViewBinding.loginBottomSheet.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = Utils.screenHeight - (context as LoginAndSignUpActivity).mContentViewBinding.root.height
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

        mContentViewBinding.loginBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }


    private fun setupFingerPrintLogin() {
        if (mContentViewBinding.data!!.isFingerPrintEnable(this) && !AppSharedPref.getCustomerFingerUserName(requireContext()).isBlank() && !AppSharedPref.getCustomerFingerPassword(requireContext()).isBlank()) {
            val keyguardManager = requireContext().getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
            val fingerprintManager: FingerprintManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                fingerprintManager = requireContext().getSystemService(AppCompatActivity.FINGERPRINT_SERVICE) as FingerprintManager
                if (!keyguardManager.isKeyguardSecure) {
                    mContentViewBinding.fingerPrintIv.visibility = View.GONE
                    mContentViewBinding.fingerprintErrorTv.visibility = View.VISIBLE
                    mContentViewBinding.fingerprintErrorTv.text = getString(R.string.screen_lock_not_enabled)
                    return
                }
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    mContentViewBinding.fingerPrintIv.visibility = View.GONE
                    mContentViewBinding.fingerprintErrorTv.visibility = View.VISIBLE
                    mContentViewBinding.fingerprintErrorTv.text = getString(R.string.fingerprint_authentication_permission_not_enabled)
                    return
                }
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    mContentViewBinding.fingerPrintIv.visibility = View.GONE
                    mContentViewBinding.fingerprintErrorTv.visibility = View.VISIBLE
                    mContentViewBinding.fingerprintErrorTv.text = getString(R.string.register_at_least_one_fingerprint_in_settings)
                    return
                }

                mContentViewBinding.fingerprintErrorTv.visibility = View.GONE
                mContentViewBinding.fingerPrintIv.visibility = View.VISIBLE
                generateKey()
                try {
                    if (cipherInit()) {
                        val cryptoObject: FingerprintManager.CryptoObject?
                        val helper: FingerprintHandler
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cryptoObject = mCipher?.let { FingerprintManager.CryptoObject(it) }

                            helper = FingerprintHandler(requireContext())
                            cryptoObject?.let { helper.startAuth(fingerprintManager, it) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

    fun verifyOtp(otp: String) {
        if (otp.length == AppSharedPref.getTwilioOtpLength(context!!)!!.toInt()) {
            mContentViewBinding.loading = true
            Utils.hideKeyboard(mContentViewBinding.otpEt)
            ApiConnection.verifyOtp(context!!, mContentViewBinding.verificationPhone!!.trim(), mContentViewBinding.data!!.email, otp, "login")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(context!!, true) {
                        override fun onNext(responseModel: BaseModel) {
                            super.onNext(responseModel)
                            mContentViewBinding.loading = false
                            if (responseModel.success) {
                                mContentViewBinding.data?.mobile = mContentViewBinding?.verificationPhone?:""
                                mContentViewBinding.handler!!.login(mContentViewBinding.data!!)
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val keyGenerator: KeyGenerator
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get KeyGenerator instance", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to get KeyGenerator instance", e)
        }

        try {
            mKeyStore!!.load(null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build())
            }
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun cipherInit(): Boolean {
        try {
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }

        try {
            mKeyStore!!.load(null)
            val key = mKeyStore!!.getKey(KEY_NAME, null) as SecretKey
            mCipher!!.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: InvalidKeyException) {
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private inner class FingerprintHandler internal constructor(private val mContext: Context) : FingerprintManager.AuthenticationCallback() {

        internal fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
            mCancellationSignal = CancellationSignal()
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            manager.authenticate(cryptoObject, mCancellationSignal, 0, this, null)
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
            if (helpString.isNotEmpty())
                Toast.makeText(mContext, String.format(getString(R.string.authentication_help), helpString.toString()), Toast.LENGTH_LONG).show()
        }

        override fun onAuthenticationFailed() {
            Utils.showShakeError(requireContext(), mContentViewBinding.fingerPrintIv)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            Toast.makeText(mContext, mContext.getString(R.string.auth_succeeded), Toast.LENGTH_SHORT).show()
            mContentViewBinding.data!!.username = AppSharedPref.getCustomerFingerUserName(requireContext())
            mContentViewBinding.data!!.password = AppSharedPref.getCustomerFingerPassword(requireContext())
            mContentViewBinding.handler!!.onClickLogin(mContentViewBinding.data!!)
        }
    }

    override fun onResume() {
        super.onResume()
        setupFingerPrintLogin()
    }

    override fun onStop() {
        super.onStop()
        if (::mCancellationSignal.isInitialized) {
            mCancellationSignal.cancel()
        }
    }
}

