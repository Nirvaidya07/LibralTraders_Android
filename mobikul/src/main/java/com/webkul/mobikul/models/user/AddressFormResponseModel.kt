package com.webkul.mobikul.models.user


import android.view.View
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import com.libraltraders.android.R
import com.webkul.mobikul.activities.AddEditAddressActivity
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel
import org.json.JSONArray
import org.json.JSONObject

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
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class AddressFormResponseModel() : BaseModel() {


    @JsonProperty("streetLineCount")
    var streetLineCount: Int = 2

    @JsonProperty("isPrefixVisible")
    var isPrefixVisible: Boolean = false

    @JsonProperty("isPrefixRequired")
    var isPrefixRequired: Boolean = false

    @JsonProperty("prefixHasOptions")
    var prefixHasOptions: Boolean = false

    @JsonProperty("prefixOptions")
    var prefixOptions: ArrayList<String> = ArrayList()

    @JsonProperty("isMiddlenameVisible")
    var isMiddleNameVisible: Boolean = false

    @JsonProperty("isSuffixVisible")
    var isSuffixVisible: Boolean = false

    @JsonProperty("isSuffixRequired")
    var isSuffixRequired: Boolean = false

    @JsonProperty("suffixHasOptions")
    var suffixHasOptions: Boolean = false

    @JsonProperty("suffixOptions")
    var suffixOptions: ArrayList<String> = ArrayList()

    @JsonProperty("addressData")
    var addressData = AddressDetailsData()

    @JsonProperty("countryData")
    var countryData: ArrayList<CountryData> = ArrayList()

    @JsonProperty("defaultCountry")
    val defaultCountry: String = ""

    @JsonProperty("allowToChooseState")
    var allowToChooseState: Boolean = false

    @JsonProperty("prefixValue")
    var prefixValue: String = ""

    @JsonProperty("firstName")
    var firstName: String? = ""

    @JsonProperty("middleName")
    var middleName: String? = ""

    @JsonProperty("lastName")
    var lastName: String? = ""

    @JsonProperty("suffixValue")
    var suffixValue: String = ""

    @JsonProperty("isCompanyVisible")
    var isCompanyVisible: Boolean = false

    @JsonProperty("isCompanyRequired")
    var isCompanyRequired: Boolean = false

    @JsonProperty("isTelephoneVisible")
    var isTelephoneVisible: Boolean = false

    @JsonProperty("isTelephoneRequired")
    var isTelephoneRequired: Boolean = false

    @JsonProperty("isFaxVisible")
    var isFaxVisible: Boolean = false

    @JsonProperty("isFaxRequired")
    var isFaxRequired: Boolean = false


    var stateRequired: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.stateRequired)
        }

    var hasStateData: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasStateData)
        }


    fun getSelectedPrefixPosition(): Int {
        if (prefixHasOptions && !addressData.prefix.isNullOrEmpty()) {
            for (prefixIterator in 0 until prefixOptions.size) {
                if (prefixOptions[prefixIterator] == addressData.prefix) {
                    return if (isPrefixRequired) prefixIterator else prefixIterator + 1
                }
            }
        }
        return 0
    }

    fun getSelectedSuffixPosition(): Int {
        if (suffixHasOptions && !addressData.suffix.isNullOrEmpty()) {
            for (suffixIterator in 0 until prefixOptions.size) {
                if (suffixOptions[suffixIterator] == addressData.suffix) {
                    return if (isSuffixRequired) suffixIterator else suffixIterator + 1
                }
            }
        }
        return 0
    }

    fun getSelectedCountryPosition(): Int {
        if (!addressData.country_id.isNullOrBlank()) {
            for (countryIterator in 0 until countryData.size) {
                if (countryData[countryIterator].country_id == addressData.country_id) {
                    return countryIterator
                }
            }
        } else if (defaultCountry.isNotBlank()) {
            for (countryIterator in 0 until countryData.size) {
                if (countryData[countryIterator].country_id == defaultCountry) {
                    return countryIterator
                }
            }
        }
        return -1
    }
    var selectedRegionId=0

    fun getSelectedStatePosition(): Int {
        if (addressData.region_id != 0) {
            for (countryIterator in 0 until countryData.size) {
                if (countryData[countryIterator].country_id == addressData.country_id) {
                    for (stateIterator in 0 until countryData[countryIterator].states.size) {
                        if (countryData[countryIterator].states[stateIterator].region_id == addressData.region_id) {
                            return stateIterator+1
                        }
                    }
                }
            }
        } else if (!addressData.selectedRegion.isNullOrBlank()) {
            for (countryIterator in 0 until countryData.size) {
                if (countryData[countryIterator].country_id == addressData.country_id) {
                    for (stateIterator in 0 until countryData[countryIterator].states.size) {
                        if (countryData[countryIterator].states[stateIterator].name == addressData.selectedRegion) {
                            selectedRegionId=countryData[countryIterator].states[stateIterator].region_id
                            return stateIterator+1
                        }
                    }
                }
            }
        }
        return 0
    }

    fun isFormValidated(addEditAddressActivity: AddEditAddressActivity): Boolean {
        var isFormValidated = true

        /* Checking State */
        val countryPosition = getSelectedCountryPosition()
        if (countryPosition != -1 && countryData.isNotEmpty() && countryData[getSelectedCountryPosition()].isStateRequired && !hasStateData && addressData.region?.trim().isNullOrBlank()) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.state.error = addEditAddressActivity.getString(R.string.state_province) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.state)
            addEditAddressActivity.mContentViewBinding.state.requestFocus()
        } else if (countryPosition != -1 && countryData.isNotEmpty() && countryData[getSelectedCountryPosition()].isStateRequired && hasStateData && addressData.region_id == 0) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.stateSpTil.error = addEditAddressActivity.getString(R.string.state_province) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.stateSpTil)
            addEditAddressActivity.mContentViewBinding.stateSpTil.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.state.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.state.error = null
        }

        /* Checking PostCode */
        if (!countryData[getSelectedCountryPosition()].isZipOptional && addressData.postcode?.trim().isNullOrBlank()) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.zip.error = addEditAddressActivity.getString(R.string.zip_postal_code) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.zip)
            addEditAddressActivity.mContentViewBinding.zip.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.zip.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.zip.error = null
        }

        /* Checking City */
        if (addressData.city?.trim().isNullOrBlank()) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.city.error = addEditAddressActivity.getString(R.string.city) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.city)
            addEditAddressActivity.mContentViewBinding.city.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.city.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.city.error = null
        }

        /* Checking Street Address */
