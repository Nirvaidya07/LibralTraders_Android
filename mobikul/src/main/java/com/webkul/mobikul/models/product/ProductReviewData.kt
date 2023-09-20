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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductReviewData() : Parcelable {
    @JsonProperty("title")

    var title: String  ?= ""

    @JsonProperty("details")

    var details: String  ?= ""

    @JsonProperty("ratings")

    var ratings: ArrayList<ProductRatingData> ? = ArrayList()

    @JsonProperty("reviewBy")

    var reviewBy: String  ?= ""

    @JsonProperty("reviewOn")

    var reviewOn: String ? = ""

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        details = parcel.readString()
        ratings = parcel.createTypedArrayList(ProductRatingData.CREATOR)
        reviewBy = parcel.readString()
        reviewOn = parcel.readString()
    }

    fun getRating(): String {
        var rating = 0.0
        ratings?.forEach { ratingData ->
            rating += ratingData.ratingValue
        }
        if (ratings?.size?:0 > 0)
            return (Math.round((rating / ratings!!.size) * 10) / 10.0).toString()
        else
            return rating.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(details)
        parcel.writeTypedList(ratings)
        parcel.writeString(reviewBy)
        parcel.writeString(reviewOn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductReviewData> {
        override fun createFromParcel(parcel: Parcel): ProductReviewData {
            return ProductReviewData(parcel)
        }

        override fun newArray(size: Int): Array<ProductReviewData?> {
            return arrayOfNulls(size)
        }
    }
}