/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.checkout


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.libraltraders.android.BR
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressDetailsData
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CheckoutAddressInfoResponseModel() : BaseModel(), Parcelable {

    @JsonProperty("address")

    var addressData: ArrayList<BillingShippingAddress>? = ArrayList()

    var hasNewAddress = false
    var newAddressData: AddressDetailsData? = AddressDetailsData()
        get() = field ?: AddressDetailsData()
    var selectedAddressData: BillingShippingAddress? = BillingShippingAddress()
        get() = field ?: BillingShippingAddress()

    var selectedShippingMethod: String? = ""

    // For Payment and Reviews Page Only
    var sameAsShipping = true
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.sameAsShipping)
        }

    constructor(parcel: Parcel) : this() {
        addressData = parcel.createTypedArrayList(BillingShippingAddress.CREATOR)
        hasNewAddress = parcel.readByte() != 0.toByte()
        newAddressData = parcel.readParcelable(AddressDetailsData::class.java.classLoader)
        selectedAddressData = parcel.readParcelable(BillingShippingAddress::class.java.classLoader)
        selectedShippingMethod = parcel.readString()
    }

    fun getNewAddressData(): JSONObject {
        val shippingDataJson = JSONObject()
        val newAddressJson = JSONObject()
        shippingDataJson.put("addressId", selectedAddressData?.id)
        if (selectedAddressData?.id == "new") {
            shippingDataJson.put("addressId", "0")
            newAddressJson.put("prefix", newAddressData?.prefix)
            newAddressJson.put("firstName", newAddressData?.firstname)
            newAddressJson.put("middleName", newAddressData?.middlename)
            newAddressJson.put("lastName", newAddressData?.lastname)
            newAddressJson.put("suffix", newAddressData?.suffix)
            newAddressJson.put("company", newAddressData?.company)
            newAddressJson.put("email", newAddressData?.email)
            newAddressJson.put("telephone", newAddressData?.telephone)
            newAddressJson.put("fax", newAddressData?.fax)
            newAddressJson.put("city", newAddressData?.city)
            newAddressJson.put("postcode", newAddressData?.postcode)

            newAddressJson.put("region_id", newAddressData?.region_id)
            newAddressJson.put("region", newAddressData?.region)

            newAddressJson.put("country_id", newAddressData?.country_id)
            val streetJsonArr = JSONArray()
            for (noOfSteet in 0 until newAddressData?.street?.size!!) {
                if (newAddressData!!.street!![noOfSteet].isNotBlank())
                    streetJsonArr.put(newAddressData!!.street!![noOfSteet])
            }
            newAddressJson.put("street", streetJsonArr)

            newAddressJson.put("saveInAddressBook", if (newAddressData!!.saveInAddressBook) "1" else "0")
        }
        shippingDataJson.put("newAddress", newAddressJson)
        shippingDataJson.put("sameAsShipping", if (sameAsShipping) "1" else "0")

        return shippingDataJson
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(addressData)
        parcel.writeByte(if (hasNewAddress) 1 else 0)
        parcel.writeParcelable(newAddressData, flags)
        parcel.writeParcelable(selectedAddressData, flags)
        parcel.writeString(selectedShippingMethod)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutAddressInfoResponseModel> {
        override fun createFromParcel(parcel: Parcel): CheckoutAddressInfoResponseModel {
            return CheckoutAddressInfoResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutAddressInfoResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}