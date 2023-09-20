package com.webkul.mobikul.models.homepage


import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.product.ProductTileData


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
class Carousel() : Parcelable {

    @JsonProperty("id")
    var id: String ?= ""

    @JsonProperty("type")
    var type: String ?= ""

    @JsonProperty("label")
    var label: String ?= ""

    @JsonProperty("color")
    var color: String ?= ""

    @JsonProperty("image")
    var image: String ?= ""

    @JsonProperty("productList")
    var productList: ArrayList<ProductTileData>? = ArrayList()

    @JsonProperty("banners")
    var banners: ArrayList<BannerImage> ?= ArrayList()

    @JsonProperty("featuredCategories")
    var featuredCategories: ArrayList<FeaturedCategory> ?= ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        type = parcel.readString()
        label = parcel.readString()
        color = parcel.readString()
        image = parcel.readString()
        productList = parcel.createTypedArrayList(ProductTileData.CREATOR)
        banners = parcel.createTypedArrayList(BannerImage.CREATOR)
        featuredCategories = parcel.createTypedArrayList(FeaturedCategory.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(label)
        parcel.writeString(color)
        parcel.writeString(image)
        parcel.writeTypedList(productList)
        parcel.writeTypedList(banners)
        parcel.writeTypedList(featuredCategories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Carousel> {
        override fun createFromParcel(parcel: Parcel): Carousel {
            return Carousel(parcel)
        }

        override fun newArray(size: Int): Array<Carousel?> {
            return arrayOfNulls(size)
        }
    }
}