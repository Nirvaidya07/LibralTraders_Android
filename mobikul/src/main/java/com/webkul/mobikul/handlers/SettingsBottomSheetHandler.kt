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

import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.fragments.SettingsBottomSheetFragment
import com.webkul.mobikul.helpers.ToastHelper

class SettingsBottomSheetHandler(var mFragmentContext: SettingsBottomSheetFragment) {

    fun onClickCancelBtn() {
        mFragmentContext.dismiss()
    }

    fun onClickClearRecentlyViewedProductsData() {
        mDataBaseHandler.clearRecentlyViewedProductsTableData()
        ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.requireContext().getString(R.string.recently_viewed_product_data_cleared))
        if (mFragmentContext.context is HomeActivity) {
            (mFragmentContext.context as HomeActivity).setupRecentlyViewedCarouselsLayout()
        }
    }

    fun onClickClearRecentSearchData() {
        mDataBaseHandler.clearRecentSearchData()
        ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.requireContext().getString(R.string.recently_search_history_cleared))
    }

    fun onClickDeviceSettingsButton() {
        mFragmentContext.mContentViewBinding.data!!.goToSettings()
    }
}