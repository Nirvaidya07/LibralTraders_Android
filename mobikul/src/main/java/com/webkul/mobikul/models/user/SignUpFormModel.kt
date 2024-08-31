package com.webkul.mobikul.models.user


import android.view.View
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import com.libraltraders.android.R
import com.webkul.mobikul.fragments.SignUpBottomSheetFragment
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
class SignUpFormModel : BaseModel() {

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

    @JsonProperty("dateFormat")

    var dateFormat: String = ""

    @JsonProperty("isMobileVisible")

    var isMobileVisible: Boolean = false

    @JsonProperty("isMobileRequired")

    var isMobileRequired: Boolean = false

    @JsonProperty("sellerRegistrationStatus")

    var sellerRegistrationStatus: Boolean = false

    var dialCode: String = ""


    var prefix :String ?= ""
    var firstName :String ?= ""
    var middleName :String ?= ""
    var lastName :String ?= ""
    var suffix:String ? = ""
    var dob :String ?= ""
    var taxVat:String ? = ""
    var gender = 0
    var emailAddr :String ?= ""
    var password :String ?= ""
    var confirmPassword :String ?= ""
    var pictureURL :String ?= ""
    var mobile :String ?= ""
    var isSocial: Int = 0
    var shopURL :String ?= ""
    var shopName :String ?= ""
    var orderId:String ?=""

