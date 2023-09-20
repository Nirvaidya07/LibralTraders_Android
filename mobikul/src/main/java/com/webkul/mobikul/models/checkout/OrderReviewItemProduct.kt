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

package com.webkul.mobikul.models.checkout


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.user.OptionsItem

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderReviewItemProduct {

    @JsonProperty("productName")

    var productName: String = ""

    @JsonProperty("price")

    var price: String = ""

    @JsonProperty("unformattedPrice")

    var unformattedPrice: Double = 0.0

    @JsonProperty("originalPrice")

    var originalPrice: String = ""

    @JsonProperty("unformattedOriginalPrice")

    var unformattedOriginalPrice: Double = 0.0

    @JsonProperty("qty")

    var qty: String = ""

    @JsonProperty("sku")

    var sku: String = ""

    @JsonProperty("subTotal")

    var subTotal: String = ""

    @JsonProperty("thumbNail")

    val thumbNail: String = ""

    @JsonProperty("dominantColor")

    val dominantColor: String = ""

    @JsonProperty("option")

    var itemOption: ArrayList<OptionsItem> = ArrayList()

    fun hasSpecialPrice(): Boolean {
        return unformattedPrice != 0.0 && unformattedPrice < unformattedOriginalPrice
    }
}