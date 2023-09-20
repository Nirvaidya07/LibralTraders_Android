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

import android.content.Context
import android.widget.PopupWindow
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.adapters.CartItemsRvAdapter
import com.webkul.mobikul.adapters.MyWishListRvAdapter
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.helpers.AlertDialogHelper


class QtyPopupMenuHandler(private val mContext: Context, private val mQtyPopupWindow: PopupWindow, private val mPosition: Int) {

    fun onClickQtyNo(qty: String) {
        mQtyPopupWindow.dismiss()
        if (mContext is MyWishListActivity) {
            val cartBottomSheet = (mContext as BaseActivity).supportFragmentManager.findFragmentByTag(CartBottomSheetFragment::class.java.simpleName)
            if (cartBottomSheet != null && cartBottomSheet is CartBottomSheetFragment) {
                (cartBottomSheet.mContentViewBinding.cartItemsRv.adapter as CartItemsRvAdapter).getItemList()[mPosition].qty = qty
            } else {
                (mContext.mContentViewBinding.wishListRv.adapter as MyWishListRvAdapter).getItemList()[mPosition].qty = qty
            }

        } else {
            val cartBottomSheet = (mContext as BaseActivity).supportFragmentManager.findFragmentByTag(CartBottomSheetFragment::class.java.simpleName)
            if (cartBottomSheet != null && cartBottomSheet is CartBottomSheetFragment) {
                (cartBottomSheet.mContentViewBinding.cartItemsRv.adapter as CartItemsRvAdapter).getItemList()[mPosition].qty = qty
            }
        }
    }

    fun onClickMoreBtn() {
        mQtyPopupWindow.dismiss()
        AlertDialogHelper.showQtyDialog(mContext, mPosition)
    }
}