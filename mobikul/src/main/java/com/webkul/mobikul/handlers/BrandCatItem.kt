package com.webkul.mobikul.handlers

import com.fasterxml.jackson.annotation.JsonProperty

data class BrandCatItem(
    @JsonProperty("id") val id: Int,
    @JsonProperty("option_id") val optionId: Int,
    @JsonProperty("page_title") val pageTitle: String? = null,
    @JsonProperty("url_key") val urlKey: String,
    @JsonProperty("image") val image: String,
    @JsonProperty("short_description") val shortDescription: String? = null,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("is_featured") val isFeatured: Int,
    @JsonProperty("is_display") val isDisplay: Int,
    @JsonProperty("meta_title") val metaTitle: String? = null,
    @JsonProperty("meta_description") val metaDescription: String? = null,
    @JsonProperty("label") val label: String? = null,
    @JsonProperty("value") val value: String,
    @JsonProperty("sort_order") val sortOrder: Int,
    @JsonProperty("product_quantity") val productQuantity: Int
)