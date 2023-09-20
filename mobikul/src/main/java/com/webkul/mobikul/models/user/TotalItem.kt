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
class TotalItem() : Parcelable {

    @JsonProperty("code")

    var code: String ?= ""

    @JsonProperty("label")

    var label: String ?= ""

    @JsonProperty("value")

    var value: String ?= ""

    @JsonProperty("formattedValue")

    var formattedValue: String? = ""

    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        label = parcel.readString()
        value = parcel.readString()
        formattedValue = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(label)
        parcel.writeString(value)
        parcel.writeString(formattedValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TotalItem> {
        override fun createFromParcel(parcel: Parcel): TotalItem {
            return TotalItem(parcel)
        }

        override fun newArray(size: Int): Array<TotalItem?> {
            return arrayOfNulls(size)
        }
    }
}