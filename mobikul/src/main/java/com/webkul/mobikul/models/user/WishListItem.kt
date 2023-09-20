package com.webkul.mobikul.models.user


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
class WishListItem : BaseObservable() {

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("name")

    var name: String = ""

    @JsonProperty("description")

    var description: String? = ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
        }

    @JsonProperty("sku")

    var sku: String = ""

    @JsonProperty("productId")

    var productId: String = ""

    @JsonProperty("typeId")

    var typeId: String = ""

    @JsonProperty("qty")

    var qty: String? = ""
        @Bindable get() = field
        set(value) {
            field = value
            if (value!!.isEmpty()) {
                field = "0"
            } else {
                if (Integer.parseInt(value) < 0) {
                    field = "0"
                    return
                }
            }
            field = value
            notifyPropertyChanged(BR.qty)
        }

    @JsonProperty("price")

    var price: Double = 0.0

    @JsonProperty("finalPrice")

    var finalPrice: Double = 0.0

    @JsonProperty("formattedPrice")

    var formattedPrice: String = ""

    @JsonProperty("formattedFinalPrice")

    var formattedFinalPrice: String = ""

    @JsonProperty("isInRange")

    var isInRange: Boolean = false

    @JsonProperty("thumbNail")

    var thumbNail: String = ""

    @JsonProperty("dominantColor")

    var dominantColor: String = ""

    @JsonProperty("rating")

    var rating: Float = 0f

    @JsonProperty("options")

    var itemOption: ArrayList<OptionsItem> = ArrayList()

    @JsonProperty("isAvailable")

    var isAvailable: Boolean = false

    @JsonProperty("availability")

    var availability: String = ""

    fun hasSpecialPrice(): Boolean {
        return finalPrice != 0.0 && finalPrice < price && isInRange
    }

    fun getDiscountPercentage(): String {
        return (100 - finalPrice / price * 100).toInt().toString() + "%"
    }
}