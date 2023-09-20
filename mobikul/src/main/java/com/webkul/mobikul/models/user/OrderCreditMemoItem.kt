/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.user

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderCreditMemoItem() : Parcelable {

    @JsonProperty("productId")

    var productId: String ?= ""

    @JsonProperty("name")

    var name: String ?= ""

    @JsonProperty("sku")

    var sku: String ?= ""

    @JsonProperty("qty")

    var qty: String ?= ""

    @JsonProperty("option")

    var itemOption: ArrayList<OptionsItem> ?= ArrayList()

    @JsonProperty("price")

    var price: String ?= ""

    @JsonProperty("subTotal")

    var subTotal: String? = ""

    @JsonProperty("discountAmount")

    var discountAmount: String? = ""

    @JsonProperty("rowTotal")

    var rowTotal: String ?= ""

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString()
        name = parcel.readString()
        sku = parcel.readString()
        qty = parcel.readString()
        itemOption = parcel.createTypedArrayList(OptionsItem.CREATOR)
        price = parcel.readString()
        subTotal = parcel.readString()
        discountAmount = parcel.readString()
        rowTotal = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(sku)
        parcel.writeString(qty)
        parcel.writeTypedList(itemOption)
        parcel.writeString(price)
        parcel.writeString(subTotal)
        parcel.writeString(discountAmount)
        parcel.writeString(rowTotal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderCreditMemoItem> {
        override fun createFromParcel(parcel: Parcel): OrderCreditMemoItem {
            return OrderCreditMemoItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderCreditMemoItem?> {
            return arrayOfNulls(size)
        }
    }
}