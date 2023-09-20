package com.webkul.mobikul.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Brand() : Parcelable {

    @JsonProperty("value")
    var value: String? = null

    @JsonProperty("code")
    var code: String? = null

    @JsonProperty("label")
    var label: String? = null

    @JsonProperty("url")
    var url: String? = null

    @JsonProperty("img")
    var img: String? = null

    @JsonProperty("image")
    var image: String? = null

    @JsonProperty("dominantColor")
    var dominantColor: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("short_description")
    var shortDescription: String? = null

    @JsonProperty("cnt")
    var cnt: Int? = null

    @JsonProperty("alt")
    var alt: String? = null

    constructor(parcel: Parcel) : this() {
        value = parcel.readString()
        code = parcel.readString()
        label = parcel.readString()
        url = parcel.readString()
        img = parcel.readString()
        image = parcel.readString()
        dominantColor = parcel.readString()
        description = parcel.readString()
        shortDescription = parcel.readString()
        cnt = parcel.readValue(Int::class.java.classLoader) as? Int
        alt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(code)
        parcel.writeString(label)
        parcel.writeString(url)
        parcel.writeString(img)
        parcel.writeString(image)
        parcel.writeString(dominantColor)
        parcel.writeString(description)
        parcel.writeString(shortDescription)
        parcel.writeValue(cnt)
        parcel.writeString(alt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brand> {
        override fun createFromParcel(parcel: Parcel): Brand {
            return Brand(parcel)
        }

        override fun newArray(size: Int): Array<Brand?> {
            return arrayOfNulls(size)
        }
    }

}