package com.webkul.mobikul.handlers

import com.webkul.mobikul.activities.HomeActivity

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

class HomeActivityHandler(val mContext: HomeActivity) {

    fun onClickBackToTop() {
        mContext.mContentViewBinding.mainScroller.smoothScrollTo(0, 0);
    }

    fun onSearchClick() {
        mContext.openMaterialSearchView()
    }
}