package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class BookingData() : Parcelable {

    @JsonProperty("FromCenter")
    var FromCenter: String? = ""
    @JsonProperty("ToCenter")
    var ToCenter: String? = ""
    @JsonProperty("BookingDate")
    var BookingDate: String? = ""
    @JsonProperty("BookingTime")
    var BookingTime: String? = ""
    @JsonProperty("ReceiverName")
    var ReceiverName: String? = ""

    constructor(parcel: Parcel) : this() {
        FromCenter = parcel.readString()
        ToCenter = parcel.readString()
        BookingDate = parcel.readString()
        BookingTime = parcel.readString()
        ReceiverName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(FromCenter)
        parcel.writeString(ToCenter)
        parcel.writeString(BookingDate)
        parcel.writeString(BookingTime)
        parcel.writeString(ReceiverName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingData> {
        override fun createFromParcel(parcel: Parcel): BookingData {
            return BookingData(parcel)
        }

        override fun newArray(size: Int): Array<BookingData?> {
            return arrayOfNulls(size)
        }
    }
}