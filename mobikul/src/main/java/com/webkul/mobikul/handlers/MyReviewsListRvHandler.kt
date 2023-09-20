package com.webkul.mobikul.handlers

import android.content.Context
import android.content.Intent
import com.webkul.mobikul.activities.ReviewDetailsActivity
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_REVIEW_ID
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.models.user.ReviewsListItem

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

class MyReviewsListRvHandler(val mContext: Context) {

    fun onClickProductName(reviewsListItem: ReviewsListItem) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BUNDLE_KEY_PRODUCT_ID, reviewsListItem.productId)
        intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, reviewsListItem.proName)
        intent.putExtra(BUNDLE_KEY_PRODUCT_IMAGE, reviewsListItem.thumbNail)
        intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, reviewsListItem.dominantColor)
        mContext.startActivity(intent)
    }

    fun onClickDetails(reviewId: String) {
        val intent = Intent(mContext, ReviewDetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_REVIEW_ID, reviewId)
        mContext.startActivity(intent)
    }
}