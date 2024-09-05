package com.webkul.mobikul.handlers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyOrdersActivity
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.fragments.OrderItemListBottomSheetFragment
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.OrderDetailsModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

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

class MyOrdersRvHandler(val mContext: Context, private val mIncrementId: String) {

    var mHashIdentifier = ""

    fun onClickViewOrder() {
        val intent = Intent(mContext, OrderDetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_INCREMENT_ID, mIncrementId)
        mContext.startActivity(intent)
    }

    fun onClickReorder() {
        if (mContext is MyOrdersActivity)
            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = true
        ApiConnection.reorder(mContext, mIncrementId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                    override fun onNext(reorderResponseModel: BaseModel) {
                        super.onNext(reorderResponseModel)
                        if (mContext is MyOrdersActivity)
                            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        if (reorderResponseModel.success) {
                            onSuccessfulResponse(reorderResponseModel)
                        } else {
                            onFailureResponse(reorderResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        if (mContext is MyOrdersActivity)
                            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(reorderResponseModel: BaseModel) {
        (mContext as BaseActivity).updateCartCount(reorderResponseModel.cartCount)
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.reorder),
                reorderResponseModel.message,
                false,
                mContext.getString(R.string.go_to_cart),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    val cartBottomSheetFragment = CartBottomSheetFragment()
                    cartBottomSheetFragment.show(mContext.supportFragmentManager, CartBottomSheetFragment::class.java.simpleName)
                }, mContext.getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    private fun onFailureResponse(reorderResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.error),
                reorderResponseModel.message,
                false,
                mContext.getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickReorder()
                }, mContext.getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickReorder()
                }, mContext.getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun onClickReview() {
        if (mContext is MyOrdersActivity) {
            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = true
        }
        mHashIdentifier = Utils.getMd5String("getOrderDetails" + AppSharedPref.getStoreId(mContext) + AppSharedPref.getCustomerToken(mContext) + AppSharedPref.getCurrencyCode(mContext) + mIncrementId)
        ApiConnection.getOrderDetails(mContext, BaseActivity.mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mIncrementId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<OrderDetailsModel>(mContext, true) {
                    override fun onNext(orderDetailsModel: OrderDetailsModel) {
                        super.onNext(orderDetailsModel)
                        if (mContext is MyOrdersActivity) {
                            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        if (orderDetailsModel.success) {
                            onItemListSuccessResponse(orderDetailsModel)
                        } else {
                            if (orderDetailsModel.message.isBlank()) {
                                ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
                            } else {
                                ToastHelper.showToast(mContext, orderDetailsModel.message)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        if (mContext is MyOrdersActivity) {
                            mContext.mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        if ((!NetworkHelper.isNetworkAvailable(mContext) || (e is HttpException && e.code() == 304))) {
                            checkLocalData()
                        } else {
                            ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
                        }
                    }
                })
    }

    private fun checkLocalData() {
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onItemListSuccessResponse(BaseActivity.mObjectMapper.readValue(response, OrderDetailsModel::class.java))
                        } else {
                            ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        (mContext as BaseActivity).mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })

    }

    private fun onItemListSuccessResponse(orderDetailsModel: OrderDetailsModel) {
        if (!orderDetailsModel.orderData?.items.isNullOrEmpty() && orderDetailsModel.orderData?.items?.size == 1) {
            val productCreateReviewBottomSheetFragment = ProductCreateReviewBottomSheetFragment.newInstance(orderDetailsModel.orderData?.items?.get(0)?.productId, orderDetailsModel?.orderData?.items?.get(0)?.name, orderDetailsModel.orderData?.items?.get(0)?.imageUrl)
            productCreateReviewBottomSheetFragment.show((mContext as BaseActivity).supportFragmentManager, ProductCreateReviewBottomSheetFragment::class.java.simpleName)
        } else {
            val orderItemListBottomSheetFragment = orderDetailsModel.orderData?.items?.let { OrderItemListBottomSheetFragment.newInstance(it) }
            orderItemListBottomSheetFragment?.show((mContext as BaseActivity).supportFragmentManager, orderItemListBottomSheetFragment.tag)
        }
    }
}