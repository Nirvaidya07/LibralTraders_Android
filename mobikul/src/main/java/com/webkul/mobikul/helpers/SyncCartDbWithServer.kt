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

package com.webkul.mobikul.helpers

import android.content.Context
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.AddToCartResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject


class SyncCartDbWithServer(private val mContext: Context)  {

     fun execute() {
        Observable.fromCallable {
            if (NetworkHelper.isNetworkAvailable(mContext)) {
                try {
                    val cursor = mDataBaseHandler.getCartTableData()
                    cursor.moveToFirst()
                    do {
                        val productId = cursor.getString(cursor.getColumnIndex("productId"))
                        val qty = cursor.getString(cursor.getColumnIndex("qty"))
                        val paramsObject = JSONObject(cursor.getString(cursor.getColumnIndex("params")))
                        val relatedProductsArray = JSONArray(cursor.getString(cursor.getColumnIndex("relatedProducts")))

                        ApiConnection.addToCart(mContext
                            , productId
                            , qty
                            , paramsObject
                            , null
                            , relatedProductsArray)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(mContext, true) {
                                override fun onNext(addToCartResponseModel: AddToCartResponseModel) {
                                    super.onNext(addToCartResponseModel)
                                    mDataBaseHandler.deleteCartEntry(productId, paramsObject.toString(), relatedProductsArray.toString())
                                    if (addToCartResponseModel.success) {
                                        if (AppSharedPref.getQuoteId(mContext) == 0 && AppSharedPref.getCustomerToken(mContext).isEmpty()) {
                                            AppSharedPref.setQuoteId(mContext, addToCartResponseModel.quoteId)
                                        }

                                        if (addToCartResponseModel.cartCount > 0) {
                                            (mContext as BaseActivity).updateCartCount(addToCartResponseModel.cartCount)
                                        }
                                    }
                                }
                            })
                    } while (cursor.moveToNext())
                    cursor.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
            .subscribeOn(Schedulers.io()) // report or post the result to main thread.
            .observeOn(AndroidSchedulers.mainThread()) // execute this RxJava
            .subscribe()

    }

    fun executeWishListToCart() {
        Observable.fromCallable {
            if (NetworkHelper.isNetworkAvailable(mContext)) {
                try {
                 if(AppSharedPref.isLoggedIn(mContext)){
                    val cursor = mDataBaseHandler.getWishListTableData()
                    cursor.moveToFirst()
                    do {
                        val productId = cursor.getString(cursor.getColumnIndex("productId"))
                        val paramsObject = cursor.getString(cursor.getColumnIndex("itemId"))
                        val qty = cursor.getString(cursor.getColumnIndex("qty"))

                        ApiConnection.wishListToCart(
                            mContext, productId, paramsObject, qty
                        )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                                override fun onNext(addToCartResponseModel: BaseModel) {
                                    super.onNext(addToCartResponseModel)
                                    mDataBaseHandler.deleteWishListCartEntry(
                                        productId,
                                        paramsObject,
                                        qty
                                    )
                                    if (addToCartResponseModel.success) {

                                        if (addToCartResponseModel.cartCount > 0) {
                                            (mContext as BaseActivity).updateCartCount(
                                                addToCartResponseModel.cartCount
                                            )
                                        }
                                    }
                                }

                            })

                    } while (cursor.moveToNext())
                    cursor.close()
                     if(mContext is MyWishListActivity){
                         mContext.mPageNumber=1
                         mContext.callApi()
                         mContext.mContentViewBinding.invalidateAll()
                     }

                }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
            .subscribeOn(Schedulers.io()) // report or post the result to main thread.
            .observeOn(AndroidSchedulers.mainThread()) // execute this RxJava
            .subscribe()

    }

    fun executeWishListAllToCart() {
        Observable.fromCallable {
            if (NetworkHelper.isNetworkAvailable(mContext)) {
                try {
                    if(AppSharedPref.isLoggedIn(mContext)){
                        val cursor = mDataBaseHandler.getWishListAllTableData()
                        cursor.moveToFirst()
                        do {
                            val productData = cursor.getString(cursor.getColumnIndex("productData"))

                            ApiConnection.addAllToCart(
                                mContext,
                               JSONObject( productData)
                            )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                                    override fun onNext(addAllToCartResponseModel: BaseModel) {
                                        super.onNext(addAllToCartResponseModel)
                                        mDataBaseHandler.clearWishListAllCartTableData()
                                        if (addAllToCartResponseModel.success) {
                                            mContext
                                            if (addAllToCartResponseModel.cartCount > 0) {
                                                (mContext as BaseActivity).updateCartCount(
                                                    addAllToCartResponseModel.cartCount
                                                )
                                            }
                                        }
                                    }
                                    })
                        } while (cursor.moveToNext())
                        cursor.close()
                        if(mContext is MyWishListActivity){
                            mContext.mPageNumber=1
                            mContext.callApi()
                            mContext.mContentViewBinding.invalidateAll()
                        }

                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
            .subscribeOn(Schedulers.io()) // report or post the result to main thread.
            .observeOn(AndroidSchedulers.mainThread()) // execute this RxJava
            .subscribe()

    }
}