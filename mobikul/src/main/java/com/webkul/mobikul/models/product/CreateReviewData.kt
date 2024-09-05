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

package com.webkul.mobikul.models.product

import com.libraltraders.android.R
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment
import com.webkul.mobikul.helpers.Utils


class CreateReviewData {

    var productImage: String = ""
    var productName: String = ""

    var nickName: String = ""
    var summary: String = ""
    var comment: String = ""

    fun isFormValidated(fragmentContext: ProductCreateReviewBottomSheetFragment): Boolean {
        var isFormValidated = true

        if (comment.isBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.commentTil.error = fragmentContext.getString(R.string.comment) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.commentTil)
            fragmentContext.mContentViewBinding.commentTil.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.commentTil.isErrorEnabled = false
            fragmentContext.mContentViewBinding.commentTil.error = null
        }

        if (summary.isBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.summaryTil.error = fragmentContext.getString(R.string.summary) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.summaryTil)
            fragmentContext.mContentViewBinding.summaryTil.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.summaryTil.isErrorEnabled = false
            fragmentContext.mContentViewBinding.summaryTil.error = null
        }

        if (nickName.isBlank()) {
            isFormValidated = false
            fragmentContext.mContentViewBinding.nicknameTil.error = fragmentContext.getString(R.string.nickname) + " " + fragmentContext.getString(R.string.is_required)
            Utils.showShakeError(fragmentContext.requireContext(), fragmentContext.mContentViewBinding.nicknameTil)
            fragmentContext.mContentViewBinding.nicknameTil.requestFocus()
        } else {
            fragmentContext.mContentViewBinding.nicknameTil.isErrorEnabled = false
            fragmentContext.mContentViewBinding.nicknameTil.error = null
        }

        return isFormValidated
    }
}