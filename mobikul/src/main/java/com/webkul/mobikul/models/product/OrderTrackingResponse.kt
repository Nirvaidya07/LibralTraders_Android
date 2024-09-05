package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderTrackingResponse() : Parcelable {
    @JsonProperty("carrier_title")
    var carrier_title: String? = ""
    @JsonProperty("tracking_number")
    var tracking_number: String? = ""
    @JsonProperty("track_summary")
    var track_summary: TrackSummary? = TrackSummary()

    constructor(parcel: Parcel) : this() {
        carrier_title = parcel.readString()
        tracking_number = parcel.readString()
        track_summary = parcel.readParcelable(TrackSummary::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(carrier_title)
        parcel.writeString(tracking_number)
        parcel.writeParcelable(track_summary, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderTrackingResponse> {
        override fun createFromParcel(parcel: Parcel): OrderTrackingResponse {
            return OrderTrackingResponse(parcel)
        }

        override fun newArray(size: Int): Array<OrderTrackingResponse?> {
            return arrayOfNulls(size)
        }
    }

}