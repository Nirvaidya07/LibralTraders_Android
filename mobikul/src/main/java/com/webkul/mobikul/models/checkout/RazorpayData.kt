package com.webkul.mobikul.models.checkout

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class RazorpayData {
    @JsonProperty("razorpay_order_id")
    var razorpay_order_id: String? = ""

    @JsonProperty("razorpay_merchantName")
    var razorpay_merchantName: String? = ""

    @JsonProperty("razorpay_keyId")
    var razorpay_keyId: String? = ""

    @JsonProperty("amount")
    var amount: String? = ""

    @JsonProperty("unformattedAmount")
    var unformattedAmount: Int? = 0

    @JsonProperty("customer_name")
    var customer_name: String? = ""

    @JsonProperty("customer_lastname")
    var customer_lastname: String? = ""

    @JsonProperty("customer_email")
    var customer_email: String? = ""

    @JsonProperty("customer_phone_number")
    var customer_phone_number: String? = ""

}