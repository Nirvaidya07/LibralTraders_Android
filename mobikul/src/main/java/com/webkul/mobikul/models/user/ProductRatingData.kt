package com.webkul.mobikul.models.user


import com.fasterxml.jackson.annotation.JsonAlias
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
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductRatingData {

    @JsonProperty(value = "ratingCode")
    @JsonAlias("ratingCode", "label")
    var ratingCode: String = ""

    @JsonProperty(value = "ratingValue")
    @JsonAlias("ratingValue", "value")

    var ratingValue: Float = 0f
        get() {
            return if (field > 5) (field / 20) else field
        }
}