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
class ProductAttributeOption() : Parcelable {
    @JsonProperty("id")

    var id: String  ?= ""

    @JsonProperty("label")

    var label: String  ?= ""

    @JsonProperty("products")

    var products: ArrayList<String> ? = ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        label = parcel.readString()
        products = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(label)
        parcel.writeStringList(products)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductAttributeOption> {
        override fun createFromParcel(parcel: Parcel): ProductAttributeOption {
            return ProductAttributeOption(parcel)
        }

        override fun newArray(size: Int): Array<ProductAttributeOption?> {
            return arrayOfNulls(size)
        }
    }
}