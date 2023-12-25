/*
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

package com.webkul.mobikul.models.product


import android.content.Context
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_AR_CORE
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.homepage.PriceFormat
import java.util.*

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductDetailsPageModel : BaseModel() {

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("productUrl")

    var productUrl: String = ""

    @JsonProperty("name")

    var name: String = ""

    @JsonProperty("formattedMinPrice")

    var formattedMinPrice: String = ""

    @JsonProperty("minPrice")

    var minPrice: Double = 0.0

    @JsonProperty("formattedMaxPrice")

    var formattedMaxPrice: String = ""

    @JsonProperty("maxPrice")

    var maxPrice: Double = 0.0

    @JsonProperty("formattedPrice")

    var formattedPrice: String = ""

    @JsonProperty("price")

    var price: Double = 0.0

    @JsonProperty("formattedFinalPrice")

    var formattedFinalPrice: String = ""

    @JsonProperty("finalPrice")

    var finalPrice: Double = 0.0

    @JsonProperty("groupedPrice")

    var groupedPrice: String = ""

    @JsonProperty("rating")

    var rating: String = ""

    @JsonProperty("thumbNail")

    var thumbNail: String = ""

    @JsonProperty("typeId")

    var typeId: String = ""

    @JsonProperty("isNew")
    @JsonAlias("isNew", "is_new")
    var isNew: Boolean = false

    @JsonProperty("msrpEnabled")

    var msrpEnabled: Int = 0

    @JsonProperty("msrpDisplayActualPriceType")

    var msrpDisplayActualPriceType: Int = 0

    @JsonProperty("msrp")

    var msrp: Double = 0.0

    @JsonProperty("formattedMsrp")

    var formattedMsrp: String = ""

    @JsonProperty("shortDescription")

    var shortDescription: String = ""

    @JsonProperty("description")

    var description: String = ""

    @JsonProperty("isInRange")

    var isInRange: Boolean = false

    @JsonProperty("guestCanReview")

    var guestCanReview: Boolean = false

    @JsonProperty("availability")

    var availability: String = ""

    @JsonProperty("isAvailable")

    var isAvailable: Boolean = false

    @JsonProperty("priceFormat")

    var priceFormat: PriceFormat = PriceFormat()

    @JsonProperty("imageGallery")

    var imageGallery: ArrayList<ImageGalleryData> = ArrayList()

    @JsonProperty("additionalInformation")

    var additionalInformation: ArrayList<AdditionalInformationData> = ArrayList()

    @JsonProperty("reviewList")

    var reviewList: ArrayList<ProductReviewData> = ArrayList()

    @JsonProperty("priceView")

    var priceView: String = ""

    @JsonProperty("tierPrices")

    var tierPrices: ArrayList<String> = ArrayList()

    @JsonProperty("relatedProductList")

    var relatedProductList: ArrayList<ProductTileData> = ArrayList()

    @JsonProperty("groupedData")

    var groupedData: ArrayList<GroupedData> = ArrayList()

    @JsonProperty("links")

    var links: Links = Links()

    @JsonProperty("samples")

    var samples: Samples = Samples()

    @JsonProperty("bundleOptions")

    var bundleOptions: ArrayList<BundleOption> = ArrayList()

    @JsonProperty("configurableData")

    var configurableData: ConfigurableData = ConfigurableData()

    @JsonProperty("customOptions")
    var customOptions: ArrayList<ProductCustomOption> = ArrayList()

    @JsonProperty("isInWishlist")

    var isInWishList: Boolean = false

    @JsonProperty("wishlistItemId")

    var wishListItemId: String = ""

    @JsonProperty("upsellProductList")

    var upsellProductList: ArrayList<ProductTileData> = ArrayList()

    @JsonProperty("showPriceDropAlert")

    var showPriceDropAlert: Boolean = false

    @JsonProperty("showBackInStockAlert")

    var showBackInStockAlert: Boolean = false

    @JsonProperty("isCheckoutAllowed")

    var isCheckoutAllowed: Boolean = false

    @JsonProperty("isAllowedGuestCheckout")

    var isAllowedGuestCheckout: Boolean = false

    @JsonProperty("canGuestCheckoutDownloadable")

    var canGuestCheckoutDownloadable: Boolean = false

    @JsonProperty("arType")

    var arType: String = ""

    @JsonProperty("arUrl")

    var arModelUrl: String = ""

    @JsonProperty("reviewCount")

    var reviewCount: Int = 0

    @JsonProperty("isThresholdVisible")

    var isThresholdVisible: Boolean = false

    @JsonProperty("thresholdQtyLeft")

    var thresholdQtyLeft: Int? = null

    @JsonProperty("ratingArray")

    var ratingArray: RatingArray = RatingArray()

    var qty: String = "1"
        @Bindable get() = field
        set(value) {
            field = value
            if (value.isEmpty()) {
                field = "1"
            } else {
                if (Integer.parseInt(value) < 1) {
                    field = "1"
                    return
                }
            }
            field = value
            notifyPropertyChanged(BR.qty)
        }

    fun hasSpecialPrice(): Boolean {
        return finalPrice != 0.0 && finalPrice < price
    }

    fun hasPrice(): Boolean {
        return !(hasMinPrice() || hasGroupedPrice())
    }

    fun hasMinPrice(): Boolean {
        return minPrice != 0.0
    }

    fun hasMaxPrice(): Boolean {
        return maxPrice != 0.0
    }

    fun hasMinMaxRange(): Boolean {
        return minPrice < maxPrice
    }

    fun hasGroupedPrice(): Boolean {
        return groupedPrice.isNotBlank()
    }

    fun hasAnyOptions(): Boolean {
        return (configurableData.attributes != null && configurableData.attributes!!.size != 0 || bundleOptions.size != 0 || links.linkData.size != 0 || samples.linkSampleData.size != 0 || groupedData.size != 0)
    }

    fun getDiscountPercentage(): String {
        return Math.round((100 - finalPrice / price * 100)).toString() + "%"
        // return (100 - finalPrice / price * 100).toInt().toString() + "%"
    }

    fun isArEnabled(context: Context): Boolean {
        if (ENABLE_AR_CORE) {
            if (arModelUrl.isBlank()) {
                return false
            } else {
                if (arType == "2D") {
                    return true
                } else if (arType == "3D" && AppSharedPref.getIsArSupported(context)) {
                    return true
                }
                return false
            }
        } else {
            return false
        }
    }
}