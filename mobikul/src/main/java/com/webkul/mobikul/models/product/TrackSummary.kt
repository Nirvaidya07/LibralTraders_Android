package com.webkul.mobikul.models.product

import TransitHistoryModel
import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TrackSummary() : Parcelable {
    @JsonProperty("AwbNumber")
    var AwbNumber: String? = ""
    @JsonProperty("BookingData")
    var BookingData: BookingData = BookingData()
    @JsonProperty("TransitHistory")
    var TransitHistory: TransitHistoryModel? = TransitHistoryModel()
    @JsonProperty("Status")
    var Status: String? = ""

    constructor(parcel: Parcel) : this() {
        AwbNumber = parcel.readString()
        BookingData = parcel.readParcelable(BookingData::class.java.classLoader)!!
        TransitHistory = parcel.readParcelable(TransitHistoryModel::class.java.classLoader)

        Status = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(AwbNumber)
        parcel.writeParcelable(BookingData, flags)
        parcel.writeParcelable(TransitHistory, flags)
        parcel.writeString(Status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrackSummary> {
        override fun createFromParcel(parcel: Parcel): TrackSummary {
            return TrackSummary(parcel)
        }

        override fun newArray(size: Int): Array<TrackSummary?> {
            return arrayOfNulls(size)
        }
    }
}