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
class PriceFormat() : Parcelable {

    @JsonProperty("pattern")

    var pattern: String? = ""

    @JsonProperty("precision")

    var precision: Int = 0

    @JsonProperty("requiredPrecision")

    var requiredPrecision: Int = 0

    @JsonProperty("decimalSymbol")

    var decimalSymbol: String? = ""

    @JsonProperty("groupSymbol")

    var groupSymbol: String ?= ""

    @JsonProperty("groupLength")

    var groupLength: Int = 0

    constructor(parcel: Parcel) : this() {
        pattern = parcel.readString()
        precision = parcel.readInt()
        requiredPrecision = parcel.readInt()
        decimalSymbol = parcel.readString()
        groupSymbol = parcel.readString()
        groupLength = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pattern)
        parcel.writeInt(precision)
        parcel.writeInt(requiredPrecision)
        parcel.writeString(decimalSymbol)
        parcel.writeString(groupSymbol)
        parcel.writeInt(groupLength)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PriceFormat> {
        override fun createFromParcel(parcel: Parcel): PriceFormat {
            return PriceFormat(parcel)
        }

        override fun newArray(size: Int): Array<PriceFormat?> {
            return arrayOfNulls(size)
        }
    }
}