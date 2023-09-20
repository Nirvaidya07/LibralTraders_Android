package com.webkul.mobikul.models.catalog


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.Brand
import com.webkul.mobikul.models.homepage.BannerImage
import com.webkul.mobikul.models.homepage.Category
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
class SubCategoryResponseModel : BaseModel() {

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("name")

    var name: String = ""

    @JsonProperty("productList")

    val productList: ArrayList<ProductTileData> = ArrayList()

    @JsonProperty("hotSeller")

    val hotSeller: ArrayList<ProductTileData> = ArrayList()

    ///For Default Category View
    @JsonProperty("bannerImage")
    val bannerImage: ArrayList<BannerImage> = ArrayList()

    //For Tab Category View
    @JsonProperty("smallBannerImage")
    val smallBannerImage: ArrayList<BannerImage> = ArrayList()

    @JsonProperty("categories")
    val subCategoriesList: ArrayList<Category> = ArrayList()

    @JsonProperty("brandList")
    val brandList: ArrayList<Brand> = ArrayList()
}