    var signUpAsSeller: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpAsSeller)
        }


    fun getPrefixPosition(): Int {
        if (prefixHasOptions && !prefix.isNullOrBlank()) {
            prefixOptions.forEachIndexed { index, eachPrefix ->
                if (prefix == eachPrefix) {
                    return index
                }
            }
        }
        return 0
    }

    fun getSuffixPosition(): Int {
        if (suffixHasOptions && !suffix.isNullOrBlank()) {
            suffixOptions.forEachIndexed { index, eachPrefix ->
                if (suffix == eachPrefix) {
                    return index
                }
            }
        }
        return 0
    }

    fun isFormValidated(fragmentContext: SignUpBottomSheetFragment): Boolean {
        var isFormValidated = true

        /* Checking Confirm Password */
        if (confirmPassword.isNullOrBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.confirmPassword.error = fragmentContext.getString(R.string.confirm_password) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.confirmPassword)
            fragmentContext.mContentViewBinding.confirmPassword.requestFocus()
        } else if (!password.isNullOrBlank()  && password?.trim() != confirmPassword) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.confirmPassword.error = fragmentContext.getString(R.string.password_match_issue)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.confirmPassword)
            fragmentContext.mContentViewBinding.confirmPassword.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.confirmPassword.isErrorEnabled = false
            fragmentContext.mContentViewBinding.confirmPassword.error = null
        }

        /* Checking Password */
        if (password.isNullOrBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.password.error = fragmentContext.getString(R.string.password) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.password)
            fragmentContext.mContentViewBinding.password.requestFocus()
        } else if (password?.trim()!!.length < 8) {
            isFormValidated = false
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.password)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.passValidationLayout)
        } else {
            fragmentContext.mContentViewBinding.password.isErrorEnabled = false
            fragmentContext.mContentViewBinding.password.error = null
        }

        /* Checking seller shop url */
        if (signUpAsSeller) {
            if (shopURL?.trim().isNullOrBlank()) {
                fragmentContext.mContentViewBinding.shopUrl.requestFocus()
                fragmentContext.mContentViewBinding.shopUrl.error = fragmentContext.context?.resources?.getString(R.string.shop_url) + " " + fragmentContext.context?.getString(R.string.is_required)
                isFormValidated = false
            } else if (!Utils.validateUrlForSpecialCharacter(shopURL!!)) {
                fragmentContext.mContentViewBinding.shopUrl.requestFocus()
                fragmentContext.mContentViewBinding.shopUrl.error = fragmentContext.getString(R.string.enter_a_valid) + " " + fragmentContext.getString(R.string.shop_url)
                isFormValidated = false
            } else {
                fragmentContext.mContentViewBinding.shopUrl.error = null
            }
        }
        /* Checking Mobile */
        if (isMobileVisible && isMobileRequired) {
            if (mobile.isNullOrBlank()) {
                isFormValidated = false
                fragmentContext.mContentViewBinding.mobile.error = fragmentContext.getString(R.string.mobile) + " " + fragmentContext.getString(R.string.is_required)
                Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.mobile)
                fragmentContext.mContentViewBinding.mobile.requestFocus()
            } else if (!android.util.Patterns.PHONE.matcher(mobile!!.trim()).matches()) {
                isFormValidated = false
                fragmentContext.mContentViewBinding.mobile.error = fragmentContext.getString(R.string.enter_a_valid) + " " + fragmentContext.getString(R.string.mobile)
                Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.mobile)
                fragmentContext.mContentViewBinding.mobile.requestFocus()
            } else {
                fragmentContext.mContentViewBinding.mobile.isErrorEnabled = false
                fragmentContext.mContentViewBinding.mobile.error = null
            }
        }

        /* Checking Email */
        if (emailAddr.isNullOrBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.email.error = fragmentContext.getString(R.string.email) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.email)
            fragmentContext.mContentViewBinding.email.requestFocus()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddr!!.trim()).matches()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.email.error = fragmentContext.getString(R.string.enter_a_valid) + " " + fragmentContext.getString(R.string.email)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.email)
            fragmentContext.mContentViewBinding.email.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.email.isErrorEnabled = false
            fragmentContext.mContentViewBinding.email.error = null
        }

        /* Checking Gender */
        if (isGenderVisible && isGenderRequired) {
            if (gender == 0) {
                isFormValidated = false
                fragmentContext.mContentViewBinding.genderSpTi.error = fragmentContext.getString(R.string.gender) + " " + fragmentContext.getString(R.string.is_required)
                Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.genderSpTi)
                fragmentContext.mContentViewBinding.genderSp.performClick()
            } else {
                fragmentContext.mContentViewBinding.genderSpTi.error = null
            }
        }

        /* Checking Tax Vat */
        if (isTaxVisible && isTaxRequired) {
            if (taxVat.isNullOrBlank()) {
                isFormValidated = false
                fragmentContext.mContentViewBinding.taxVatNumber.error = fragmentContext.getString(R.string.tax_vat_number) + " " + fragmentContext.getString(R.string.is_required)
                Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.taxVatNumber)
                fragmentContext.mContentViewBinding.taxVatNumber.requestFocus()
            } else {
                fragmentContext.mContentViewBinding.taxVatNumber.isErrorEnabled = false
                fragmentContext.mContentViewBinding.taxVatNumber.error = null
            }
        }

        /* Checking DOB */
        if (isDOBVisible && isDOBRequired) {
            if (dob.isNullOrBlank()) {
                isFormValidated = false
                fragmentContext.mContentViewBinding.dateOfBirth.error = fragmentContext.getString(R.string.date_of_birth) + " " + fragmentContext.getString(R.string.is_required)
                Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.dateOfBirth)
                fragmentContext.mContentViewBinding.dateOfBirth.requestFocus()
            } else {
                fragmentContext.mContentViewBinding.dateOfBirth.isErrorEnabled = false
                fragmentContext.mContentViewBinding.dateOfBirth.error = null
            }
        }

        /* Checking Name Suffix */
        if (isSuffixVisible && isSuffixRequired) {
            if (suffixHasOptions) {
                if (suffix.isNullOrBlank()) {
                    isFormValidated = false
                    fragmentContext.mContentViewBinding.suffixError.visibility = View.VISIBLE
                    fragmentContext.mContentViewBinding.suffixError.error = fragmentContext.getString(R.string.name_suffix) + " " + fragmentContext.getString(R.string.is_required)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.suffixHeading)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.suffixSp)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.suffixError)
                    fragmentContext.mContentViewBinding.suffixSp.performClick()
                } else {
                    fragmentContext.mContentViewBinding.suffixError.visibility = View.GONE
                    fragmentContext.mContentViewBinding.suffixError.error = null
                }
            } else {
                if (suffix.isNullOrBlank()) {
                    isFormValidated = false
                    fragmentContext.mContentViewBinding.suffix.error = fragmentContext.getString(R.string.name_suffix) + " " + fragmentContext.getString(R.string.is_required)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.suffix)
                    fragmentContext.mContentViewBinding.suffix.requestFocus()
                } else {
                    fragmentContext.mContentViewBinding.suffix.isErrorEnabled = false
                    fragmentContext.mContentViewBinding.suffix.error = null
                }
            }
        }

        /* Checking Last Name */
        if (lastName.isNullOrBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.lastName.error = fragmentContext.getString(R.string.last_name) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.lastName)
            fragmentContext.mContentViewBinding.lastName.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.lastName.isErrorEnabled = false
            fragmentContext.mContentViewBinding.lastName.error = null
        }

        /* Checking First Name */
        if (firstName.isNullOrBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.firstName.error = fragmentContext.getString(R.string.first_name) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.firstName)
            fragmentContext.mContentViewBinding.firstName.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.firstName.isErrorEnabled = false
            fragmentContext.mContentViewBinding.firstName.error = null
        }

        /* Checking Name Prefix */
        if (isPrefixVisible && isPrefixRequired) {
            if (prefixHasOptions) {
                if (prefix.isNullOrBlank()) {
                    isFormValidated = false
                    fragmentContext.mContentViewBinding.prefixError.visibility = View.VISIBLE
                    fragmentContext.mContentViewBinding.prefixError.error = fragmentContext.getString(R.string.name_prefix) + " " + fragmentContext.getString(R.string.is_required)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.prefixHeading)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.prefixSp)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.prefixError)
                    fragmentContext.mContentViewBinding.prefixSp.performClick()
                } else {
                    fragmentContext.mContentViewBinding.prefixError.visibility = View.GONE
                    fragmentContext.mContentViewBinding.prefixError.error = null
                }
            } else {
                if (prefix.isNullOrBlank()) {
                    isFormValidated = false
                    fragmentContext.mContentViewBinding.prefix.error = fragmentContext.getString(R.string.name_prefix) + " " + fragmentContext.getString(R.string.is_required)
                    Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.prefix)
                    fragmentContext.mContentViewBinding.prefix.requestFocus()
                } else {
                    fragmentContext.mContentViewBinding.prefix.isErrorEnabled = false
                    fragmentContext.mContentViewBinding.prefix.error = null
                }
            }
        }

        return isFormValidated
    }
}