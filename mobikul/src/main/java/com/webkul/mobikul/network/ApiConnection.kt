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

package com.webkul.mobikul.network

import android.content.Context
import android.util.Log
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.ApplicationConstants.DEFAULT_OS
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.CountryCodeListingResponseModel
import com.webkul.mobikul.models.ImageUploadResponseData
import com.webkul.mobikul.models.ReOrderModel
import com.webkul.mobikul.models.catalog.*
import com.webkul.mobikul.models.checkout.*
import com.webkul.mobikul.models.extra.*
import com.webkul.mobikul.models.homepage.HomePageDataModel
import com.webkul.mobikul.models.homepage.OnBoardResponseModel
import com.webkul.mobikul.models.product.ProductDetailsPageModel
import com.webkul.mobikul.models.product.ProductRatingFormDataModel
import com.webkul.mobikul.models.product.ProductRequestBody
import com.webkul.mobikul.models.product.ProductResponse
import com.webkul.mobikul.models.product.ReviewListData
import com.webkul.mobikul.models.user.*
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ApiConnection {

    companion object {

        /*Catalog*/
        fun getHomePageData(context: Context, eTag: String, isFromUrl: Boolean, url: String): Observable<HomePageDataModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getHomePageData(
                    eTag
                    , AppSharedPref.getWebsiteId(context)
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , Utils.screenWidth
                    , Utils.screenDensity
                    , if (isFromUrl) 1 else 0
                    , url)
        }


        fun customerWebLogin(context: Context, token: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).customerWebLogin(
                    AppSharedPref.getCustomerToken(context),
                    token
            )
        }

        fun getSubCategoryData(context: Context, eTag: String, categoryId: String): Observable<SubCategoryResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getSubCategoryData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , Utils.screenWidth
                    , Utils.screenDensity
                    , categoryId)
        }

        fun getCatalogProductData(context: Context, eTag: String, type: String, id: String, pageNumber: Int, sortData: JSONArray, filterData: JSONArray): Observable<CatalogProductsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCatalogProductData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , Utils.screenWidth
                    , Utils.screenDensity
                    , type
                    , id
                    , pageNumber
                    , sortData
                    , filterData)
        }

        fun getAdvanceSearchFormData(context: Context, eTag: String): Observable<AdvancedSearchFormModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getAdvanceSearchFormData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , 1)
        }

        fun addToWishList(context: Context, productId: String, params: JSONObject, qty: String, files: List<MultipartBody.Part>): Observable<AddToWishListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addToWishList(
                         AppSharedPref.getStoreId(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , AppSharedPref.getCustomerToken(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , productId.toRequestBody("text/plain".toMediaTypeOrNull())
                    , params.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    ,qty.toRequestBody("text/plain".toMediaTypeOrNull())
                    , files)
        }

        fun getProductPageData(context: Context, eTag: String, productId: String): Observable<ProductDetailsPageModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getProductPageData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , Utils.screenWidth
                    , AppSharedPref.getCurrencyCode(context)
                    , productId)
        }

        fun getProductReviewList(context: Context, eTag: String, productId: String, pageNumber: Int): Observable<ReviewListData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getProductReviewList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , Utils.screenWidth
                    , productId
                    , pageNumber)
        }

        fun getRatingForData(context: Context, eTag: String): Observable<ProductRatingFormDataModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getRatingForData(
                    eTag
                    , AppSharedPref.getStoreId(context))
        }

        fun addToCompare(context: Context, productId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addToCompare(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , productId)
        }

        fun getCompareList(context: Context, eTag: String): Observable<CompareListData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCompareList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , Utils.screenWidth
                    , AppSharedPref.getCurrencyCode(context))
        }

        fun deleteFromCompareList(context: Context, productId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).deleteFromCompareList(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , productId)
        }

        /*Customer*/
        fun login(context: Context, loginFormModel: LoginFormModel): Observable<LoginResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).login(
                    AppSharedPref.getWebsiteId(context)
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getQuoteId(context)
                    , Utils.screenWidth
                    , Utils.screenDensity
                    , loginFormModel.email.trim()
                    , loginFormModel.mobile.trim()
                    , loginFormModel.password.trim()
                    , AppSharedPref.getFcmToken(context)
                    , DEFAULT_OS)
        }

        fun forgotPassword(context: Context, email: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).forgotPassword(
                    AppSharedPref.getWebsiteId(context)
                    , AppSharedPref.getStoreId(context)
                    , email)
        }

        fun createAccountFormData(context: Context, eTag: String): Observable<SignUpFormModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).createAccountFormData(
                    eTag
                    , AppSharedPref.getStoreId(context))
        }

        fun signUp(context: Context, signUpFormModel: SignUpFormModel): Observable<SignUpResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).signUp(
                    AppSharedPref.getWebsiteId(context)
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getQuoteId(context)
                    , Utils.screenWidth
                    , Utils.screenDensity
                    , signUpFormModel.prefix?.trim()
                    , signUpFormModel.firstName?.trim()
                    , signUpFormModel.middleName?.trim()
                    , signUpFormModel.lastName?.trim()
                    , signUpFormModel.suffix?.trim()
                    , signUpFormModel.dob?.trim()
                    , signUpFormModel.taxVat?.trim()
                    , signUpFormModel.gender
                    , signUpFormModel.emailAddr?.trim()
                    , signUpFormModel.password?.trim()
                    , signUpFormModel.pictureURL?.trim()
                    , signUpFormModel.isSocial
                    , (signUpFormModel.mobile?:"").trim()
                    , AppSharedPref.getFcmToken(context)
                    , signUpFormModel.shopURL
                    , if (signUpFormModel.signUpAsSeller) 1 else 0
                    , DEFAULT_OS
                    , signUpFormModel.orderId)
        }

        fun getOrderList(context: Context, eTag: String, pageNumber: Int, forDashboard: Boolean): Observable<OrderListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getOrderList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , pageNumber
                    , if (forDashboard) forDashboard.toString() else null)
        }

        fun getDownloadableProductsList(context: Context, eTag: String, pageNumber: Int): Observable<DownloadableProductsListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getDownloadableProductsList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , pageNumber)
        }

        fun getWishList(context: Context, eTag: String, pageNumber: Int): Observable<WishListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getWishList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , pageNumber)
        }

        fun getReviewsList(context: Context, eTag: String, pageNumber: Int, forDashboard: Boolean): Observable<ReviewListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getReviewsList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , pageNumber
                    , Utils.screenWidth
                    , if (forDashboard) "true" else null)
        }

        fun getAddressBookData(context: Context, eTag: String, forDashboard: Boolean): Observable<AddressBookResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getAddressBookData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , if (forDashboard) "true" else null)
        }

        fun getAccountInfo(context: Context, eTag: String): Observable<AccountInfoResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getAccountInfo(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context))
        }

        fun saveAccountInfo(context: Context, accountInfoResponseModel: AccountInfoResponseModel): Observable<SaveAccountInfoResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).saveAccountInfo(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , accountInfoResponseModel.prefixValue
                    , accountInfoResponseModel.firstName
                    , accountInfoResponseModel.middleName
                    , accountInfoResponseModel.lastName
                    , accountInfoResponseModel.suffixValue
                    , accountInfoResponseModel.dobValue
                    , accountInfoResponseModel.taxValue
                    , accountInfoResponseModel.genderValue
                    , if (accountInfoResponseModel.isChangesEmailChecked) 1 else 0
                    , accountInfoResponseModel.email
                    , if (accountInfoResponseModel.isChangesPasswordChecked) 1 else 0
                    , accountInfoResponseModel.currentPassword
                    , accountInfoResponseModel.newPassword
                    , accountInfoResponseModel.confirmNewPassword
                    , accountInfoResponseModel.mobile)
        }

        fun reorder(context: Context, incrementId: String?): Observable<ReOrderModel> {

            if (AppSharedPref.isLoggedIn(context)) {
                return ApiClient.getClient()!!.create(ApiDetails::class.java).reorder(
                        AppSharedPref.getStoreId(context)
                        , AppSharedPref.getCustomerToken(context)
                        , incrementId)
            } else {
                return ApiClient.getClient()!!.create(ApiDetails::class.java).reorderGuest(
                        AppSharedPref.getStoreId(context)
                        , incrementId)
            }


        }

        fun getOrderDetails(context: Context, eTag: String, incrementId: String): Observable<OrderDetailsModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getOrderDetails(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , incrementId)
        }

        fun downloadProduct(context: Context, eTag: String, hash: String): Observable<DownloadProductsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).downloadProduct(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , hash)
        }

        fun getReviewDetails(context: Context, eTag: String, reviewId: String): Observable<ReviewDetailsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getReviewDetails(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , Utils.screenWidth
                    , reviewId)
        }

        fun removeFromWishList(context: Context, itemId: String): Observable<ReviewDetailsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).removeFromWishList(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , itemId)
        }

        fun wishListToCart(context: Context, productId: String, itemId: String, qty: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).wishListToCart(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , productId
                    , itemId
                    , qty)
        }

        fun getAddressFormData(context: Context, eTag: String, addressId: String): Observable<AddressFormResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getAddressFormData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , addressId)
        }

        fun saveAddress(context: Context, addressId: String, addressData: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).saveAddress(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , addressId
                    , addressData)
        }

        fun addAllToCart(context: Context, qty: JSONObject): Observable<BaseModel> {
            Log.d("TAG", "addAllToCart: "+ AppSharedPref.getStoreId(context))
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addAllToCart(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , qty)
        }

        fun shareWishList(context: Context, emails: String, message: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).shareWishList(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , emails
                    , message)
        }

        fun uploadProfileImage(context: Context, profileImageMultipartBody: MultipartBody.Part): Observable<ImageUploadResponseData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).uploadProfileImage(
                   AppSharedPref.getStoreId(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , AppSharedPref.getCustomerToken(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , Utils.screenWidth.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , Utils.screenDensity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , profileImageMultipartBody)
        }

        fun uploadBannerImage(context: Context, bannerImageMultipartBody: MultipartBody.Part): Observable<ImageUploadResponseData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).uploadBannerImage(
                  AppSharedPref.getStoreId(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    ,  AppSharedPref.getCustomerToken(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    ,  Utils.screenWidth.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , Utils.screenDensity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , bannerImageMultipartBody)
        }

        fun updateWishList(context: Context, itemData: JSONArray): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).updateWishList(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , itemData)
        }

        fun saveReview(context: Context, id: String, title: String, detail: String, nickname: String, ratingObj: JSONObject): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).saveReview(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , id
                    , title
                    , detail
                    , nickname
                    , ratingObj)
        }

        fun deleteAddress(context: Context, id: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).deleteAddress(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , id)
        }

        fun getInvoiceDetailsData(context: Context, eTag: String, invoiceId: String): Observable<InvoiceDetailsData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getInvoiceDetailsData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , invoiceId)
        }

        fun getShipmentDetailsData(context: Context, eTag: String, shipmentId: String): Observable<ShipmentDetailsData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getShipmentDetailsData(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , shipmentId)
        }

        fun checkCustomerByEmail(context: Context, email: String): Observable<CheckCustomerByEmailResponseData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).checkCustomerByEmail(
                    AppSharedPref.getStoreId(context)
                    , email)
        }

        /* Extra */

        fun logout(context: Context): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).logout(
                    AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getFcmToken(context))
        }

        fun getSearchSuggestions(context: Context, searchQuery: String): Observable<SearchSuggestionResponse> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getSearchSuggestions(
                    AppSharedPref.getStoreId(context),
                    AppSharedPref.getCurrencyCode(context)
                    , searchQuery)
        }

        fun getSearchTermsList(context: Context, eTag: String): Observable<SearchTermsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getSearchTermsList(
                    eTag
                    , AppSharedPref.getCurrencyCode(context)
                    , AppSharedPref.getStoreId(context))
        }

        fun getCMSPageData(context: Context, eTag: String, id: String?): Observable<CMSPageDataModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCMSPageData(
                    eTag
                    , id)
        }

        fun getNotificationsList(context: Context, eTag: String): Observable<NotificationListResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getNotificationsList(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , Utils.screenWidth
                    , Utils.screenDensity)
        }

        fun getOtherNotificationData(context: Context, eTag: String, notificationId: String): Observable<OtherNotificationResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getOtherNotificationData(
                    eTag
                    , notificationId)
        }

        fun uploadTokenData(context: Context, token: String?): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).uploadTokenData(
                    token
                    , AppSharedPref.getCustomerToken(context)
                    , DEFAULT_OS)
        }

        /* Other */

        fun postContactUs(context: Context, name: String, email: String, telephone: String, comment: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).postContactUs(
                    AppSharedPref.getStoreId(context)
                    , name
                    , email
                    , telephone
                    , comment)
        }

        fun addPriceAlert(context: Context, productId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addPriceAlert(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , productId)
        }

        fun addStockAlert(context: Context, productId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addStockAlert(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , productId)
        }

        fun getGuestOrderDetails(context: Context, type: String, incrementId: String, lastName: String, email: String, zipCode: String): Observable<GuestOrderDetailsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getGuestOrderDetails(
                    AppSharedPref.getStoreId(context)
                    , type
                    , incrementId
                    , lastName
                    , email
                    , zipCode)
        }

        /* Checkout */

        fun addToCart(context: Context, productId: String, qty: String, params: JSONObject, files: ArrayList<MultipartBody.Part>?, relatedProducts: JSONArray): Observable<AddToCartResponseModel> {
            Log.d("TAG", "addToCart----: "+ AppSharedPref.getStoreId(context)
                +","+  AppSharedPref.getCustomerToken(context)
                    +","+  AppSharedPref.getQuoteId(context)
                    +","+   productId
                    +","+  qty
                    +","+ params
                    +","+  files
                        +","+  relatedProducts.toString());
            return ApiClient.getClient()!!.create(ApiDetails::class.java).addToCart(
                     AppSharedPref.getStoreId(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    ,  AppSharedPref.getCustomerToken(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , AppSharedPref.getQuoteId(context)
                    ,  productId.toRequestBody("text/plain".toMediaTypeOrNull())
                    , qty.toRequestBody("text/plain".toMediaTypeOrNull())
                    ,params.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , files
                    , relatedProducts.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }

        fun updateProduct(context: Context, productId: String, qty: String, params: JSONObject, files: ArrayList<MultipartBody.Part>?, relatedProducts: JSONArray, itemId: String): Observable<AddToCartResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).updateProduct(
                    AppSharedPref.getStoreId(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , AppSharedPref.getCustomerToken(context).toRequestBody("text/plain".toMediaTypeOrNull())
                    , AppSharedPref.getQuoteId(context)
                    ,productId.toRequestBody("text/plain".toMediaTypeOrNull())
                    , qty.toRequestBody("text/plain".toMediaTypeOrNull())
                    , params.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , files
                    ,relatedProducts.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    , itemId.toRequestBody("text/plain".toMediaTypeOrNull()))
        }

        fun getCartDetails(context: Context, eTag: String): Observable<CartDetailsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCartDetails(
                    eTag
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , Utils.screenWidth
                    , AppSharedPref.getCurrencyCode(context))
        }

        fun moveToWishList(context: Context, itemId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).moveToWishList(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , itemId)
        }

        fun removeFromCart(context: Context, itemId: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).removeFromCart(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , itemId)
        }

        fun applyOrRemoveCoupon(context: Context, couponCode: String, removeCoupon: Boolean): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).applyOrRemoveCoupon(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , couponCode
                    , if (removeCoupon) 1 else 0)
        }

        fun updateCart(context: Context, itemIds: JSONArray, itemQtys: JSONArray): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).updateCart(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , itemIds
                    , itemQtys)
        }

        fun emptyCart(context: Context): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).emptyCart(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context))
        }

        fun getCheckoutAddressInfo(context: Context): Observable<CheckoutAddressInfoResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCheckoutAddressInfo(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context))
        }

        fun getShippingMethods(context: Context, shippingData: JSONObject): Observable<ShippingMethodsModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getShippingMethods(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , if (AppSharedPref.isLoggedIn(context)) "customer" else "guest"
                    , shippingData)
        }

        fun getReviewsAndPaymentsData(context: Context, shippingMethod: String?, selectedPaymentMethod: String?): Observable<ReviewsAndPaymentsResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getReviewsAndPaymentsData(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , Utils.screenWidth
                    , if (AppSharedPref.isLoggedIn(context)) "customer" else "guest"
                    , shippingMethod
                    , selectedPaymentMethod
            )
        }

        fun applyPaymentMethod(context: Context, shippingMethod: String?, selectedPaymentMethod: String? , paymentData: JSONObject): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).applyPaymentMethod(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , AppSharedPref.getCurrencyCode(context)
                    , Utils.screenWidth
                    , if (AppSharedPref.isLoggedIn(context)) "customer" else "guest"
                    , shippingMethod
                    , selectedPaymentMethod
                    , paymentData
            )
        }

        fun placeOrder(context: Context, paymentMethod: String, paymentData: JSONObject, transactionId:String?,status:Int?): Observable<SaveOrderResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).placeOrder(
                    AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
                    , AppSharedPref.getQuoteId(context)
                    , paymentMethod
                    , paymentData
                    , if (AppSharedPref.isLoggedIn(context)) "customer" else "guest"
                    , AppSharedPref.getFcmToken(context)
                    , DEFAULT_OS
                    , DEFAULT_OS
                    ,transactionId
                    ,status)
        }

        fun getDeliveryBoyLocation(context: Context, deliveryboyId: String): Observable<GetDeliveryBoyLocationResponseData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getDeliveryBoyLocation(deliveryboyId)
        }

        fun saveDeliveryboyReview(context: Context, deliveryboyId: String?, customerId: String?, nickName: String, title: String, comment: String, rating: Int): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).saveDeliveryboyReview(
                    AppSharedPref.getStoreId(context),
                    AppSharedPref.getCustomerToken(context),
                    AppSharedPref.getCustomerEmail(context),
                    deliveryboyId,
                    customerId,
                    nickName,
                    title,
                    comment,
                    rating)
        }

        fun updateTokenOnCloud(context: Context, accountType: String,sellerId:String?): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).updateTokenOnCloud(
                    BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + AppSharedPref.getCustomerId(context),
                    AppSharedPref.getCustomerName(context),
                    AppSharedPref.getCustomerImageUrl(context),
                    AppSharedPref.getFcmToken(context) ?: "",
                    accountType,
                    "android",
                    sellerId
            )

        }

        fun deleteTokenFromCloud(context: Context, accountType: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).deleteTokenFromCloud(
                    BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + AppSharedPref.getCustomerId(context),
                    AppSharedPref.getFcmToken(context) ?: "",
                    accountType, "android"
            )
        }

        fun getRefundDetailsData(context: Context, eTag: String, refundId: String): Observable<RefundDetailsData> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getRefundDetailsData(
                    eTag, AppSharedPref.getStoreId(context), AppSharedPref.getCustomerToken(context), refundId)
        }


        fun getOnBoardData(context: Context): Observable<OnBoardResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getOnBoardData(
                    AppSharedPref.getWebsiteId(context),
                    AppSharedPref.getStoreId(context)
            )
        }

        fun deleteAccount(context: Context): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).deleteAccount(
                    AppSharedPref.getWebsiteId(context)
                    , AppSharedPref.getStoreId(context)
                    , AppSharedPref.getCustomerToken(context)
            )
        }

        /* OTP Login*/

        fun countryCodeApi(context: Context): Observable<CountryCodeListingResponseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).countryCodeApi(AppSharedPref.getStoreId(context))
        }

        fun sendOtp(context: Context, mobile: String, email: String, type: String, resend: Boolean): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).sendOtp(
                    AppSharedPref.getStoreId(context)
                    , mobile
                    , email
                    , type
                    , DEFAULT_OS
                    , if (resend) 1 else 0)
        }

        fun verifyOtp(context: Context, mobile: String, email: String, otp: String, type: String): Observable<BaseModel> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).verifyOtp(
                    ApplicationConstants.DEFAULT_WEBSITE_ID
                    , AppSharedPref.getStoreId(context)
                    , mobile
                    , email
                    , otp
                    , type
                    , Utils.screenDensity
                    , Utils.screenWidth
                    , AppSharedPref.getFcmToken(context)
                    , "android")
        }

        fun orderAgain(context: Context) : Observable<String> {
            val productRequestBody = ProductRequestBody();
            productRequestBody.pageSize  = 20
            productRequestBody.currentPage  = 1
            productRequestBody.customerId  = AppSharedPref.getCustomerId(context).toInt()
            return ApiClient.getClient()!!.create(ApiDetails::class.java).orderAgain(productRequestBody)
        }

        fun getQuoteId(context: Context) : Observable<String> {
            return ApiClient.getClient1()!!.create(ApiDetails::class.java).quoteId()
        }

        fun getOrderTrackingData(context: Context, shipmentId: String ) : Observable<String> {
            return ApiClient.getClient()!!.create(ApiDetails::class.java).getOrderTrackingData(shipmentId,AppSharedPref.getCustomerId(context))
        }
//        fun getCartMine(quoteId:String,sku:String,qty:String ) : Observable<String> {
//            return ApiClient.getClient()!!.create(ApiDetails::class.java).getCartMine(shipmentId,AppSharedPref.getCustomerId(context))
//        }

    }
}