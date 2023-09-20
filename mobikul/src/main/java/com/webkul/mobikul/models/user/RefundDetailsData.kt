package com.webkul.mobikul.models.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class RefundDetailsData: BaseModel() {

    @JsonProperty("orderId")

    var orderId: String?= ""

    @JsonProperty("itemList")

    var items: ArrayList<OrderRefundItem> = ArrayList()

    @JsonProperty("totals")

    var totals: ArrayList<TotalItem> = ArrayList()
}