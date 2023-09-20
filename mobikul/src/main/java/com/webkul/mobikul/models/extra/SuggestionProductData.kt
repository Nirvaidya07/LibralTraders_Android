package com.webkul.mobikul.models.extra


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
class SuggestionProductData {

    @JsonProperty("productName")

    val productName: String = ""

    @JsonProperty("productId")

    val productId: String = ""

    @JsonProperty("thumbNail")

    val thumbNail: String = ""

    @JsonProperty("dominantColor")

    val dominantColor: String = ""

    @JsonProperty("price")

    val price: String = ""

    @JsonProperty("hasSpecialPrice")

    val hasSpecialPrice: Boolean = false
        get() {
            if (price == specialPrice)
                return false
            else
                return field
        }

    @JsonProperty("specialPrice")

    val specialPrice: String = ""
}