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
class OrderList {

    @JsonProperty("order_id")

    val orderId: String = ""

    @JsonProperty("date")

    val date: String = ""

    @JsonProperty("ship_to")

    val shipTo: String = ""

    @JsonProperty("item_count")

    val itemCount: String = ""

    @JsonProperty("order_total")

    val orderTotal: String = ""

    @JsonProperty("item_image_url")

    val itemImageUrl: String = ""

    @JsonProperty("dominantColor")

    val dominantColor: String = ""

    @JsonProperty("status")

    val status: String = ""

    @JsonProperty("canReorder")

    val canReorder: Boolean = false

    @JsonProperty("state")

    val state: String = ""
        get() {
            return if (field == "new") "pending" else field
        }

    @JsonProperty("statusColorCode")

    val statusColorCode: String = ""
}