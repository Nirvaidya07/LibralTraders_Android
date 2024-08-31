package com.webkul.mobikul.helpers

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputLayout
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.adapters.CartItemsRvAdapter
import com.webkul.mobikul.adapters.MyWishListRvAdapter
import com.webkul.mobikul.fragments.CartBottomSheetFragment


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

class AlertDialogHelper {

    companion object {

        fun showNewCustomDialog(context: BaseActivity?, title: String?, content: String?, cancelable: Boolean = true, positiveButtonText: String? = null, positiveButtonClickListener: DialogInterface.OnClickListener? = null, negativeButtonText: String? = null, negativeButtonClickListener: DialogInterface.OnClickListener? = null) {
            if (context != null && title != null && content != null) {
                if (context.mCustomDialog == null || !context.mCustomDialog!!.isShowing) {
                    val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                    builder.setTitle(title)
                    builder.setMessage(content)
                    builder.setCancelable(cancelable)
                    if (positiveButtonText != null && positiveButtonClickListener != null) {
                        builder.setPositiveButton(positiveButtonText, positiveButtonClickListener)
                    } else {
                        builder.setPositiveButton(context.resources.getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }
                    }
                    if (negativeButtonText != null && negativeButtonClickListener != null) {
                        builder.setNegativeButton(negativeButtonText, negativeButtonClickListener)
                    }
                    context.mCustomDialog = builder.create()
                    context.mCustomDialog?.show()
                }
            }
        }

        fun showQtyDialog(context: Context?, position: Int) {
            if (context != null) {
                dismissCustomDialog(context as BaseActivity)
                val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                builder.setTitle(context.getString(R.string.enter_quantity))
                builder.setView(context.layoutInflater.inflate(R.layout.dialog_enter_qty, null))
                builder.setCancelable(false)
                context.mCustomDialog = builder.create()
                context.mCustomDialog?.show()
                context.mCustomDialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

                val editTil = context.mCustomDialog!!.findViewById<View>(R.id.qty_til) as TextInputLayout
                val saveBtn = context.mCustomDialog!!.findViewById<View>(R.id.save_btn) as AppCompatTextView
                saveBtn.setOnClickListener {
                    if (editTil.editText!!.text.isNullOrBlank()) {
                        editTil.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.quantity)
                        Utils.showShakeError(context, editTil)
                        editTil.requestFocus()
                    } else if (editTil.editText!!.text.toString().trim() == "0") {
                        editTil.error = context.getString(R.string.wishlist_invalid_quantity)
                        Utils.showShakeError(context, editTil)
                        editTil.requestFocus()
                    } else {
                        context.mCustomDialog?.currentFocus?.let { it1 -> Utils.hideKeyboard(it1) }
                        val cartBottomSheet = (context as BaseActivity).supportFragmentManager.findFragmentByTag(CartBottomSheetFragment::class.java.simpleName)

                        if (context is MyWishListActivity && cartBottomSheet==null) {
                            (context.mContentViewBinding.wishListRv.adapter as MyWishListRvAdapter).getItemList()[position].qty = editTil.editText!!.text.toString()
                        } else {
                            if (cartBottomSheet != null && cartBottomSheet is CartBottomSheetFragment)
                                (cartBottomSheet.mContentViewBinding.cartItemsRv.adapter as CartItemsRvAdapter).getItemList()[position].qty = editTil.editText!!.text.toString()
                        }
                        dismissCustomDialog(context)
                    }
                }

                val cancelBtn = context.mCustomDialog!!.findViewById<View>(R.id.cancel_btn) as AppCompatTextView
                cancelBtn.setOnClickListener {
                    dismissCustomDialog(context)
                }
            }
        }

        private fun dismissCustomDialog(context: BaseActivity) {
            if (context.mCustomDialog != null && context.mCustomDialog!!.isShowing) {
                context.mCustomDialog?.dismiss()
            }
        }
    }
}