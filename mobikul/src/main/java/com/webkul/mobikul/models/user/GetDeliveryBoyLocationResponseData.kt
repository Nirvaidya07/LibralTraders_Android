package com.webkul.mobikul.models.user


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

/**
 * Created by vedesh.kumar on 28/3/18. @Webkul Software Private limited
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class GetDeliveryBoyLocationResponseData : BaseModel() {

    @JsonProperty("latitude")

    var latitude: String = "0.0"

    @JsonProperty("longitude")

    var longitude: String = "0.0"
}