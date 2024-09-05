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
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.QtyPopupMenuBinding
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CART_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ITEM_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.catalog.CartItem
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CartItemsRvHandler(val mFragmentContext: CartBottomSheetFragment) {

    fun onClickItem(id: String, name: String, dominantColor: String, groupedProductId: String) {
        val intent = (mFragmentContext.context?.applicationContext as MobikulApplication).getProductDetailsActivity(mFragmentContext.requireContext())
        intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, dominantColor)
        intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, name)
        if (groupedProductId.isBlank() || groupedProductId == "0") {
            intent.putExtra(BUNDLE_KEY_PRODUCT_ID, id)
        } else {
            intent.putExtra(BUNDLE_KEY_PRODUCT_ID, groupedProductId)
        }
        mFragmentContext.startActivity(intent)
    }

    fun onClickQtyBtn(view: View, position: Int) {
        val layout = mFragmentContext.layoutInflater.inflate(R.layout.qty_popup_menu, null)
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
        mBinding?.handler = QtyPopupMenuHandler(mFragmentContext.requireContext(), mQtyPopupWindow, position)
    }

    fun onClickMoveToWishListBtn(itemId: String) {
        if (AppSharedPref.isLoggedIn(mFragmentContext.requireContext())) {
            AlertDialogHelper.showNewCustomDialog(
                    mFragmentContext.context as BaseActivity,
                    mFragmentContext.getString(R.string.move_to_wish_list),
                    mFragmentContext.getString(R.string.move_to_wish_list_msg),
                    false,
                    mFragmentContext.getString(R.string.yes_move_it),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        mFragmentContext.mContentViewBinding.loading = true
                        ApiConnection.moveToWishList(mFragmentContext.requireContext(), itemId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                                    override fun onNext(moveToWishListResponse: BaseModel) {
                                        super.onNext(moveToWishListResponse)
                                        mFragmentContext.mContentViewBinding.loading = false
                                        if (moveToWishListResponse.success) {
                                            onSuccessfulResponse(moveToWishListResponse)
                                        } else {
                                            onFailureResponse(moveToWishListResponse)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        super.onError(e)
                                        mFragmentContext.mContentViewBinding.loading = false
                                        onErrorResponse(e)
                                    }
                                })
                    },
                    mFragmentContext.getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    })
        } else {
            showLoginAlertDialog(mFragmentContext.getString(R.string.login_first))
        }
    }

    fun showLoginAlertDialog(content: String?) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.login_required),
                content,
                true,
                mFragmentContext.getString(R.string.login),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mFragmentContext.startActivity((mFragmentContext.requireActivity().application as MobikulApplication).getLoginAndSignUpActivity(mFragmentContext.requireContext()))
                },
                mFragmentContext.getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    fun onClickRemoveItemBtn(itemId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.remove_item),
                mFragmentContext.getString(R.string.remove_from_cart_msg),
                false,
                mFragmentContext.getString(R.string.yes_remove_it),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mFragmentContext.mContentViewBinding.loading = true
                    ApiConnection.removeFromCart(mFragmentContext.requireContext(), itemId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                                override fun onNext(removeFromWishListResponse: BaseModel) {
                                    super.onNext(removeFromWishListResponse)
                                    mFragmentContext.mContentViewBinding.loading = false
                                    if (removeFromWishListResponse.success) {
                                        (mFragmentContext.context as BaseActivity).updateCartCount(removeFromWishListResponse.cartCount)
                                        onSuccessfulResponse(removeFromWishListResponse)
                                    } else {
                                        onFailureResponse(removeFromWishListResponse)
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    mFragmentContext.mContentViewBinding.loading = false
                                    onErrorResponse(e)
                                }
                            })
                }
                ,
                mFragmentContext.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    private fun onSuccessfulResponse(response: BaseModel) {
        showToast(mFragmentContext.requireContext(), response.message, Toast.LENGTH_SHORT)
        mFragmentContext.callApi()
    }

    private fun onFailureResponse(response: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                response.message,
                false,
                mFragmentContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.requireContext(), error),
                false,
                mFragmentContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    fun onClickEditProduct(cartItem: CartItem) {
        val intent = (mFragmentContext.context?.applicationContext as MobikulApplication).getProductDetailsActivity(mFragmentContext.requireContext())
        intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, cartItem.dominantColor)
        intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, cartItem.name)
        intent.putExtra(BUNDLE_KEY_PRODUCT_ID, cartItem.productId)
        intent.putExtra(BUNDLE_KEY_ITEM_ID, cartItem.id)
        intent.putExtra(BUNDLE_KEY_CART_DATA, cartItem)
        mFragmentContext.startActivity(intent)
    }
}