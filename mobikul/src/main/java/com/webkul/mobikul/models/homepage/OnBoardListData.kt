package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OnBoardListData() : Parcelable {

    @JsonProperty("id")
    var id: String? = ""

    @JsonProperty("image")
    var image: String? = ""

    @JsonProperty("title")
    var title: String? = ""

    @JsonProperty("content")
    @JsonAlias("description","content")
    var content: String? = ""

    @JsonProperty("colorCode")
    var colorCode: String? = "#FFFFFF"
        get() {
            return  if (!field.isNullOrEmpty() && field?.startsWith("#") ==false) "#$field" else if (field.isNullOrEmpty()) "#FFFFFF" else field
        }

    @JsonProperty("imageDominantColor")
    var imageDominantColor: String? =""

    @JsonProperty("sortOrder")
    var sortOrder: String? = ""

    @JsonProperty("status")
    var status: String? = ""

    @JsonProperty("created_at")
    var createdAt: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        image = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
        content = parcel.readString() ?: ""
        colorCode = parcel.readString() ?: ""
        imageDominantColor = parcel.readString() ?: ""
        sortOrder = parcel.readString() ?: ""
        status = parcel.readString() ?: ""
        createdAt = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(colorCode)
        parcel.writeString(imageDominantColor)
        parcel.writeString(sortOrder)
        parcel.writeString(status)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OnBoardListData> {
        override fun createFromParcel(parcel: Parcel): OnBoardListData {
            return OnBoardListData(parcel)
        }

        override fun newArray(size: Int): Array<OnBoardListData?> {
            return arrayOfNulls(size)
        }
    }
}