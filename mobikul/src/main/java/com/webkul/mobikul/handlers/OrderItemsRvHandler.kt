package com.webkul.mobikul.handlers

import android.content.Context
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment
import com.webkul.mobikul.fragments.ProductOptionsBottomSheetFragment
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.models.user.OptionsItem

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

class OrderItemsRvHandler(val mContext: Context) {

    fun onClickItem(id: String, name: String) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BUNDLE_KEY_PRODUCT_ID, id)
        intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, name)
        mContext.startActivity(intent)
    }

    fun onClickInfoBtn(productOptions: ArrayList<OptionsItem>) {
        val productOptionsBottomSheetFragment = ProductOptionsBottomSheetFragment.newInstance(productOptions)
        productOptionsBottomSheetFragment.show((mContext as BaseActivity).supportFragmentManager, productOptionsBottomSheetFragment.tag)
    }

    fun onClickWriteReviewBtn(id: String, name: String, imageUrl: String) {
        val productCreateReviewBottomSheetFragment = ProductCreateReviewBottomSheetFragment.newInstance(id, name, imageUrl)
        productCreateReviewBottomSheetFragment.show((mContext as BaseActivity).supportFragmentManager, ProductCreateReviewBottomSheetFragment::class.java.simpleName)
    }
}