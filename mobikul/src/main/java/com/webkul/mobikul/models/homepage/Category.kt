package com.webkul.mobikul.models.homepage

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR


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
class Category() : Parcelable, BaseObservable() {

    @JsonProperty("id")
    var id: String ?= ""

    @JsonProperty("name")
    var name: String ?= ""

    @JsonProperty("thumbnail")
    var thumbnail: String ?= ""

    @JsonProperty("thumbnailDominantColor")
    var thumbnailDominantColor: String? = ""

    @JsonProperty("hasChildren")
    var hasChildren: Boolean = false

    @JsonProperty("childCategories")
    val childCategories: ArrayList<Category> = ArrayList()


    var isExpanded: Boolean = false
    @Bindable get
    set(value) {
        field=value
        notifyPropertyChanged(BR.isExpanded)
    }


    var isParentExpanded: Boolean = false


    @JsonProperty("bannerImage")
    val bannerImage: ArrayList<BannerImage> = ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        thumbnail = parcel.readString()
        thumbnailDominantColor = parcel.readString()
        hasChildren = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(thumbnail)
        parcel.writeString(thumbnailDominantColor)
        parcel.writeByte(if (hasChildren) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}