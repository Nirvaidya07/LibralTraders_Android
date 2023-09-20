package com.webkul.mobikul.models

import com.webkul.mobikul.models.homepage.BannerImage
import com.webkul.mobikul.models.homepage.Category
import com.webkul.mobikul.models.product.ProductTileData

data class CategoriesData(val viewType: Int=0, val parentCategoryId: String?=null,val category: Category?=null, val bannerImage: List<BannerImage> ?=null, val productList: List<ProductTileData> ?=null,val hotSeller: List<ProductTileData> ?=null, val title:String?=null)