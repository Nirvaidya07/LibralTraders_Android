package com.webkul.mobikul.models.product

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
class OptionPrice() : Parcelable {

    @JsonProperty("oldPrice")

    var oldPrice: Price ?= Price()

    @JsonProperty("basePrice")

    var basePrice: Price  ?= Price()

    @JsonProperty("finalPrice")

    var finalPrice: Price  ?= Price()

    @JsonProperty("tierPrices")

    var tierPrices: ArrayList<Any>  ?= ArrayList()

    @JsonProperty("product")

    var product: Int = 0

    constructor(parcel: Parcel) : this() {
        oldPrice = parcel.readParcelable(Price::class.java.classLoader)
        basePrice = parcel.readParcelable(Price::class.java.classLoader)
        finalPrice = parcel.readParcelable(Price::class.java.classLoader)
        product = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(oldPrice, flags)
        parcel.writeParcelable(basePrice, flags)
        parcel.writeParcelable(finalPrice, flags)
        parcel.writeInt(product)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OptionPrice> {
        override fun createFromParcel(parcel: Parcel): OptionPrice {
            return OptionPrice(parcel)
        }

        override fun newArray(size: Int): Array<OptionPrice?> {
            return arrayOfNulls(size)
        }
    }
}