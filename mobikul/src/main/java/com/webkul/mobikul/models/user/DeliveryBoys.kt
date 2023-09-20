package com.webkul.mobikul.models.user

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class DeliveryBoys() : Parcelable {
    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("email")
    var email: String? = null

    @JsonProperty("avatar")
    var avatar: String? = null

    @JsonProperty("vehicleType")
    var vehicleType: String? = null

    @JsonProperty("mobileNumber")
    var mobileNumber: String? = null

    @JsonProperty("vehicleNumber")
    var vehicleNumber: String? = null

    @JsonProperty("id")
    var id: String? = null

    @JsonProperty("orderStatus")
    var orderStatus: String? = null

    @JsonProperty("deliveryBoyLat")
    var deliveryBoyLat: String? = null

    @JsonProperty("deliveryBoyLong")
    var deliveryBoyLong: String? = null

    @JsonProperty("picked")
    var picked: Boolean? = null

    @JsonProperty("otp")
    var otp: String? = null

    @JsonProperty("sellerId")
    var sellerId: String? = null

    @JsonProperty("products")
    var products: List<String>? = null

    var product = ""
        get() {
            products?.forEach {
                field = it+"\n"
            }
            return field
        }

    @JsonProperty("isEligibleForDeliveryBoy")
    var isEligibleForDeliveryBoy: Boolean? = null

    @JsonProperty("customerId")
    var customerId: String? = null

    @JsonProperty("rating")
    var rating: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        email = parcel.readString()
        avatar = parcel.readString()
        vehicleType = parcel.readString()
        mobileNumber = parcel.readString()
        vehicleNumber = parcel.readString()
        id = parcel.readString()
        orderStatus = parcel.readString()
        deliveryBoyLat = parcel.readString()
        deliveryBoyLong = parcel.readString()
        picked = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        otp = parcel.readString()
        sellerId = parcel.readString()
        products = parcel.createStringArrayList()
        isEligibleForDeliveryBoy = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        customerId = parcel.readString()
        rating = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(avatar)
        parcel.writeString(vehicleType)
        parcel.writeString(mobileNumber)
        parcel.writeString(vehicleNumber)
        parcel.writeString(id)
        parcel.writeString(orderStatus)
        parcel.writeString(deliveryBoyLat)
        parcel.writeString(deliveryBoyLong)
        parcel.writeValue(picked)
        parcel.writeString(otp)
        parcel.writeString(sellerId)
        parcel.writeStringList(products)
        parcel.writeValue(isEligibleForDeliveryBoy)
        parcel.writeString(customerId)
        parcel.writeString(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeliveryBoys> {
        override fun createFromParcel(parcel: Parcel): DeliveryBoys {
            return DeliveryBoys(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryBoys?> {
            return arrayOfNulls(size)
        }
    }
}