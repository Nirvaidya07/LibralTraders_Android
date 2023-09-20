/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.OrderItemListBottomSheetFragment
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment

class OrderItemsForReviewRvHandler(val mFragmentContext: OrderItemListBottomSheetFragment) {

    fun onClickItem(id: String, name: String, imageUrl: String) {
        mFragmentContext.dismiss()
        val productCreateReviewBottomSheetFragment = ProductCreateReviewBottomSheetFragment.newInstance(id, name, imageUrl)
        productCreateReviewBottomSheetFragment.show((mFragmentContext.context as BaseActivity).supportFragmentManager, ProductCreateReviewBottomSheetFragment::class.java.simpleName)
    }
}