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

import com.webkul.mobikul.activities.ReviewDetailsActivity
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.MobikulApplication


class ReviewDetailsActivityHandler(val mContext: ReviewDetailsActivity) {

    fun onClickProduct(id: String, name: String) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID, id)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME, name)
        mContext.startActivity(intent)
    }
}