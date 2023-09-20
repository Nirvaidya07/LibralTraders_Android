/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.catalog

import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.product.ProductTileData
import org.json.JSONArray
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

class CartDetailsResponseModel : BaseModel() {

    @JsonProperty("items")

    var items: ArrayList<CartItem> = ArrayList()

    @JsonProperty("couponCode")

    var couponCode: String = ""

    @JsonProperty("showThreshold")

    var showThreshold: Boolean = false

    @JsonProperty("isVirtual")

    var isVirtual: Boolean = false

    @JsonProperty("isAllowedGuestCheckout")

    var isAllowedGuestCheckout: Boolean = false

    @JsonProperty("totalsData")

    var totalsData: ArrayList<PriceDetailsItem> = ArrayList()

    @JsonProperty("crossSellList")

    var crossSellList: ArrayList<ProductTileData> = ArrayList()

    @JsonProperty("canGuestCheckoutDownloadable")

    var canGuestCheckoutDownloadable: Boolean = false

    @JsonProperty("allowMultipleShipping")

    var allowMultipleShipping: Boolean = false

    @JsonProperty("isCheckoutAllowed")

    var isCheckoutAllowed: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkoutAllowed)
        }

    @JsonProperty("minimumAmount")

    var minimumAmount: Float = 0f

    @JsonProperty("minimumFormattedAmount")

    var minimumFormattedAmount: String = ""

    @JsonProperty("descriptionMessage")
    var descriptionMessage: String? = ""
        get() = field ?: ""

    @JsonProperty("cartTotal")

    var cartTotal: String = ""

    var containsDownloadableProducts = false
    var error = ""

    fun getItemIds(): JSONArray {
        val itemIds = JSONArray()
        for (eachItem in items) {
            itemIds.put(eachItem.id)
        }
        return itemIds
    }

    fun getQtys(): JSONArray {
        val qtyJsonArr = JSONArray()
        for (eachItem in items) {
            qtyJsonArr.put(eachItem.qty?.let { Integer.parseInt(it) })
        }
        return qtyJsonArr
    }
}