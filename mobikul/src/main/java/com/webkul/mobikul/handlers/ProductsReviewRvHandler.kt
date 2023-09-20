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
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.fragments.ProductOptionsBottomSheetFragment
import com.webkul.mobikul.models.user.OptionsItem


class ProductsReviewRvHandler(val mContext: Context) {

    fun onClickInfoBtn(productOptions: ArrayList<OptionsItem>) {
        val productOptionsBottomSheetFragment = ProductOptionsBottomSheetFragment.newInstance(productOptions)
        productOptionsBottomSheetFragment.show((mContext as CheckoutActivity).supportFragmentManager, productOptionsBottomSheetFragment.tag)
    }
}