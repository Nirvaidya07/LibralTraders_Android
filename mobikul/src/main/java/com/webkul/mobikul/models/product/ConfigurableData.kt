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
class ConfigurableData() : Parcelable {

    @JsonProperty("attributes")

    var attributes: ArrayList<Attribute> ?= ArrayList()

    @JsonProperty("template")

    var template: String ?= ""

    @JsonProperty("optionPrices")

    var optionPrices: ArrayList<OptionPrice>? = ArrayList()

    @JsonProperty("prices")

    var prices: Prices ?= Prices()

    @JsonProperty("id")

    var productId: String? = ""

    @JsonProperty("chooseText")

    var chooseText: String ?= ""

    @JsonProperty("images")

    var images: String ?= ""



    @JsonProperty("index")

    var index: String ?= ""

    @JsonProperty("swatchData")

    var swatchData: String ?= ""

    constructor(parcel: Parcel) : this() {
        attributes = parcel.createTypedArrayList(Attribute.CREATOR)
        template = parcel.readString()
        optionPrices = parcel.createTypedArrayList(OptionPrice.CREATOR)
        prices = parcel.readParcelable(Prices::class.java.classLoader)
        productId = parcel.readString()
        chooseText = parcel.readString()
        images = parcel.readString()
        index = parcel.readString()
        swatchData = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(attributes)
        parcel.writeString(template)
        parcel.writeTypedList(optionPrices)
        parcel.writeParcelable(prices, flags)
        parcel.writeString(productId)
        parcel.writeString(chooseText)
        parcel.writeString(images)
        parcel.writeString(index)
        parcel.writeString(swatchData)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConfigurableData> {
        override fun createFromParcel(parcel: Parcel): ConfigurableData {
            return ConfigurableData(parcel)
        }

        override fun newArray(size: Int): Array<ConfigurableData?> {
            return arrayOfNulls(size)
        }
    }
}