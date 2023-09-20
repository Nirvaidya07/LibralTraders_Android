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
class ShipmentItem() : Parcelable {

    @JsonProperty("id")

    var id: String? = ""

    @JsonProperty("incrementId")

    var incrementId: String? = ""

    @JsonProperty("items")

    var items: ArrayList<OrderShipmentItem>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        incrementId = parcel.readString()
        items = parcel.createTypedArrayList(OrderShipmentItem.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(incrementId)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShipmentItem> {
        override fun createFromParcel(parcel: Parcel): ShipmentItem {
            return ShipmentItem(parcel)
        }

        override fun newArray(size: Int): Array<ShipmentItem?> {
            return arrayOfNulls(size)
        }
    }
}