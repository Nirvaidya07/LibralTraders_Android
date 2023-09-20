package com.webkul.mobikul.models.user

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


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
class OrderItem() : Parcelable {

    @JsonProperty("productId")

    var productId: String ?= ""

    @JsonProperty("name")

    var name: String ?= ""

    @JsonProperty("sku")

    var sku: String? = ""

    @JsonProperty("image")

    var imageUrl: String ?= ""

    @JsonProperty("dominantColor")

    var dominantColor: String? = ""

    @JsonProperty("price")

    var price: String? = ""

    @JsonProperty("qty")

    var qty: OrderQty ?= OrderQty()

    @JsonProperty("subTotal")

    var subTotal: String? = ""

    @JsonProperty("option")

    var itemOption: ArrayList<OptionsItem>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString()
        name = parcel.readString()
        sku = parcel.readString()
        imageUrl = parcel.readString()
        dominantColor = parcel.readString()
        price = parcel.readString()
        qty = parcel.readParcelable(OrderQty::class.java.classLoader)
        subTotal = parcel.readString()
        itemOption = parcel.createTypedArrayList(OptionsItem.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(sku)
        parcel.writeString(imageUrl)
        parcel.writeString(dominantColor)
        parcel.writeString(price)
        parcel.writeParcelable(qty, flags)
        parcel.writeString(subTotal)
        parcel.writeTypedList(itemOption)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderItem> {
        override fun createFromParcel(parcel: Parcel): OrderItem {
            return OrderItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderItem?> {
            return arrayOfNulls(size)
        }
    }
}