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

import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.fragments.ProductCommentBottomSheetFragment
import com.webkul.mobikul.helpers.Utils


class ProductCommentBottomSheetFragmentHandler(private var mFragmentContext: ProductCommentBottomSheetFragment) {

    fun onClickCancelBtn() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.wishlistCommentBottomSheet)
        mFragmentContext.dismiss()
    }

    fun onClickUpdateBtn(description: String?) {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.wishlistCommentBottomSheet)
        (mFragmentContext.context as MyWishListActivity).mContentViewBinding.data!!.wishList[mFragmentContext.mSelectedItemPosition].description = if (description.isNullOrBlank()) "" else description!!
        mFragmentContext.dismiss()
    }
}