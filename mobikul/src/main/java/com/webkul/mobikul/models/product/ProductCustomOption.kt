/*
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

package com.webkul.mobikul.models.product

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductCustomOption {
    @JsonProperty("optionValues")
    @JsonIgnore
    var optionValues: ArrayList<ProductCustomOptionValues>? = ArrayList()
        get() = field ?: ArrayList()

    @JsonProperty("option_id")
    var option_id: String? = ""
        get() = field ?: ""

    @JsonProperty("product_id")
    @JsonIgnore
    var product_id: String? = ""
        get() = field ?: ""

    @JsonProperty("type")
    var type: String? = ""
        get() = field ?: ""

    @JsonProperty("is_require")
    @JsonIgnore
    var is_require: Int = 0

    @JsonProperty("sku")
    @JsonIgnore
    var sku: String? = ""
        get() = field

    @JsonProperty("max_characters")
    @JsonIgnore
    var max_characters: String? = ""
        get() = field ?: ""

    @JsonProperty("file_extension")
    var file_extension: String? = ""
        get() = field ?: ""

    @JsonProperty("image_size_x")
    var image_size_x: String? = ""
        get() = field ?: ""

    @JsonProperty("image_size_y")
    var image_size_y: String? = ""
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
    var default_price: Double? = 0.0
        get() = field ?: 0.0

    @JsonProperty("default_price_type")
    var defaultPriceType: String? = ""
        get() = field ?: ""

    @JsonProperty("store_price")
    var storePrice: Double = 0.0

    @JsonProperty("store_price_type")
    var storePriceType: String? = ""
        get() = field ?: ""

    @JsonProperty("price")
    var price: String? = ""
        get() = field ?: ""

    @JsonProperty("price_type")
    var price_type: String? = ""
        get() = field ?: ""

    @JsonProperty("decorated_is_first")
    var decoratedIsFirst: Boolean? = false
        get() = field ?: false

    @JsonProperty("decorated_is_odd")
    var decoratedIsOdd: Boolean? = false
        get() = field ?: false

    @JsonProperty("decorated_is_last")
    var decoratedIsLast: Boolean? = false
        get() = field ?: false

    @JsonProperty("unformatted_default_price")
    var unformatted_default_price: Double? = 0.0
        get() = field ?: 0.0

    @JsonProperty("formatted_default_price")
    var formatted_default_price: String? = ""
        get() = field ?: ""

    @JsonProperty("unformatted_price")
    var unformattedPrice: Double? = 0.0
        get() = field ?: 0.0

    @JsonProperty("formatted_price")
    var formatted_price: String? = ""
        get() = field ?: ""

    @JsonProperty("decorated_is_even")
    var decoratedIsEven: Boolean? = false
        get() = field ?: false
    @JsonProperty("is_ar")
    var isAr: Boolean = false
}