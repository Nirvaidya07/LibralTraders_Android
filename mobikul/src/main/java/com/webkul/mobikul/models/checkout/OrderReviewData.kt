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
import com.webkul.mobikul.models.catalog.PriceDetailsItem


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderReviewData {

    @JsonProperty("items")

    var items: ArrayList<OrderReviewItemProduct> = ArrayList()

    @JsonProperty("totals")

    var totals: ArrayList<PriceDetailsItem> = ArrayList()

    @JsonProperty("grandtotal")

    var grandTotal: PriceDetailsItem = PriceDetailsItem()

    @JsonProperty("currencyCode")

    var currencyCode: String = ""
}