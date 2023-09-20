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
class OptionsItem() : Parcelable {

    @JsonProperty("label")

    var label: String ?= ""

    @JsonProperty("value")

    var value: ArrayList<String>? = ArrayList()

    @JsonProperty("valueIds")

    var valueIds: ArrayList<String>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        label = parcel.readString()
        value = parcel.createStringArrayList()
        valueIds = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeStringList(value)
        parcel.writeStringList(valueIds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OptionsItem> {
        override fun createFromParcel(parcel: Parcel): OptionsItem {
            return OptionsItem(parcel)
        }

        override fun newArray(size: Int): Array<OptionsItem?> {
            return arrayOfNulls(size)
        }
    }
}