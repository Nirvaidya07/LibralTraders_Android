package com.webkul.mobikul.models

import androidx.databinding.BaseObservable
import com.webkul.mobikul.fragments.DeliveryboyMakeReviewFragment
import com.webkul.mobikul.helpers.Utils


/**
 * Webkul Software.
 *
 * @author Webkul
 * @category Mobikul
 * @package com.webkul.mobikulmp.fragments
 * @copyright Copyright (c) 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html
 * @date 26/07/19
 */

class DeliveryboyMakeReviewData : BaseObservable() {

    var nickName = ""

    var summary = ""

    var comment = ""

    var rating = 1


    fun isFormValidated(sellerMakeReviewFragment: DeliveryboyMakeReviewFragment): Boolean {

        var isFormValidated = true

        if (comment.isBlank()) {
            isFormValidated = false
            sellerMakeReviewFragment.mContentViewBinding.commentTil.error = sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.comment) + " " + sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.is_required)
            Utils.showShakeError(sellerMakeReviewFragment.requireContext(), sellerMakeReviewFragment.mContentViewBinding.commentTil)
            sellerMakeReviewFragment.mContentViewBinding.commentTil.requestFocus()
        } else {
            sellerMakeReviewFragment.mContentViewBinding.commentTil.isErrorEnabled = false
            sellerMakeReviewFragment.mContentViewBinding.commentTil.error = null
        }

        if (summary.isBlank()) {
            isFormValidated = false
            sellerMakeReviewFragment.mContentViewBinding.summaryTil.error = sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.summary) + " " + sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.is_required)
            Utils.showShakeError(sellerMakeReviewFragment.requireContext(), sellerMakeReviewFragment.mContentViewBinding.summaryTil)
            sellerMakeReviewFragment.mContentViewBinding.summaryTil.requestFocus()
        } else {
            sellerMakeReviewFragment.mContentViewBinding.summaryTil.isErrorEnabled = false
            sellerMakeReviewFragment.mContentViewBinding.summaryTil.error = null
        }

        if (nickName.isBlank()) {
            isFormValidated = false
            sellerMakeReviewFragment.mContentViewBinding.nicknameTil.error = sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.nickname) + " " + sellerMakeReviewFragment.getString(com.webkul.mobikul.R.string.is_required)
            Utils.showShakeError(sellerMakeReviewFragment.requireContext(), sellerMakeReviewFragment.mContentViewBinding.nicknameTil)
            sellerMakeReviewFragment.mContentViewBinding.nicknameTil.requestFocus()
        } else {
            sellerMakeReviewFragment.mContentViewBinding.nicknameTil.isErrorEnabled = false
            sellerMakeReviewFragment.mContentViewBinding.nicknameTil.error = null
        }
        return isFormValidated
    }
}
