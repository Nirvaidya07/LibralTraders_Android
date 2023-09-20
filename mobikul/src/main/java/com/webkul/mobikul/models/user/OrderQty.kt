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
class OrderQty() : Parcelable {

    @JsonProperty("Ordered")

    var ordered: String ?= ""

    @JsonProperty("Shipped")

    var shipped: String ?= ""

    @JsonProperty("Canceled")

    var canceled: String? = ""

    @JsonProperty("Refunded")

    var refunded: String? = ""

    constructor(parcel: Parcel) : this() {
        ordered = parcel.readString()
        shipped = parcel.readString()
        canceled = parcel.readString()
        refunded = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ordered)
        parcel.writeString(shipped)
        parcel.writeString(canceled)
        parcel.writeString(refunded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderQty> {
        override fun createFromParcel(parcel: Parcel): OrderQty {
            return OrderQty(parcel)
        }

        override fun newArray(size: Int): Array<OrderQty?> {
            return arrayOfNulls(size)
        }
    }
}