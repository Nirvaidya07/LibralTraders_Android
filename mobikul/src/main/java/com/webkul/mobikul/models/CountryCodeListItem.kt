package com.webkul.mobikul.models


import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.LoginAndSignUpActivity
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
class CountryCodeListItem() : Parcelable {

    @JsonProperty("country_code")
    var country_code: String? = ""

    @JsonProperty("dail_code")
    var dial_code: String? = ""

    @JsonProperty("image")
    var image: String? = ""


    @JsonProperty("mobile")
    var mobile: String? = ""

    @JsonProperty("country_name")
    var countryName: String? = ""


    constructor(parcel: Parcel) : this() {
        country_code = parcel.readString()
        dial_code = parcel.readString()
        image = parcel.readString()
        mobile = parcel.readString()
        countryName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(country_code)
        parcel.writeString(dial_code)
        parcel.writeString(image)
        parcel.writeString(mobile)
        parcel.writeString(countryName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CountryCodeListItem> {
        override fun createFromParcel(parcel: Parcel): CountryCodeListItem {
            return CountryCodeListItem(parcel)
        }

        override fun newArray(size: Int): Array<CountryCodeListItem?> {
            return arrayOfNulls(size)
        }
    }


    fun isFormValidated(context: SignUpBottomSheetFragment): Boolean {
        var isFormValidated = true


        /* Checking Mobile Number*/
//        if (mobile!!.isBlank() || context!!.mContentViewBinding.editMobileNumber.text?.trim()!!.isNotBlank()) {
//            if (!android.util.Patterns.PHONE.matcher(context!!.mContentViewBinding.editMobileNumber.text!!.trim()).matches()) {
//                isFormValidated = false
//                context.mContentViewBinding.editMobileNumber.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.mobile)
//                Utils.showShakeError(context.requireContext(), context.mContentViewBinding.editMobileNumber)
//                context.mContentViewBinding.editMobileNumber.requestFocus()
//            } else {
////                context.mContentViewBinding.editMobileNumber.isErrorEnabled = false
//                context.mContentViewBinding.editMobileNumber.error = null
//            }
//        }

        return isFormValidated
    }
    }