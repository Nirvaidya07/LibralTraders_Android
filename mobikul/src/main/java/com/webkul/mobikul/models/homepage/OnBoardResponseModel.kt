package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OnBoardResponseModel() : BaseModel(), Parcelable {

    @JsonProperty("walkthroughVersion")
    var walkThroughVersion: String ?= ""

    @JsonProperty("walkthroughData")
    var walkThroughData: ArrayList<OnBoardListData>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        walkThroughVersion = parcel.readString()
        walkThroughData = parcel.createTypedArrayList(OnBoardListData.CREATOR)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(walkThroughVersion)
        parcel.writeTypedList(walkThroughData)
    }

    companion object CREATOR : Parcelable.Creator<OnBoardResponseModel> {
        override fun createFromParcel(parcel: Parcel): OnBoardResponseModel {
            return OnBoardResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<OnBoardResponseModel?> {
            return arrayOfNulls(size)
        }
    }


}