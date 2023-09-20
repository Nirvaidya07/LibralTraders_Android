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
class InvoiceItem() : Parcelable {

    @JsonProperty("id")

    var id: String? = ""

 @JsonProperty("incrementId")

    var incrementId: String? = ""

    @JsonProperty("items")

    var items: ArrayList<OrderItem>? = ArrayList()

    @JsonProperty("sku")

    var sku: String ?= ""

    @JsonProperty("price")

    var price: String ?= ""

    @JsonProperty("qty")

    var qty: String ?= ""

    @JsonProperty("subTotal")

    var subTotal: String ?= ""

    @JsonProperty("totals")

    var totals: ArrayList<TotalItem>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        incrementId = parcel.readString()
        items = parcel.createTypedArrayList(OrderItem.CREATOR)
        sku = parcel.readString()
        price = parcel.readString()
        qty = parcel.readString()
        subTotal = parcel.readString()
        totals = parcel.createTypedArrayList(TotalItem.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(incrementId)
        parcel.writeTypedList(items)
        parcel.writeString(sku)
        parcel.writeString(price)
        parcel.writeString(qty)
        parcel.writeString(subTotal)
        parcel.writeTypedList(totals)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InvoiceItem> {
        override fun createFromParcel(parcel: Parcel): InvoiceItem {
            return InvoiceItem(parcel)
        }

        override fun newArray(size: Int): Array<InvoiceItem?> {
            return arrayOfNulls(size)
        }
    }
}