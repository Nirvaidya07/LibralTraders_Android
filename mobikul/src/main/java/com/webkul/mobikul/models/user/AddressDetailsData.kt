package com.webkul.mobikul.models.user


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import java.util.*

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
class AddressDetailsData() : BaseObservable(), Parcelable {

    @JsonProperty("isDefaultBilling")
    var isDefaultBilling: Boolean = false

    @JsonProperty("isDefaultShipping")
    var isDefaultShipping: Boolean = false

    @JsonProperty("entity_id")
    var entityId: String ?= ""

    @JsonProperty("entity_type_id")
    var entityTypeId: String ?= ""

    @JsonProperty("attribute_set_id")
    var attributeSetId: String ?= ""

    @JsonProperty("increment_id")
    var incrementId: String ?= ""

    @JsonProperty("parent_id")
    var parentId: String ?= ""

    @JsonProperty("created_at")
    var createdAt: String ?= ""

    @JsonProperty("updated_at")
    var updatedAt: String ?= ""

    @JsonProperty("is_active")
    var isActive: String ? = ""

    @JsonProperty("prefix")
    var prefix: String ?= ""

    @JsonProperty("firstname")
    @JsonAlias("firstname", "firstName")
    var firstname: String ?= ""

    @JsonProperty("middlename")
    @JsonAlias("middlename", "middleName")
    var middlename: String ?= ""

    @JsonProperty("lastname")
    @JsonAlias("lastname", "lastName")
    var lastname: String ?= ""

    @JsonProperty("suffix")
    var suffix: String ?= ""

    @JsonProperty("company")
    var company: String ?= ""

    @JsonProperty("email")
    var email: String ?= ""

    @JsonProperty("city")
    var city: String ?= ""

    @JsonProperty("country_id")
    var country_id: String ?= ""

    var countryName: String ?= ""

    @JsonProperty("region")
    var region: String ?= ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.region)
        }

    @JsonProperty("postcode")
    var postcode: String? = ""

    @JsonProperty("telephone")
    var telephone: String ?= ""

    @JsonProperty("fax")
    var fax: String ?= ""

    @JsonProperty("region_id")
    var region_id: Int = 0

    @JsonProperty("street")
    var street: ArrayList<String> ?= ArrayList(4)

    var selectedRegion: String? = ""

    var saveInAddressBook: Boolean = false

    constructor(parcel: Parcel) : this() {
        isDefaultBilling = parcel.readByte() != 0.toByte()
        isDefaultShipping = parcel.readByte() != 0.toByte()
        entityId = parcel.readString()
        entityTypeId = parcel.readString()
        attributeSetId = parcel.readString()
        incrementId = parcel.readString()
        parentId = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        isActive = parcel.readString()
        prefix = parcel.readString()
        firstname = parcel.readString()
        middlename = parcel.readString()
        lastname = parcel.readString()
        suffix = parcel.readString()
        company = parcel.readString()
        email = parcel.readString()
        city = parcel.readString()
        country_id = parcel.readString()
        countryName = parcel.readString()
        region = parcel.readString()
        postcode = parcel.readString()
        telephone = parcel.readString()
        fax = parcel.readString()
        region_id = parcel.readInt()
        street = parcel.createStringArrayList()
        selectedRegion = parcel.readString()
        saveInAddressBook = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isDefaultBilling) 1 else 0)
        parcel.writeByte(if (isDefaultShipping) 1 else 0)
        parcel.writeString(entityId)
        parcel.writeString(entityTypeId)
        parcel.writeString(attributeSetId)
        parcel.writeString(incrementId)
        parcel.writeString(parentId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(isActive)
        parcel.writeString(prefix)
        parcel.writeString(firstname)
        parcel.writeString(middlename)
        parcel.writeString(lastname)
        parcel.writeString(suffix)
        parcel.writeString(company)
        parcel.writeString(email)
        parcel.writeString(city)
        parcel.writeString(country_id)
        parcel.writeString(countryName)
        parcel.writeString(region)
        parcel.writeString(postcode)
        parcel.writeString(telephone)
        parcel.writeString(fax)
        parcel.writeInt(region_id)
        parcel.writeStringList(street)
        parcel.writeString(selectedRegion)
        parcel.writeByte(if (saveInAddressBook) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressDetailsData> {
        override fun createFromParcel(parcel: Parcel): AddressDetailsData {
            return AddressDetailsData(parcel)
        }

        override fun newArray(size: Int): Array<AddressDetailsData?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedAddress(): String {
        val formattedAddress = StringBuilder()
        if (!firstname.isNullOrEmpty()) {
            formattedAddress.append("$prefix $firstname $middlename $lastname $suffix <br/>")
            street?.forEach { streetAddress ->
                if (streetAddress.isNotBlank())
                    formattedAddress.append("$streetAddress <br/>")
            }
            if (!city.isNullOrEmpty())
                formattedAddress.append("$city, ")
            if (!region.isNullOrEmpty()) {
                formattedAddress.append("$region, ")
            } else if (!selectedRegion.isNullOrEmpty()) {
                formattedAddress.append("$selectedRegion, ")
            }
            formattedAddress.append("$postcode <br/>")
            formattedAddress.append("$countryName <br/>")
            if (!telephone.isNullOrEmpty())
                formattedAddress.append("T: $telephone")
        }
        return formattedAddress.toString().trim()
    }
}