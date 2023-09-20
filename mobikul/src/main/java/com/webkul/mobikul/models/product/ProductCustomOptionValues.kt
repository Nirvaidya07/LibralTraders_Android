/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkula
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.product


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductCustomOptionValues {

    @JsonProperty("option_type_id")
    var optionTypeId: String = ""

    @JsonProperty("option_id")
    var optionId: String = ""

    @JsonProperty("sku")
    var sku: String? = ""
        get() = field ?: ""

    @JsonProperty("sort_order")
    var sortOrder: String? = ""
        get() = field ?: ""

    @JsonProperty("default_title")
    var defaultTitle: String? = ""
        get() = field ?: ""

    @JsonProperty("store_title")
    var storeTitle: String? = ""
        get() = field ?: ""

    @JsonProperty("title")
    var title: String? = ""
        get() = field ?: ""

    @JsonProperty("default_price")
    var defaultPrice: Double = 0.0

    @JsonProperty("default_price_type")
    var defaultPriceType: String = ""

    @JsonProperty("store_price")
    var storePrice: Double? = 0.0
        get() = field ?: 0.0

    @JsonProperty("store_price_type")
    var storePriceType: String? = ""
        get() = field ?: ""

    @JsonProperty("price")
    var price: String = ""

    @JsonProperty("price_type")
    var priceType: String = ""

    @JsonProperty("formatted_price")
    var formattedPrice: String = ""

    @JsonProperty("formatted_default_price")
    var formattedDefaultPrice: String = ""
}