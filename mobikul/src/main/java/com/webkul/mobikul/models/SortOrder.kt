package com.webkul.mobikul.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class SortOrder() : Parcelable {
    @JsonProperty("layout_id")
    var layout_id: String? = null

    @JsonProperty("position")
    var position: String? = ""

    @JsonProperty("type")
    var type: String? = null

    val positionArray: Array<String>?
        get() {
            return position?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        }

    constructor(parcel: Parcel) : this() {
        layout_id = parcel.readString()
        position = parcel.readString()
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(layout_id)
        parcel.writeString(position)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SortOrder> {
        override fun createFromParcel(parcel: Parcel): SortOrder {
            return SortOrder(parcel)
        }

        override fun newArray(size: Int): Array<SortOrder?> {
            return arrayOfNulls(size)
        }
    }

}