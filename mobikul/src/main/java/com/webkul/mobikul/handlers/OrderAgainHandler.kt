package com.webkul.mobikul.handlers

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyOrdersActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.AddToCartResponseModel
import com.webkul.mobikul.models.product.ProductData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class OrderAgainHandler(val mContext: MyOrdersActivity) {
    fun onClickQtyDecrementBtn(productData: ProductData) {
//        productData.qty_order
        if (productData.qty_order!!.toInt() > 1) {
            productData.qty_order = (productData.qty_order!!.toInt() - 1)
            if (mContext.orderAgainAdapter != null) {
                mContext.orderAgainAdapter.notifyDataSetChanged()
            }
        }

    }

    fun onClickQtyIncrementBtn(productData: ProductData) {
        productData.qty_order = (productData.qty_order!!.toInt() + 1)
        if (mContext.orderAgainAdapter != null) {
            mContext.orderAgainAdapter.notifyDataSetChanged()
        }
//        }
    }
    fun onClickReorder(productData: ProductData) {
        Log.d("TAG", "onClickReorder: " + productData.sku)
        mContext.callCartMine("", "", productData)
//        mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = true

        if (NetworkHelper.isNetworkAvailable(mContext)) {
            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = true
            Log.d(
                "TAG",
                "onClickReorder: " + productData.productid.toString() + "   " + productData.qty_order.toString()
            )
            ApiConnection.addToCart(
                mContext,
                productData.productid.toString(),
                productData.qty_order.toString(),
                JSONObject(),
                null,
                JSONArray()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(mContext, true) {
                    override fun onNext(addToCartResponse: AddToCartResponseModel) {
                        super.onNext(addToCartResponse)
                        mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        ToastHelper.showToast(mContext, addToCartResponse.message)
                        if (addToCartResponse.cartCount!=0){
                            mContext.lastItemCount=addToCartResponse.cartCount
                        }
                        Log.d("TAG", "onNext: "+addToCartResponse.cartCount+" "+mContext.lastItemCount+"\n"+addToCartResponse)
                        mContext.updateCartCount(mContext.lastItemCount)
                        if (addToCartResponse.success) {
                            if (addToCartResponse.quoteId != 0) {
                                AppSharedPref.setQuoteId(mContext, addToCartResponse.quoteId)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        Log.d("TAG", "onNext:error "+e)
                        mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = true
                        ToastHelper.showToast(
                            mContext,
                            mContext.resources.getString(R.string.something_went_wrong)
                        )
                    }
                })
        } else {
            addToOfflineCart(productData.productid.toString(),productData.qty_order.toString())
        }
    }

    private fun addToOfflineCart(entityId: String,quntity:String) {
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
                entityId, quntity, "", ""
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
        }
    }
}