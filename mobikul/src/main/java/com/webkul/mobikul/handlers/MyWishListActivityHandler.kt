package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.content.Intent
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.activities.WishListSharingActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

class MyWishListActivityHandler(val mContext: MyWishListActivity) {

    var mIsUpdateWishList = false

    fun onClickUpdateWishList() {
        mIsUpdateWishList = true
        mContext.mContentViewBinding.loading = true
        ApiConnection.updateWishList(mContext, mContext.mContentViewBinding.data!!.getUpdateWishListItemJsonArray())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                    override fun onNext(updateWishListResponseModel: BaseModel) {
                        super.onNext(updateWishListResponseModel)
                        mContext.mContentViewBinding.loading = false
                        if (updateWishListResponseModel.success) {
                            onSuccessfulResponse(updateWishListResponseModel)
                        } else {
                            onFailureResponse(updateWishListResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContext.mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    fun onClickShareWishList() {
        mContext.startActivity(Intent(mContext, WishListSharingActivity::class.java))
    }

    fun onClickAddAllToCart() {
        if (NetworkHelper.isNetworkAvailable(mContext)) {
            mIsUpdateWishList = false
            mContext.mContentViewBinding.loading = true
            ApiConnection.addAllToCart(
                mContext,
                mContext.mContentViewBinding.data!!.productQuantities()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                    override fun onNext(addAllToCartResponseModel: BaseModel) {
                        super.onNext(addAllToCartResponseModel)
                        mContext.mContentViewBinding.loading = false
                        if (addAllToCartResponseModel.success) {
                            mContext.updateCartCount(addAllToCartResponseModel.cartCount)
                            onSuccessfulResponse(addAllToCartResponseModel)
                        } else {
                            onFailureResponse(addAllToCartResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContext.mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
        }else{
            addToOfflineWishListAllToCart()
        }
    }

    private fun addToOfflineWishListAllToCart() {
        mContext.mContentViewBinding.loading = false
        try {
            AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.add_to_offline_wish_list_all_to_cart),
                mContext.getString(R.string.offline_mode_add_to_cart_message),
                false,
                mContext.getString(R.string.ok),
                { dialogInterface: DialogInterface, _: Int ->

                    val updated=    BaseActivity.mDataBaseHandler.addWishListAllToCartOffline(
                        mContext.mContentViewBinding.data!!.productQuantities()
                    )
                    if(updated)
                    {
                        mContext.removeAllItem()
                    }
                    dialogInterface.dismiss()
                })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
        }
    }

    private fun onSuccessfulResponse(updateResponseModel: BaseModel) {
        ToastHelper.showToast(mContext, updateResponseModel.message)
        mContext.mPageNumber = 1
        mContext.callApi()
    }

    private fun onFailureResponse(updateResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                updateResponseModel.message,
                false,
                mContext.getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                    if (mIsUpdateWishList) {
                        onClickUpdateWishList()
                    } else {
                        onClickAddAllToCart()
                    }
                }
                , mContext.getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        })
    }
}