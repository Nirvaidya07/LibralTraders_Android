package com.webkul.mobikul.models.product


import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.libraltraders.android.BR
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants

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
@SuppressWarnings("unused")

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductTileData() : Parcelable, BaseObservable() {

    @JsonProperty("configurableData")

    var configurableData: ConfigurableData  ?= ConfigurableData()

    @JsonProperty("isInWishlist")

    var isInWishList: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.inWishList)
        }

    @JsonProperty("wishlistItemId")

    var wishListItemId: String  ?= ""

    @JsonProperty("typeId")

    var typeId: String  ?= ""

    @JsonProperty(value = "entityId")
    @JsonAlias("entityId", "id")
    var id: String  ?= ""
        get() = field?:""
    @JsonProperty("shortDescription")
    var shortDescription: String ? = ""

    @JsonProperty("rating")

    var rating: String ? = ""

    @JsonProperty("isAvailable")

    var isAvailable: Boolean = false

    @JsonProperty("isNew")

    var isNew: Boolean = false

    @JsonProperty("price")

    var price: Double = 0.0

    @JsonProperty("finalPrice")

    var finalPrice: Double = 0.0

    @JsonProperty("formattedPrice")

    var formattedPrice: String ? = ""

    @JsonProperty("formattedFinalPrice")

    var formattedFinalPrice: String ? = ""

    @JsonProperty("name")

    var name: String  ?= ""
        get() = field?:""

    @JsonProperty("hasRequiredOptions")

    var hasRequiredOptions: Boolean = false

    @JsonProperty("isInRange")

    var isInRange: Boolean = false

    @JsonProperty("thumbNail")
    @JsonAlias("thumbNail", "image_link")

    var thumbNail: String ? = ""

    @JsonProperty("dominantColor")

    var dominantColor: String ? = ""

    @JsonProperty("minAddToCartQty")

    var minAddToCartQty: Int = 1

    @JsonProperty("priceView")

    var priceView: String  ?= ""

    @JsonProperty("minPrice")

    var minPrice: Double = 0.0

    @JsonProperty("maxPrice")

    var maxPrice: Double = 0.0

    @JsonProperty("formattedMinPrice")

    var formattedMinPrice: String ? = ""

    @JsonProperty("formattedMaxPrice")

    var formattedMaxPrice: String  ?= ""

    @JsonProperty("groupedPrice")

    var groupedPrice: String  ?= ""

    @JsonProperty("availability")

    var availability: String ? = ""

    @JsonProperty("arType")

    var arType: String  ?= ""

    @JsonProperty("arUrl")

    var arModelUrl: String  ?= ""

    var productPosition = -1
    var addToCart = false // For related products only

    constructor(parcel: Parcel) : this() {
        configurableData = parcel.readParcelable(ConfigurableData::class.java.classLoader)
        isInWishList = parcel.readByte() != 0.toByte()
        wishListItemId = parcel.readString()
        typeId = parcel.readString()
        id = parcel.readString()
        shortDescription = parcel.readString()
        rating = parcel.readString()
        isAvailable = parcel.readByte() != 0.toByte()
        isNew = parcel.readByte() != 0.toByte()
        price = parcel.readDouble()
        finalPrice = parcel.readDouble()
        formattedPrice = parcel.readString()
        formattedFinalPrice = parcel.readString()
        name = parcel.readString()
        hasRequiredOptions = parcel.readByte() != 0.toByte()
        isInRange = parcel.readByte() != 0.toByte()
        thumbNail = parcel.readString()
        dominantColor = parcel.readString()
        minAddToCartQty = parcel.readInt()
        priceView = parcel.readString()
        minPrice = parcel.readDouble()
        maxPrice = parcel.readDouble()
        formattedMinPrice = parcel.readString()
        formattedMaxPrice = parcel.readString()
        groupedPrice = parcel.readString()
        availability = parcel.readString()
        arType = parcel.readString()
        arModelUrl = parcel.readString()
        productPosition = parcel.readInt()
        addToCart = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(configurableData as Parcelable?, flags)
        parcel.writeByte(if (isInWishList) 1 else 0)
        parcel.writeString(wishListItemId)
        parcel.writeString(typeId)
        parcel.writeString(id)
        parcel.writeString(shortDescription)
        parcel.writeString(rating)
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeByte(if (isNew) 1 else 0)
        parcel.writeDouble(price)
        parcel.writeDouble(finalPrice)
        parcel.writeString(formattedPrice)
        parcel.writeString(formattedFinalPrice)
        parcel.writeString(name)
        parcel.writeByte(if (hasRequiredOptions) 1 else 0)
        parcel.writeByte(if (isInRange) 1 else 0)
        parcel.writeString(thumbNail)
        parcel.writeString(dominantColor)
        parcel.writeInt(minAddToCartQty)
        parcel.writeString(priceView)
        parcel.writeDouble(minPrice)
        parcel.writeDouble(maxPrice)
        parcel.writeString(formattedMinPrice)
        parcel.writeString(formattedMaxPrice)
        parcel.writeString(groupedPrice)
        parcel.writeString(availability)
        parcel.writeString(arType)
        parcel.writeString(arModelUrl)
        parcel.writeInt(productPosition)
        parcel.writeByte(if (addToCart) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductTileData> {
        override fun createFromParcel(parcel: Parcel): ProductTileData {
            return ProductTileData(parcel)
        }

        override fun newArray(size: Int): Array<ProductTileData?> {
            return arrayOfNulls(size)
        }
    }

    fun hasSpecialPrice(): Boolean {
        return finalPrice != 0.0 && finalPrice < price && isInRange
    }

    fun hasPrice(): Boolean {
        return !(hasMinPrice() || hasGroupedPrice())
    }

    fun hasMinPrice(): Boolean {
        return minPrice != 0.0
    }

    fun hasMaxPrice(): Boolean {
        return maxPrice != 0.0
    }

    fun hasMinMaxRange(): Boolean {
        return minPrice < maxPrice
    }

    fun hasGroupedPrice(): Boolean {
        return !groupedPrice.isNullOrEmpty()
    }

    fun getDiscountPercentage(): String {
        return Math.round((100 - finalPrice / price * 100)).toString() + "%"

    }
    fun newGroupPrice():String{
        Log.d("TAG", "newGroupPrice: ${name}>>$groupedPrice")
        return "Starting at : $groupedPrice";
    }

    fun newFormattedPrice():String{
        Log.d("TAG", "newGroupPrice2: ${formattedMinPrice}>>$formattedFinalPrice>>${formattedMaxPrice}>>${price}")
        return "Starting at : $formattedFinalPrice";
    }

    fun isArEnabled(context: Context): Boolean {
        if (ApplicationConstants.ENABLE_AR_CORE) {
            if (arModelUrl.isNullOrEmpty()) {
                return false
            } else {
                if (arType == "2D") {
                    return true
                } else if (arType == "3D" && AppSharedPref.getIsArSupported(context)) {
                    return true
                }
                return false
            }
        } else {
            return false
        }
    }

    fun getRequiredOptions(): Boolean {
        return if (typeId.equals("configurable", ignoreCase = true) || typeId.equals("grouped", ignoreCase = true) || typeId.equals("bundle", ignoreCase = true)) {
            true
        } else {
            hasRequiredOptions
        }
    }
}