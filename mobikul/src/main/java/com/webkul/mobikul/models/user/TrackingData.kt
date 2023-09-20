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
class TrackingData() : Parcelable {

    @JsonProperty("id")

    var id: String? = ""

    @JsonProperty("carrier")

    var carrier: String? = ""

    @JsonProperty("title")

    var title: String ?= ""

    @JsonProperty("number")

    var number: String ?= ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        carrier = parcel.readString()
        title = parcel.readString()
        number = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(carrier)
        parcel.writeString(title)
        parcel.writeString(number)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrackingData> {
        override fun createFromParcel(parcel: Parcel): TrackingData {
            return TrackingData(parcel)
        }

        override fun newArray(size: Int): Array<TrackingData?> {
            return arrayOfNulls(size)
        }
    }
}