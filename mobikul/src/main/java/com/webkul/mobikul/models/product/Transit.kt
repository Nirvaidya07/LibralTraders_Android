package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Transit() : Parcelable {

    @JsonProperty("Job")
    var Job: String? = ""
    @JsonProperty("TransitDate")
    var TransitDate: String? = ""
    @JsonProperty("Route")
    var Route: String? = ""
    @JsonProperty("TransitTime")
    var TransitTime: String? = ""


    constructor(parcel: Parcel) : this() {
        Job = parcel.readString()
        TransitDate = parcel.readString()
        Route = parcel.readString()
        TransitTime = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Job)
        parcel.writeString(TransitDate)
        parcel.writeString(Route)
        parcel.writeString(TransitTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transit> {
        override fun createFromParcel(parcel: Parcel): Transit {
            return Transit(parcel)
        }

        override fun newArray(size: Int): Array<Transit?> {
            return arrayOfNulls(size)
        }
    }
}