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
class CmsData() : Parcelable {

    @JsonProperty("id")

    var id: String ?= ""

    @JsonProperty("title")

    var title: String ?= ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        title = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CmsData> {
        override fun createFromParcel(parcel: Parcel): CmsData {
            return CmsData(parcel)
        }

        override fun newArray(size: Int): Array<CmsData?> {
            return arrayOfNulls(size)
        }
    }
}