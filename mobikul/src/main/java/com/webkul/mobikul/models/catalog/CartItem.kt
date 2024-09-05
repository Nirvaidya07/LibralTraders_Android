/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.catalog


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.libraltraders.android.BR
import com.webkul.mobikul.models.user.OptionsItem

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

class CartItem() : BaseObservable(),Parcelable {

    @JsonProperty("thresholdQty")

    var thresholdQty: Int = 0

    @JsonProperty("remainingQty")

    var remainingQty: Double = 0.0

    @JsonProperty("image")

    var image: String ?= ""

    @JsonProperty("dominantColor")

    val dominantColor: String ?= ""

    @JsonProperty("name")

    var name: String ?= ""

    @JsonProperty("sku")

    var sku: String? = ""

    @JsonProperty("price")

    var price: Double = 0.0

    @JsonProperty("finalPrice")

    var finalPrice: Double = 0.0

    @JsonProperty("formattedPrice")

    var formattedPrice: String ?= ""

    @JsonProperty("formattedFinalPrice")

    var formattedFinalPrice: String ?= ""

    @JsonProperty("isInRange")

    var isInRange: Boolean = false

    @JsonProperty("qty")
    var qty: String ?= ""
        @Bindable get
        set(value) {
            cartItemQtyChanged = qty != value
            field = value
            if (value.isNullOrEmpty()) {
                field = "1"
            } else {
                if (value.toInt() < 1) {
                    field = "1"
                    return
                }
            }
            field = value
            notifyPropertyChanged(BR.qty)
        }

    @JsonProperty("productId")
    var productId: String ?= ""

    @JsonProperty("groupedProductId")
    var groupedProductId: String ?= ""

    @JsonProperty("typeId")
    var typeId: String ?= ""

    @JsonProperty("subTotal")
    var subTotal: String ?= ""

    @JsonProperty("id")
    var id: String ? = ""

    @JsonProperty("options")
    var optionItems: ArrayList<OptionsItem>?= ArrayList()

    @JsonProperty("messages")
    var messages: ArrayList<Message> = ArrayList()

    @JsonProperty("canMoveToWishlist")
    val canMoveToWishlist: Boolean = false

    var cartItemQtyChanged: Boolean = false

    constructor(parcel: Parcel) : this() {
        thresholdQty = parcel.readInt()
        remainingQty = parcel.readDouble()
        image = parcel.readString()
        name = parcel.readString()
        sku = parcel.readString()
        price = parcel.readDouble()
        finalPrice = parcel.readDouble()
        formattedPrice = parcel.readString()
        formattedFinalPrice = parcel.readString()
        isInRange = parcel.readByte() != 0.toByte()
        qty = parcel.readString()
        productId = parcel.readString()
        groupedProductId = parcel.readString()
        typeId = parcel.readString()
        subTotal = parcel.readString()
        id = parcel.readString()
        optionItems = parcel.createTypedArrayList(OptionsItem.CREATOR)
        cartItemQtyChanged = parcel.readByte() != 0.toByte()
    }

    fun isCartItemQtyChanged(): Boolean {
        return cartItemQtyChanged
    }

    fun isShowThreshold(): Boolean {
        return !(typeId == "configurable" || typeId == "bundle" || typeId == "grouped" || remainingQty > thresholdQty)
    }

    fun hasSpecialPrice(): Boolean {
        return finalPrice != 0.0 && finalPrice < price && isInRange
    }

    fun getDiscountPercentage(): String {
        return (100 - finalPrice / price * 100).toInt().toString() + "%"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(thresholdQty)
        parcel.writeDouble(remainingQty)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(sku)
        parcel.writeDouble(price)
        parcel.writeDouble(finalPrice)
        parcel.writeString(formattedPrice)
        parcel.writeString(formattedFinalPrice)
        parcel.writeByte(if (isInRange) 1 else 0)
        parcel.writeString(qty)
        parcel.writeString(productId)
        parcel.writeString(groupedProductId)
        parcel.writeString(typeId)
        parcel.writeString(subTotal)
        parcel.writeString(id)
        parcel.writeTypedList(optionItems)
        parcel.writeByte(if (cartItemQtyChanged) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}