//        setStreetData()

        if (addressData.street?.get(0)?.trim()?.isBlank() == true) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.streetAddress0.error = addEditAddressActivity.getString(R.string.street_address) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.streetAddress0)
            addEditAddressActivity.mContentViewBinding.streetAddress0.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.streetAddress0.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.streetAddress0.error = null
        }

        /* Checking Telephone */
        if (isTelephoneRequired && isTelephoneVisible) {
            if (addressData.telephone?.trim().isNullOrBlank()) {
                isFormValidated = false
                addEditAddressActivity.mContentViewBinding.telephone.error = addEditAddressActivity.getString(R.string.phone_number) + " " + addEditAddressActivity.getString(R.string.is_required)
                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.telephone)
                addEditAddressActivity.mContentViewBinding.telephone.requestFocus()
            } else if (!android.util.Patterns.PHONE.matcher(addressData.telephone?.trim()).matches()) {
                isFormValidated = false
                addEditAddressActivity.mContentViewBinding.telephone.error = addEditAddressActivity.getString(R.string.enter_a_valid) + " " + addEditAddressActivity.getString(R.string.phone_number)
                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.telephone)
                addEditAddressActivity.mContentViewBinding.telephone.requestFocus()
            } else {
                addEditAddressActivity.mContentViewBinding.telephone.isErrorEnabled = false
                addEditAddressActivity.mContentViewBinding.telephone.error = null
            }
        }

        /* Checking Company */
        if (isCompanyVisible && isCompanyRequired) {
            if (addressData.company?.trim().isNullOrBlank()) {
                isFormValidated = false
                addEditAddressActivity.mContentViewBinding.company.error = addEditAddressActivity.getString(R.string.company) + " " + addEditAddressActivity.getString(R.string.is_required)
                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.company)
                addEditAddressActivity.mContentViewBinding.company.requestFocus()
            } else {
                addEditAddressActivity.mContentViewBinding.company.isErrorEnabled = false
                addEditAddressActivity.mContentViewBinding.company.error = null
            }
        }


        /* Checking Fax */
