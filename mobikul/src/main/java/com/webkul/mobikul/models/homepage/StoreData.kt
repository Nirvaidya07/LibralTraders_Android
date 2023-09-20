package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
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
class StoreData() : Parcelable, BaseObservable() {

    @JsonProperty("id")

    var id: String ?= ""

    @JsonProperty("name")

    var name: String ??= ""

    @JsonProperty("code")

    var code: String ?= ""

    @JsonProperty("icon")

    var icon: String ?= ""

    @JsonProperty("stores")

    var stores: ArrayList<StoreData> ?= ArrayList()

    @Bindable
    var isExpanded: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        code = parcel.readString()
        icon = parcel.readString()
        stores = parcel.createTypedArrayList(StoreData.CREATOR)
        isExpanded = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeString(icon)
        parcel.writeTypedList(stores)
        parcel.writeByte(if (isExpanded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreData> {
        override fun createFromParcel(parcel: Parcel): StoreData {
            return StoreData(parcel)
        }

        override fun newArray(size: Int): Array<StoreData?> {
            return arrayOfNulls(size)
        }
    }
}