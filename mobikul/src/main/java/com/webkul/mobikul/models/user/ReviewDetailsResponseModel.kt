package com.webkul.mobikul.models.user


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

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
class ReviewDetailsResponseModel : BaseModel() {
    @JsonProperty("productName")

    var name: String = ""

    @JsonProperty("thumbNail")

    var image: String = ""

    @JsonProperty("dominantColor")

    var dominantColor: String = ""

    @JsonProperty("ratingData")

    var ratingData: ArrayList<ProductRatingData> = ArrayList()

    @JsonProperty("reviewDate")

    var reviewDate: String = ""

    @JsonProperty("reviewTitle")

    var reviewTitle: String = ""

    @JsonProperty("reviewDetail")

    var reviewDetail: String = ""

    @JsonProperty("productId")

    var productId: String = ""

    @JsonProperty("averageRating")

    var rating: Float = 0f

    @JsonProperty("totalProductReviews")

    var totalProductReviews: Int = 0
}