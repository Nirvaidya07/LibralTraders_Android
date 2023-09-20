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
class RatingFormData() : Parcelable {

    @JsonProperty("id")

    var id: Int = 0

    @JsonProperty("name")

    var name: String?= ""

    @JsonProperty("values")

    var values: ArrayList<String> ?= ArrayList()

    var ratingValue: Float = 0f

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString()
        values = parcel.createStringArrayList()
        ratingValue = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeStringList(values)
        parcel.writeFloat(ratingValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RatingFormData> {
        override fun createFromParcel(parcel: Parcel): RatingFormData {
            return RatingFormData(parcel)
        }

        override fun newArray(size: Int): Array<RatingFormData?> {
            return arrayOfNulls(size)
        }
    }

    fun getSelectedRatingValue(): String {
        return values?.get((ratingValue - 1).toInt())?:""
    }
}