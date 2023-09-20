/*
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

package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductRatingData() : Parcelable {

    @JsonProperty(value = "ratingCode")
    @JsonAlias("ratingCode", "label")

    var ratingCode: String  ?= ""

    @JsonProperty(value = "ratingValue")
    @JsonAlias("ratingValue", "value")

    var ratingValue: Double = 0.0

    constructor(parcel: Parcel) : this() {
        ratingCode = parcel.readString()
        ratingValue = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ratingCode)
        parcel.writeDouble(ratingValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductRatingData> {
        override fun createFromParcel(parcel: Parcel): ProductRatingData {
            return ProductRatingData(parcel)
        }

        override fun newArray(size: Int): Array<ProductRatingData?> {
            return arrayOfNulls(size)
        }
    }
}