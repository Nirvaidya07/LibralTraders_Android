package com.webkul.mobikul.handlers

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.activities.CompareProductsActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.ConstantsHelper.RC_AR
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.AddToCartResponseModel
import com.webkul.mobikul.models.product.ProductTileData
import com.webkul.mobikul.models.user.AddToWishListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

class ProductTileHandler(val mContext: Context, val mProductList: ArrayList<ProductTileData>) {

    private var mIsProcessing: Boolean = false

    fun onClickItem(entityId: String, name: String, thumbNail: String, dominantColor: String) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BUNDLE_KEY_PRODUCT_ID, entityId)
        intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, name)
        intent.putExtra(BUNDLE_KEY_PRODUCT_IMAGE, thumbNail)
        intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, dominantColor)
        mContext.startActivity(intent)
    }

    fun onClickWishListButton(view: View, position: Int, entityId: String, isInWishList: Boolean, wishListItemId: String) {
        if (AppSharedPref.isLoggedIn(mContext)) {
            if (!mIsProcessing) {
                if (isInWishList) {
                    removeItemFromWishList(view, position, wishListItemId)
                } else {
                    addItemToWishList(view, position, entityId)
                }
            }
        } else {
            showLoginAlertDialog(mContext.getString(R.string.login_first))
        }
    }

    private fun removeItemFromWishList(view: View, position: Int, itemId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.remove_item),
                mContext.getString(R.string.remove_wish_list_item_msg),
                false,
                mContext.getString(R.string.yes_remove_it),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                mIsProcessing = true
                (view as LottieAnimationView).speed = -1f
                view.playAnimation()
                ApiConnection.removeFromWishList(mContext, itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : ApiCustomCallback<BaseModel>(mContext, false) {
                            override fun onNext(removeFromWishList: BaseModel) {
                                super.onNext(removeFromWishList)
                                mIsProcessing = false
                                if (removeFromWishList.success) {
                                    ToastHelper.showToast(mContext,removeFromWishList.message,Toast.LENGTH_LONG)
                                    mProductList[position].isInWishList = false
                                    mProductList[position].wishListItemId = ""
                                    mProductList[position].id?.let { BaseActivity.mDataBaseHandler.updateRecentlyViewedProduct(AppSharedPref.getStoreId(mContext), AppSharedPref.getCurrencyCode(mContext), it, ObjectMapper().writeValueAsString(mProductList[position])) }
                                    ToastHelper.showToast(mContext,removeFromWishList.message,Toast.LENGTH_LONG)
                                    if (mContext is HomeActivity) {
                                        mContext.setupRecentlyViewedCarouselsLayout()
                                    }
                                } else {
                                    view.cancelAnimation()
                                    view.progress = 1f
                                }
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                mIsProcessing = false
                                view.cancelAnimation()
                                view.progress = 1f
                            }
                        })
            },
                mContext.getString(R.string.cancel),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
    }

    private fun addItemToWishList(view: View, position: Int, entityId: String) {
        mIsProcessing = true
        (view as LottieAnimationView).speed = 1f
        view.playAnimation()
        ApiConnection.addToWishList(mContext, entityId, JSONObject(), "1", ArrayList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddToWishListResponseModel>(mContext, false) {
                    override fun onNext(addItemToWishList: AddToWishListResponseModel) {
                        super.onNext(addItemToWishList)
                        mIsProcessing = false
                        if (addItemToWishList.success) {
                            FirebaseAnalyticsHelper.logAddToWishListEvent(mProductList[position].id!!, mProductList[position].name!!, "1", JSONObject())
                            ToastHelper.showToast(mContext,addItemToWishList.message,Toast.LENGTH_LONG)
                            mProductList[position].isInWishList = true
                            mProductList[position].wishListItemId = addItemToWishList.itemId
                            mProductList[position].id?.let { BaseActivity.mDataBaseHandler.updateRecentlyViewedProduct(AppSharedPref.getStoreId(mContext), AppSharedPref.getCurrencyCode(mContext), it, ObjectMapper().writeValueAsString(mProductList[position])) }
                            if (mContext is HomeActivity) {
                                mContext.setupRecentlyViewedCarouselsLayout()
                            }
                        } else {
                            view.cancelAnimation()
                            view.progress = 0f
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mIsProcessing = false
                        view.cancelAnimation()
                        view.progress = 0f
                    }
                })
    }

    private fun showLoginAlertDialog(content: String?) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.login_required),
                content,
                true,
                mContext.getString(R.string.login),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mContext.startActivity((mContext.application as MobikulApplication).getLoginAndSignUpActivity(mContext))
                },
                mContext.getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    fun onClickDeleteItem(productId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.remove_item),
                mContext.getString(R.string.remove_compare_list_item_msg),
                false,
                mContext.getString(R.string.yes_remove_it),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    (mContext as CompareProductsActivity).mContentViewBinding.loading = true
                    mIsProcessing = true
                    ApiConnection.deleteFromCompareList(mContext, productId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : ApiCustomCallback<BaseModel>(mContext, false) {
                                override fun onNext(removeFromCompareList: BaseModel) {
                                    super.onNext(removeFromCompareList)
                                    mContext.mContentViewBinding.loading = false
                                    ToastHelper.showToast(mContext, removeFromCompareList.message)
                                    if (removeFromCompareList.success) {
                                        mContext.callApi()
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    mContext.mContentViewBinding.loading = false
                                    ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
                                }
                            })
                },
                mContext.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    fun onClickArBtn(selectedProductPosition: Int) {
        (mContext as CatalogActivity).mSelectedProduct = selectedProductPosition
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mContext.startArActivity()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                mContext.requestPermissions(permissions, RC_AR)
            }
        }
    }

    fun onClickAddToCart(hasRequiredOptions: Boolean, entityId: String, name: String, thumbNail: String, dominantColor: String) {
        if (hasRequiredOptions) {
            onClickItem(entityId, name, thumbNail, dominantColor)
        } else {
            (mContext as CompareProductsActivity).mContentViewBinding.loading = true
            if (NetworkHelper.isNetworkAvailable(mContext)) {
                ApiConnection.addToCart(mContext, entityId, "1", JSONObject(), null, JSONArray())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(mContext, true) {
                            override fun onNext(addToCartResponse: AddToCartResponseModel) {
                                super.onNext(addToCartResponse)
                                mContext.mContentViewBinding.loading = false
                                ToastHelper.showToast(mContext, addToCartResponse.message)
                                mContext.updateCartCount(addToCartResponse.cartCount)
                                if (addToCartResponse.success) {
                                    if (addToCartResponse.quoteId != 0) {
                                        AppSharedPref.setQuoteId(mContext, addToCartResponse.quoteId)
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                mContext.mContentViewBinding.loading = false
                                ToastHelper.showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                            }
                        })
            } else {
                addToOfflineCart(entityId)
            }
        }
    }

    private fun addToOfflineCart(entityId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.added_to_offline_cart),
                mContext.getString(R.string.offline_mode_add_to_cart_message),
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })

        try {
            BaseActivity.mDataBaseHandler.addToCartOffline(
                    entityId
                    , "1"
                    , ""
                    , ""
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
        }
    }
}