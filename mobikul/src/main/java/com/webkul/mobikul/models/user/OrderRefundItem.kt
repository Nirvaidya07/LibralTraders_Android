package com.webkul.mobikul.models.user

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderRefundItem() : Parcelable {

    @JsonProperty("productId")

    var productId: String ?= ""

    @JsonProperty("name")

    var name: String? = ""

    @JsonProperty("sku")

    var sku: String ?= ""

    @JsonProperty("qty")

    var qty: String ?= ""

    @JsonProperty("price")

    var price: String ?= ""

    @JsonProperty("subTotal")

    var subTotal: String? = ""

    @JsonProperty("option")

    var itemOption: ArrayList<OptionsItem> ?= ArrayList()

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString()
        name = parcel.readString()
        sku = parcel.readString()
        qty = parcel.readString()
        price = parcel.readString()
        subTotal = parcel.readString()
        itemOption = parcel.createTypedArrayList(OptionsItem.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(sku)
        parcel.writeString(qty)
        parcel.writeString(price)
        parcel.writeString(subTotal)
        parcel.writeTypedList(itemOption)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderRefundItem> {
        override fun createFromParcel(parcel: Parcel): OrderRefundItem {
            return OrderRefundItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderRefundItem?> {
            return arrayOfNulls(size)
        }
    }


}