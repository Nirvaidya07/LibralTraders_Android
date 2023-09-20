package com.webkul.mobikul.models.user


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel
import org.json.JSONArray
import org.json.JSONObject

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
@JsonIgnoreProperties(ignoreUnknown = true,)
class WishListResponseModel : BaseModel() {

    @JsonProperty("wishList")

    var wishList: ArrayList<WishListItem> = ArrayList()

    @JsonProperty("totalCount")

    var totalCount: Int = 0

    fun getUpdateWishListItemJsonArray(): JSONArray {
        val updateWishListJsonArray = JSONArray()
        for (eachWishListItem: WishListItem in wishList) {
            val temp = JSONObject()
            temp.put("id", eachWishListItem.id)
            temp.put("description", eachWishListItem.description)
            temp.put("qty", eachWishListItem.qty)
            updateWishListJsonArray.put(temp)
        }
        return updateWishListJsonArray
    }

    fun productQuantities(): JSONObject {
        val quantities = JSONObject()
        for (eachWishListItem: WishListItem in wishList) {
            quantities.put(eachWishListItem.id, eachWishListItem.qty)
        }
        return quantities
    }
}