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
class Attribute() : Parcelable {

    @JsonProperty("id")

    var id: String ?= ""

    @JsonProperty("code")

    var code: String? = ""

    @JsonProperty("label")

    var label: String ?= ""

    @JsonProperty("options")

    var options: ArrayList<ProductAttributeOption> ?= ArrayList()

    @JsonProperty("position")

    var position: String ?= ""

    @JsonProperty("swatchType")

    var swatchType: String? = ""

    @JsonProperty("updateProductPreviewImage")

    var updateProductPreviewImage: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        code = parcel.readString()
        label = parcel.readString()
        options = parcel.createTypedArrayList(ProductAttributeOption.CREATOR)
        position = parcel.readString()
        swatchType = parcel.readString()
        updateProductPreviewImage = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(code)
        parcel.writeString(label)
        parcel.writeTypedList(options)
        parcel.writeString(position)
        parcel.writeString(swatchType)
        parcel.writeByte(if (updateProductPreviewImage) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attribute> {
        override fun createFromParcel(parcel: Parcel): Attribute {
            return Attribute(parcel)
        }

        override fun newArray(size: Int): Array<Attribute?> {
            return arrayOfNulls(size)
        }
    }
}