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
import com.fasterxml.jackson.annotation.JsonAlias
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
class OrderShipmentItem() : Parcelable {

    @JsonProperty("productId")

    var productId: String ?= ""

    @JsonProperty("name")
    @JsonAlias("name","productName")
    var name: String? = ""

    @JsonProperty("sku")

    var sku: String? = ""

    @JsonProperty("qty")

    var qty: String ?= ""

    @JsonProperty("price")

    var price: String ?= ""

    @JsonProperty("mpcodprice")

    var mpcodprice: String ?= ""

    @JsonProperty("totalPrice")

    var totalPrice: String ?= ""
    @JsonProperty("adminCommission")

    var adminCommission: String ?= ""

  @JsonProperty("subTotal")

    var subTotal: String ?= ""

    @JsonProperty("option")
    @JsonAlias("option","customOption")

    var itemOption: ArrayList<OptionsItem>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString()
        name = parcel.readString()
        sku = parcel.readString()
        qty = parcel.readString()
        itemOption = parcel.createTypedArrayList(OptionsItem.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(sku)
        parcel.writeString(qty)
        parcel.writeTypedList(itemOption)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderShipmentItem> {
        override fun createFromParcel(parcel: Parcel): OrderShipmentItem {
            return OrderShipmentItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderShipmentItem?> {
            return arrayOfNulls(size)
        }
    }
}