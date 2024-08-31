package com.webkul.mobikul.handlers

import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.catalog.BrandCategoryResponseModel

class BrandCategoryViewModel: BaseModel() {

     val brandList: MutableList<BrandCategoryResponseModel> = mutableListOf()
 }