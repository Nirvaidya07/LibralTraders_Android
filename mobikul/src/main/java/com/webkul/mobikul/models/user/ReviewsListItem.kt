package com.webkul.mobikul.models.user


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
class ReviewsListItem {

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("thumbNail")

    var thumbNail: String = ""

    @JsonProperty("productId")

    var productId: String = ""

    @JsonProperty("productName")

    var proName: String = ""

    @JsonProperty("dominantColor")

    var dominantColor: String = ""

    @JsonProperty("customerRating")

    var customerRating: Float = 0f
}