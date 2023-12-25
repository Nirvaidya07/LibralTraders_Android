package com.webkul.mobikul.models.user

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.databinding.BaseObservable
import com.webkul.mobikul.R
import com.webkul.mobikul.fragments.LoginBottomSheetFragment
import com.webkul.mobikul.helpers.Utils.Companion.showShakeError

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

class LoginFormModel : BaseObservable() {

    var username: String = ""
    var email: String = ""
    var mobile: String = ""
    var password: String = ""
    var dialCode: String = ""

    fun isFormValidated(fragmentContext: LoginBottomSheetFragment): Boolean {
        var isFormValidated = true

        /* Checking Password */
        if (password.isBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.passwordTil.error = fragmentContext.getString(R.string.password) + " " + fragmentContext.getString(R.string.is_required)
            showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.passwordTil)
            fragmentContext.mContentViewBinding.passwordTil.requestFocus()
        } else if (password.trim().length < 6) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.passwordTil.error = fragmentContext.getString(R.string.enter_a_valid) + " " + fragmentContext.getString(R.string.password)
            showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.passwordTil)
            fragmentContext.mContentViewBinding.passwordTil.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.passwordTil.isErrorEnabled = false
            fragmentContext.mContentViewBinding.passwordTil.error = null
        }

        /* Checking Username */
        if (username.isBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.emailTil.error = fragmentContext.getString(R.string.email) + " " + fragmentContext.getString(R.string.is_required)
            showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.emailTil)
            fragmentContext.mContentViewBinding.emailTil.requestFocus()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches() && !android.util.Patterns.PHONE.matcher(username.trim()).matches()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.emailTil.error = fragmentContext.getString(R.string.enter_a_valid) + " " + fragmentContext.getString(R.string.email)
            showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.emailTil)
            fragmentContext.mContentViewBinding.emailTil.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.emailTil.isErrorEnabled = false
            fragmentContext.mContentViewBinding.emailTil.error = null

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches()) {
                email = username
                mobile = ""
            } else if (android.util.Patterns.PHONE.matcher(username.trim()).matches()) {
                email = ""
                mobile = username
            }
        }
        return isFormValidated
    }

    fun isFingerPrintEnable(fragmentContext: LoginBottomSheetFragment): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerprintManager = fragmentContext.context?.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
            try {
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun toString(): String {
        return "LoginFormModel(username='$username', email='$email', mobile='$mobile', password='$password', dialCode='$dialCode')"
    }
}