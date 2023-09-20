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
class OrderInvoiceItem() : Parcelable {

    @JsonProperty("productId")

    var productId: String ?= ""

    @JsonProperty("name")

    var name: String ?= ""

    @JsonProperty("sku")

    var sku: String ?= ""

    @JsonProperty("qty")

    var qty: String ?= ""

    @JsonProperty("price")

    var price: String ?= ""

    @JsonProperty("subTotal")

    var subTotal: String? = ""

    @JsonProperty("option")

    var itemOption: ArrayList<OptionsItem>? = ArrayList()

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

    companion object CREATOR : Parcelable.Creator<OrderInvoiceItem> {
        override fun createFromParcel(parcel: Parcel): OrderInvoiceItem {
            return OrderInvoiceItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderInvoiceItem?> {
            return arrayOfNulls(size)
        }
    }
}