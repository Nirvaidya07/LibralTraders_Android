package com.webkul.mobikul.models.product

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel
import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductResponse : BaseModel() {

    @JsonProperty("status")
    var status: Boolean? = false
    @JsonProperty("data")
    var data: ArrayList<ProductData>? = ArrayList()

}
