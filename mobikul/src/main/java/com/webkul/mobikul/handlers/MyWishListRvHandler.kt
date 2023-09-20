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

package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.databinding.QtyPopupMenuBinding
import com.webkul.mobikul.fragments.ProductCommentBottomSheetFragment
import com.webkul.mobikul.fragments.ProductOptionsBottomSheetFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.OptionsItem
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MyWishListRvHandler(val mContext: MyWishListActivity) {

    fun onClickItem(id: String, name: String, dominantColor: String) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, dominantColor)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME, name)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID, id)
        mContext.startActivity(intent)
    }

    fun onClickDeleteItem(position: Int, itemId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.remove_item),
                mContext.getString(R.string.remove_wish_list_item_msg),
                false,
                mContext.getString(R.string.yes_remove_it),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                mContext.mContentViewBinding.loading = true
                ApiConnection.removeFromWishList(mContext, itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                            override fun onNext(removeFromWishList: BaseModel) {
                                super.onNext(removeFromWishList)
                                mContext.mContentViewBinding.loading = false
                                if (removeFromWishList.success) {
                                    BaseActivity.mDataBaseHandler.removeWishListRecentlyViewedProduct(AppSharedPref.getStoreId(mContext), AppSharedPref.getCurrencyCode(mContext), mContext.mContentViewBinding.data!!.wishList[position].productId)
                                    onSuccessfulRemoveFromWishListResponse(removeFromWishList)
                                } else {
                                    onFailureRemoveFromWishListResponse(removeFromWishList)
                                }
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                mContext.mContentViewBinding.loading = false
                                onErrorRemoveFromWishListResponse(e)
                            }
                        })
            },
                mContext.getString(R.string.cancel),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
    }

    private fun onSuccessfulRemoveFromWishListResponse(removeFromWishList: BaseModel) {
        //mContext.updateCartCount(removeFromWishList.cartCount)
        showToast(mContext, removeFromWishList.message, Toast.LENGTH_SHORT)
        mContext.mPageNumber = 1
        mContext.callApi()

    }

    private fun onFailureRemoveFromWishListResponse(removeFromWishList: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                removeFromWishList.message,
                false,
                mContext.getString(R.string.ok),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
                , null
                , null)
    }

    private fun onErrorRemoveFromWishListResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.ok),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
                , null
                , null)
    }

    fun onClickInfoBtn(productOptions: ArrayList<OptionsItem>) {
        val productOptionsBottomSheetFragment = ProductOptionsBottomSheetFragment.newInstance(productOptions)
        productOptionsBottomSheetFragment.show(mContext.supportFragmentManager, productOptionsBottomSheetFragment.tag)
    }

    fun onClickCommentBtn(position: Int, comment: String?) {
        val productCommentBottomSheetFragment = ProductCommentBottomSheetFragment.newInstance(position, comment
                ?: "")
        productCommentBottomSheetFragment.show(mContext.supportFragmentManager, productCommentBottomSheetFragment.tag)
    }

    fun onClickAddToCartItem(productId: String, itemId: String, qty: String) {
        if (NetworkHelper.isNetworkAvailable(mContext)) {
            mContext.mContentViewBinding.loading = true
            ApiConnection.wishListToCart(mContext, productId, itemId, qty)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                    override fun onNext(wishListToCartResponse: BaseModel) {
                        super.onNext(wishListToCartResponse)
                        mContext.mContentViewBinding.loading = false
                        if (wishListToCartResponse.success) {
                            mContext.updateCartCount(wishListToCartResponse.cartCount)
                            onSuccessfulWishListToCartResponse(wishListToCartResponse)
                        } else {
                            onFailureWishListToCartResponse(wishListToCartResponse)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContext.mContentViewBinding.loading = false
                        onErrorWishListToCartResponse(e)
                    }
                })
        }else{
            addToOfflineWishListToCart(productId,itemId,qty)
        }
    }



    private fun onSuccessfulWishListToCartResponse(wishListToCartResponse: BaseModel) {
        mContext.updateCartCount(wishListToCartResponse.cartCount)
        showToast(mContext, wishListToCartResponse.message, Toast.LENGTH_SHORT)
        mContext.mPageNumber = 1
        mContext.callApi()
    }

    private fun onFailureWishListToCartResponse(wishListToCartResponse: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.move_to_cart),
                wishListToCartResponse.message,
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun onErrorWishListToCartResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun addToOfflineWishListToCart(productId: String, itemId: String, qty: String) {
        mContext.mContentViewBinding.loading = false
        try {
        AlertDialogHelper.showNewCustomDialog(
            mContext,
            mContext.getString(R.string.added_to_offline_wish_list_to_cart),
            mContext.getString(R.string.offline_mode_add_to_cart_message),
            false,
            mContext.getString(R.string.ok),
            { dialogInterface: DialogInterface, _: Int ->

                    val updated=    BaseActivity.mDataBaseHandler.addWishListToCartOffline(
                        productId
                        ,itemId
                        , qty
                    )
                    if(updated)
                    {
                        mContext.removeItem(productId)
                    }
                dialogInterface.dismiss()
            })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToast(mContext, mContext.getString(R.string.something_went_wrong))
        }
    }


    fun onClickQtyBtn(view: View, position: Int) {
        val layout = mContext.layoutInflater.inflate(R.layout.qty_popup_menu, null)
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val mQtyPopupWindow = PopupWindow(layout, 200, layout.measuredHeight + 10, true)
        mQtyPopupWindow.isOutsideTouchable = true
        mQtyPopupWindow.isFocusable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mQtyPopupWindow.elevation = 10f
        }
        mQtyPopupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        mQtyPopupWindow.showAsDropDown(view, 0, 5)

        val mBinding: QtyPopupMenuBinding? = DataBindingUtil.bind(layout)
        mBinding?.handler = QtyPopupMenuHandler(mContext, mQtyPopupWindow, position)
    }
}