package com.webkul.mobikul.models.catalog

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.webkul.mobikul.handlers.BrandCatItem
import com.webkul.mobikul.models.BaseModel

@JsonIgnoreProperties(ignoreUnknown = true)
class BrandCategoryResponseModel : BaseModel() {

    @JsonProperty("id")
    var id: Int = 0

    @JsonProperty("option_id")
    var optionId: Int = 0

    @JsonProperty("page_title")
    var pageTitle: String? = null

    @JsonProperty("url_key")
    var urlKey: String? = null

    @JsonProperty("image")
    var image: String? = null

    @JsonProperty("short_description")
    var shortDescription: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("is_featured")
    var isFeatured: Int = 0

    @JsonProperty("is_display")
    var isDisplay: Int = 0

    @JsonProperty("meta_title")
    var metaTitle: String? = null

    @JsonProperty("meta_description")
    var metaDescription: String? = null

    @JsonProperty("label")
    var label: String? = null

    @JsonProperty("value")
    var value: String? = null

    @JsonProperty("sort_order")
    var sortOrder: Int = 0

    @JsonProperty("product_quantity")
    var productQuantity: Int = 0

    // Use a map to store additional properties dynamically
    private val additionalProperties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }
}
