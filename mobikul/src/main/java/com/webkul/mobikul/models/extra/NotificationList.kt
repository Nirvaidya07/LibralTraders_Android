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

package com.webkul.mobikul.models.extra


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class NotificationList {

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("content")

    var content: String = ""

    @JsonProperty("notificationType")

    var notificationType: String = ""

    @JsonProperty("title")

    var title: String = ""

    @JsonProperty("categoryName")
    var categoryName: String ?= ""

    @JsonProperty("categoryId")

    var categoryId: String = ""

    @JsonProperty("productName")

    var productName: String ?= ""

    @JsonProperty("productType")

    var productType: String? = ""

    @JsonProperty("productId")

    var productId: String = ""

    @JsonProperty("banner")

    var banner: String = ""

    @JsonProperty("dominantColor")

    val dominantColor: String = ""
}