//        if (isFaxVisible) {
//            if (addressData.fax?.trim().isNullOrBlank()) {
//                isFormValidated = false
//                addEditAddressActivity.mContentViewBinding.fax.error = addEditAddressActivity.getString(R.string.fax) + " " + addEditAddressActivity.getString(R.string.is_required)
//                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.fax)
//                addEditAddressActivity.mContentViewBinding.fax.requestFocus()
//            } else {
//                addEditAddressActivity.mContentViewBinding.fax.isErrorEnabled = false
//                addEditAddressActivity.mContentViewBinding.fax.error = null
//            }
//        }


        /* Checking Email */
        if (!AppSharedPref.isLoggedIn(addEditAddressActivity)) {
            if (addressData.email?.trim().isNullOrBlank()) {
                isFormValidated = false
                addEditAddressActivity.mContentViewBinding.email.error = addEditAddressActivity.getString(R.string.email) + " " + addEditAddressActivity.getString(R.string.is_required)
                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.email)
                addEditAddressActivity.mContentViewBinding.email.requestFocus()
            } else if (!Utils.isValidEmailId(addressData.email!!.trim())) {
                isFormValidated = false
                addEditAddressActivity.mContentViewBinding.email.error = addEditAddressActivity.getString(R.string.enter_a_valid) + " " + addEditAddressActivity.getString(R.string.email)
                Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.email)
                addEditAddressActivity.mContentViewBinding.email.requestFocus()
            } else {
                addEditAddressActivity.mContentViewBinding.email.isErrorEnabled = false
                addEditAddressActivity.mContentViewBinding.email.error = null
            }
        }

        /* Checking Name Suffix */
        if (isSuffixVisible && isSuffixRequired) {
            if (suffixHasOptions) {
                if (addressData.suffix?.trim().isNullOrBlank()) {
                    isFormValidated = false
                    addEditAddressActivity.mContentViewBinding.suffixError.visibility = View.VISIBLE
                    addEditAddressActivity.mContentViewBinding.suffixError.error = addEditAddressActivity.getString(R.string.name_suffix) + " " + addEditAddressActivity.getString(R.string.is_required)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.suffixHeading)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.suffixSp)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.suffixError)
                    addEditAddressActivity.mContentViewBinding.suffixSp.performClick()
                } else {
                    addEditAddressActivity.mContentViewBinding.suffixError.visibility = View.GONE
                    addEditAddressActivity.mContentViewBinding.suffixError.error = null
                }
            } else {
                if (addressData.suffix?.trim().isNullOrBlank()) {
                    isFormValidated = false
                    addEditAddressActivity.mContentViewBinding.suffix.error = addEditAddressActivity.getString(R.string.name_suffix) + " " + addEditAddressActivity.getString(R.string.is_required)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.suffix)
                    addEditAddressActivity.mContentViewBinding.suffix.requestFocus()
                } else {
                    addEditAddressActivity.mContentViewBinding.suffix.isErrorEnabled = false
                    addEditAddressActivity.mContentViewBinding.suffix.error = null
                }
            }
        }

        /* Checking Last Name */
        if (addressData.lastname?.trim().isNullOrBlank()) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.lastName.error = addEditAddressActivity.getString(R.string.last_name) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.lastName)
            addEditAddressActivity.mContentViewBinding.lastName.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.lastName.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.lastName.error = null
        }

        /* Checking First Name */
        if (addressData.firstname?.trim().isNullOrBlank()) {
            isFormValidated = false
            addEditAddressActivity.mContentViewBinding.firstName.error = addEditAddressActivity.getString(R.string.first_name) + " " + addEditAddressActivity.getString(R.string.is_required)
            Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.firstName)
            addEditAddressActivity.mContentViewBinding.firstName.requestFocus()
        } else {
            addEditAddressActivity.mContentViewBinding.firstName.isErrorEnabled = false
            addEditAddressActivity.mContentViewBinding.firstName.error = null
        }

        /* Checking Name Prefix */
        if (isPrefixVisible && isPrefixRequired) {
            if (prefixHasOptions) {
                if (addressData.prefix?.trim().isNullOrBlank()) {
                    isFormValidated = false
                    addEditAddressActivity.mContentViewBinding.prefixError.visibility = View.VISIBLE
                    addEditAddressActivity.mContentViewBinding.prefixError.error = addEditAddressActivity.getString(R.string.name_prefix) + " " + addEditAddressActivity.getString(R.string.is_required)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.prefixHeading)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.prefixSp)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.prefixError)
                    addEditAddressActivity.mContentViewBinding.prefixSp.performClick()
                } else {
                    addEditAddressActivity.mContentViewBinding.prefixError.visibility = View.GONE
                    addEditAddressActivity.mContentViewBinding.prefixError.error = null
                }
            } else {
                if (addressData.prefix?.trim().isNullOrBlank()) {
                    isFormValidated = false
                    addEditAddressActivity.mContentViewBinding.prefix.error = addEditAddressActivity.getString(R.string.name_prefix) + " " + addEditAddressActivity.getString(R.string.is_required)
                    Utils.showShakeError(addEditAddressActivity, addEditAddressActivity.mContentViewBinding.prefix)
                    addEditAddressActivity.mContentViewBinding.prefix.requestFocus()
                } else {
                    addEditAddressActivity.mContentViewBinding.prefix.isErrorEnabled = false
                    addEditAddressActivity.mContentViewBinding.prefix.error = null
                }
            }
        }
        return isFormValidated
    }

