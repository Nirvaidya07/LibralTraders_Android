package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
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
class BannerImage() : Parcelable {

    @JsonProperty("url")
    @JsonAlias("bannerImage")
    var url: String? = ""

    @JsonProperty("dominantColor")

    var dominantColor: String ?= ""

    @JsonProperty("title")

    var title: String ?= ""

    @JsonProperty("bannerType")

    var bannerType: String ?= ""

    @JsonProperty("id")

    var id: String ?= ""

    @JsonProperty("name")

    var name: String ?= ""

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        dominantColor = parcel.readString()
        title = parcel.readString()
        bannerType = parcel.readString()
        id = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(dominantColor)
        parcel.writeString(title)
        parcel.writeString(bannerType)
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BannerImage> {
        override fun createFromParcel(parcel: Parcel): BannerImage {
            return BannerImage(parcel)
        }

        override fun newArray(size: Int): Array<BannerImage?> {
            return arrayOfNulls(size)
        }
    }
}