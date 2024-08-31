package com.webkul.mobikul.models.user


import android.view.View
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import com.libraltraders.android.R
import com.webkul.mobikul.activities.AccountInfoActivity
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class AccountInfoResponseModel : BaseModel() {

    @JsonProperty("isPrefixVisible")

    var isPrefixVisible: Boolean = false

    @JsonProperty("isPrefixRequired")

    var isPrefixRequired: Boolean = false

    @JsonProperty("prefixHasOptions")

    var prefixHasOptions: Boolean = false

    @JsonProperty("prefixOptions")

    var prefixOptions: ArrayList<String> = ArrayList()

    @JsonProperty("isMiddlenameVisible")

    var isMiddlenameVisible: Boolean = false

    @JsonProperty("isSuffixVisible")

    var isSuffixVisible: Boolean = false

    @JsonProperty("isSuffixRequired")

    var isSuffixRequired: Boolean = false

    @JsonProperty("suffixHasOptions")

    var suffixHasOptions: Boolean = false

    @JsonProperty("suffixOptions")

    var suffixOptions: ArrayList<String> = ArrayList()

    @JsonProperty("isDOBVisible")

    var isDOBVisible: Boolean = false

    @JsonProperty("isDOBRequired")

    var isDOBRequired: Boolean = false

    @JsonProperty("isTaxVisible")

    var isTaxVisible: Boolean = false

    @JsonProperty("isTaxRequired")

    var isTaxRequired: Boolean = false

    @JsonProperty("isGenderVisible")

    var isGenderVisible: Boolean = false

    @JsonProperty("isGenderRequired")

    var isGenderRequired: Boolean = false

    @JsonProperty("prefixValue")

    var prefixValue: String = ""

    @JsonProperty("middleName")

    var middleName: String = ""

    @JsonProperty("suffixValue")

    var suffixValue: String = ""

    @JsonProperty("DOBValue")

    var dobValue: String = ""

    @JsonProperty("taxValue")

    var taxValue: String = ""

    @JsonProperty("genderValue")

    var genderValue: Int = 0

    @JsonProperty("firstName")

    var firstName: String = ""

    @JsonProperty("lastName")

    var lastName: String = ""

    @JsonProperty("email")

    var email: String = ""

    @JsonProperty("dateFormat")

    var dateFormat: String = ""

    @JsonProperty("isMobileVisible")

    var isMobileVisible: Boolean = false

    @JsonProperty("isMobileRequired")

    var isMobileRequired: Boolean = false

    @JsonProperty("mobile")

    var mobile: String = ""

    var currentPassword = ""
    var newPassword = ""
    var confirmNewPassword = ""
    var isChangesEmailChecked = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.changesEmailChecked)
        }
    var isChangesPasswordChecked = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.changesPasswordChecked)
        }

    fun isFormValidated(context: AccountInfoActivity): Boolean {
        var isFormValidated = true

        /* Checking Confirm New Password */
        if (isChangesPasswordChecked) {
            if (confirmNewPassword.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.confirmNewPassword.error = context.getString(R.string.confirm_new_password) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.confirmNewPassword)
                context.mContentViewBinding.confirmNewPassword.requestFocus()
            } else if (confirmNewPassword.length < 6) {
                isFormValidated = false
                context.mContentViewBinding.confirmNewPassword.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.confirm_new_password)
                Utils.showShakeError(context, context.mContentViewBinding.confirmNewPassword)
                context.mContentViewBinding.confirmNewPassword.requestFocus()
            } else if (newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() && !newPassword.equals(confirmNewPassword)) {
                isFormValidated = false
                context.mContentViewBinding.confirmNewPassword.error = context.getString(R.string.password_match_issue)
                Utils.showShakeError(context, context.mContentViewBinding.confirmNewPassword)
                context.mContentViewBinding.confirmNewPassword.requestFocus()
            } else {
                context.mContentViewBinding.confirmNewPassword.isErrorEnabled = false
                context.mContentViewBinding.confirmNewPassword.error = null
            }
        }

        /* Checking New Password */
        if (isChangesPasswordChecked) {
            if (newPassword.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.newPassword.error = context.getString(R.string.new_password) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.newPassword)
                context.mContentViewBinding.newPassword.requestFocus()
            } else if (newPassword.length < 6) {
                isFormValidated = false
                context.mContentViewBinding.newPassword.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.new_password)
                Utils.showShakeError(context, context.mContentViewBinding.newPassword)
                context.mContentViewBinding.newPassword.requestFocus()
            } else {
                context.mContentViewBinding.newPassword.isErrorEnabled = false
                context.mContentViewBinding.newPassword.error = null
            }
        }

        /* Checking Current Password */
        if (isChangesEmailChecked || isChangesPasswordChecked) {
            if (currentPassword.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.currentPassword.error = context.getString(R.string.current_password) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.currentPassword)
                context.mContentViewBinding.currentPassword.requestFocus()
            } else if (currentPassword.length < 6) {
                isFormValidated = false
                context.mContentViewBinding.currentPassword.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.current_password)
                Utils.showShakeError(context, context.mContentViewBinding.currentPassword)
                context.mContentViewBinding.currentPassword.requestFocus()
            } else {
                context.mContentViewBinding.currentPassword.isErrorEnabled = false
                context.mContentViewBinding.currentPassword.error = null
            }
        }

        /* Checking Email */
        if (isChangesEmailChecked) {
            if (email.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.email.error = context.getString(R.string.email) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.email)
                context.mContentViewBinding.email.requestFocus()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isFormValidated = false
                context.mContentViewBinding.email.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.email)
                Utils.showShakeError(context, context.mContentViewBinding.email)
                context.mContentViewBinding.email.requestFocus()
            } else {
                context.mContentViewBinding.email.isErrorEnabled = false
                context.mContentViewBinding.email.error = null
            }
        }

        /* Checking Gender */
        if (isGenderVisible && isGenderRequired) {
            if (genderValue == 0) {
                isFormValidated = false
                context.mContentViewBinding.genderError.visibility = View.VISIBLE
                context.mContentViewBinding.genderError.text = context.getString(R.string.gender) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.gender)
                Utils.showShakeError(context, context.mContentViewBinding.genderSp)
                Utils.showShakeError(context, context.mContentViewBinding.genderError)
                context.mContentViewBinding.genderSp.performClick()
            } else {
                context.mContentViewBinding.genderError.visibility = View.GONE
                context.mContentViewBinding.genderError.text = null
            }
        }

        /* Checking Mobile */
        if (isMobileVisible && isMobileRequired) {
            if (mobile.isNullOrBlank()) {
                isFormValidated = false
                context.mContentViewBinding.mobile.error = context.getString(R.string.mobile) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.mobile)
                context.mContentViewBinding.mobile.requestFocus()
            } else if (!android.util.Patterns.PHONE.matcher(mobile).matches()) {
                isFormValidated = false
                context.mContentViewBinding.mobile.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.mobile)
                Utils.showShakeError(context, context.mContentViewBinding.mobile)
                context.mContentViewBinding.mobile.requestFocus()
            } else {
                context.mContentViewBinding.mobile.isErrorEnabled = false
                context.mContentViewBinding.mobile.error = null
            }
        }

        /* Checking Tax Vat */
        if (isTaxVisible && isTaxRequired) {
            if (taxValue.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.taxVatNumber.error = context.getString(R.string.tax_vat_number) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.taxVatNumber)
                context.mContentViewBinding.taxVatNumber.requestFocus()
            } else {
                context.mContentViewBinding.taxVatNumber.isErrorEnabled = false
                context.mContentViewBinding.taxVatNumber.error = null
            }
        }

        /* Checking DOB */
        if (isDOBVisible && isDOBRequired) {
            if (dobValue.isBlank()) {
                isFormValidated = false
                context.mContentViewBinding.dateOfBirth.error = context.getString(R.string.date_of_birth) + " " + context.getString(R.string.is_required)
                Utils.showShakeError(context, context.mContentViewBinding.dateOfBirth)
                context.mContentViewBinding.dateOfBirth.requestFocus()
            } else {
                context.mContentViewBinding.dateOfBirth.isErrorEnabled = false
                context.mContentViewBinding.dateOfBirth.error = null
            }
        }

        /* Checking Name Suffix */
        if (isSuffixVisible && isSuffixRequired) {
            if (suffixHasOptions) {
                if (suffixValue.isBlank()) {
                    isFormValidated = false
                    context.mContentViewBinding.suffixError.visibility = View.VISIBLE
                    context.mContentViewBinding.suffixError.error = context.getString(R.string.name_suffix) + " " + context.getString(R.string.is_required)
                    Utils.showShakeError(context, context.mContentViewBinding.suffixHeading)
                    Utils.showShakeError(context, context.mContentViewBinding.suffixSp)
                    Utils.showShakeError(context, context.mContentViewBinding.suffixError)
                    context.mContentViewBinding.suffixSp.performClick()
                } else {
                    context.mContentViewBinding.suffixError.visibility = View.GONE
                    context.mContentViewBinding.suffixError.error = null
                }
            } else {
                if (suffixValue.isBlank()) {
                    isFormValidated = false
                    context.mContentViewBinding.suffix.error = context.getString(R.string.name_suffix) + " " + context.getString(R.string.is_required)
                    Utils.showShakeError(context, context.mContentViewBinding.suffix)
                    context.mContentViewBinding.suffix.requestFocus()
                } else {
                    context.mContentViewBinding.suffix.isErrorEnabled = false
                    context.mContentViewBinding.suffix.error = null
                }
            }
        }

        /* Checking Last Name */
        if (lastName.isBlank()) {
            isFormValidated = false
            context.mContentViewBinding.lastName.error = context.getString(R.string.last_name) + " " + context.getString(R.string.is_required)
            Utils.showShakeError(context, context.mContentViewBinding.lastName)
            context.mContentViewBinding.lastName.requestFocus()
        } else {
            context.mContentViewBinding.lastName.isErrorEnabled = false
            context.mContentViewBinding.lastName.error = null
        }

        /* Checking First Name */
        if (firstName.isBlank()) {
            isFormValidated = false
            context.mContentViewBinding.firstName.error = context.getString(R.string.first_name) + " " + context.getString(R.string.is_required)
            Utils.showShakeError(context, context.mContentViewBinding.firstName)
            context.mContentViewBinding.firstName.requestFocus()
        } else {
            context.mContentViewBinding.firstName.isErrorEnabled = false
            context.mContentViewBinding.firstName.error = null
        }

        /* Checking Name Prefix */
        if (isPrefixVisible && isPrefixRequired) {
            if (prefixHasOptions) {
                if (prefixValue.isBlank()) {
                    isFormValidated = false
                    context.mContentViewBinding.prefixError.visibility = View.VISIBLE
                    context.mContentViewBinding.prefixError.error = context.getString(R.string.name_prefix) + " " + context.getString(R.string.is_required)
                    Utils.showShakeError(context, context.mContentViewBinding.prefixHeading)
                    Utils.showShakeError(context, context.mContentViewBinding.prefixSp)
                    Utils.showShakeError(context, context.mContentViewBinding.prefixError)
                    context.mContentViewBinding.prefixSp.performClick()
                } else {
                    context.mContentViewBinding.prefixError.visibility = View.GONE
                    context.mContentViewBinding.prefixError.error = null
                }
            } else {
                if (prefixValue.isBlank()) {
                    isFormValidated = false
                    context.mContentViewBinding.prefix.error = context.getString(R.string.name_prefix) + " " + context.getString(R.string.is_required)
                    Utils.showShakeError(context, context.mContentViewBinding.prefix)
                    context.mContentViewBinding.prefix.requestFocus()
                } else {
                    context.mContentViewBinding.prefix.isErrorEnabled = false
                    context.mContentViewBinding.prefix.error = null
                }
            }
        }

        return isFormValidated
    }
}