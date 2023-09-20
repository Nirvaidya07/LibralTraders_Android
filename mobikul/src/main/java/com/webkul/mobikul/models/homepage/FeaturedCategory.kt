package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
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
class FeaturedCategory() : Parcelable {

    @JsonProperty("url")

    var url: String ?= ""

    @JsonProperty("categoryId")

    var categoryId: String? = ""

    @JsonProperty("categoryName")

    var categoryName: String ?= ""

    @JsonProperty("dominantColor")

    var dominantColor: String ?= ""

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        categoryId = parcel.readString()
        categoryName = parcel.readString()
        dominantColor = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(categoryId)
        parcel.writeString(categoryName)
        parcel.writeString(dominantColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeaturedCategory> {
        override fun createFromParcel(parcel: Parcel): FeaturedCategory {
            return FeaturedCategory(parcel)
        }

        override fun newArray(size: Int): Array<FeaturedCategory?> {
            return arrayOfNulls(size)
        }
    }
}