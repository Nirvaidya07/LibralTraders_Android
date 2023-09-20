package com.webkul.mobikul.models.catalog


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.homepage.BannerImage
import com.webkul.mobikul.models.product.ProductTileData

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

class CatalogProductsResponseModel : BaseModel() {

    @JsonProperty("productList")

    val productList: ArrayList<ProductTileData> = ArrayList()

    @JsonProperty("totalCount")

    val totalCount: Int = 0

    @JsonProperty("sortingData")

    val sortingData: ArrayList<SortingData> = ArrayList()

    @JsonProperty("layeredData")

    val layeredData: ArrayList<LayeredData> = ArrayList()

    @JsonProperty("banners")

    var bannerImage: ArrayList<BannerImage> = ArrayList()

    @JsonProperty("dominantColor")

    var dominantColor: String = ""

    @JsonProperty("criteriaData")

    var criteriaData: ArrayList<String> = ArrayList()
}