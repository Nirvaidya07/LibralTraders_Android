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

package com.webkul.mobikul.models.checkout


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ReviewsAndPaymentsResponseModel : BaseModel() {

    @JsonProperty("paymentMethods")

    var paymentMethods: ArrayList<PaymentMethod> = ArrayList()

    @JsonProperty("shippingAddress")

    var shippingAddress: String = ""

    @JsonProperty("shippingMethod")

    var shippingMethod: String = ""

    @JsonProperty("orderReviewData")

    var orderReviewData: OrderReviewData = OrderReviewData()

    @JsonProperty("cartTotal")

    var cartTotal: String = ""

    var selectedPaymentMethod: String = ""

    @JsonProperty("couponCode")

    var couponCode: String? = null
//        get() = field ?: ""

    @JsonProperty("currencyCode")
    var currencyCode: String? = null

    @JsonProperty("razorpay_data")
    var razorpay_data: ArrayList<RazorpayData>? = ArrayList()
}