//    private fun setStreetData(addEditAddressActivity: AddEditAddressActivity) {
//        addressData.street = ArrayList()
//        addressData.street.add(addEditAddressActivity.mContentViewBinding.streetAddress0Et.text!!.toString().trim())
//        addressData.street.add(addEditAddressActivity.mContentViewBinding.streetAddress1Et.text!!.toString().trim())
//        addressData.street.add(addEditAddressActivity.mContentViewBinding.streetAddress2Et.text!!.toString().trim())
//        addressData.street.add(addressData.street.get(3).trim())
//    }

    fun getFormattedAddressData(): String {
        val addressJsonData = JSONObject()
        try {
            addressJsonData.put("prefix", addressData.prefix?.trim())
            addressJsonData.put("firstName", addressData.firstname?.trim())
            addressJsonData.put("middleName", addressData.middlename?.trim())
            addressJsonData.put("lastName", addressData.lastname?.trim())
            addressJsonData.put("suffix", addressData.suffix?.trim())
            addressJsonData.put("company", addressData.company?.trim())
            addressJsonData.put("telephone", addressData.telephone?.trim())
            addressJsonData.put("fax", addressData.fax?.trim())

            val streetJsonArr = JSONArray()
            if (streetLineCount > 0) {
                streetJsonArr.put(addressData.street?.get(0)?.trim())
            }
            if (streetLineCount > 1) {
                streetJsonArr.put(addressData.street?.get(1)?.trim())
            }
            if (streetLineCount > 2) {
                streetJsonArr.put(addressData.street?.get(2)?.trim())
            }
            if (streetLineCount > 3) {
                streetJsonArr.put(addressData.street?.get(3)?.trim())
            }
            addressJsonData.put("street", streetJsonArr)

            addressJsonData.put("city", addressData.city?.trim())

            if (stateRequired) {
                addressJsonData.put("region_id", if (hasStateData) addressData.region_id else 0)
                addressJsonData.put("region", if (hasStateData) "" else addressData.region?.trim())
            } else {
                addressJsonData.put("region_id", 0)
                addressJsonData.put("region", "")
            }

            addressJsonData.put("postcode", addressData.postcode?.trim())
            addressJsonData.put("country_id", addressData.country_id)
            addressJsonData.put("default_billing", if (addressData.isDefaultBilling) "1" else "0")
            addressJsonData.put("default_shipping", if (addressData.isDefaultShipping) "1" else "0")
            addressJsonData.put("saveInAddressBook", if (addressData.saveInAddressBook) "1" else "0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addressJsonData.toString()
    }
}