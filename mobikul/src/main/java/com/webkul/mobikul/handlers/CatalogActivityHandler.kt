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

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.fragments.FilterBottomSheetFragment
import com.webkul.mobikul.fragments.SortBottomSheetFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ConstantsHelper.VIEW_TYPE_GRID
import com.webkul.mobikul.helpers.ConstantsHelper.VIEW_TYPE_LIST
import com.webkul.mobikul.helpers.ToastHelper

class CatalogActivityHandler(private val mContext: CatalogActivity) {

    fun onClickSortBtn() {
        if (!mContext.mContentViewBinding.data!!.productList.isNullOrEmpty() && mContext.mContentViewBinding.data!!.sortingData.isNotEmpty()) {
            val sortBottomSheetFragment = SortBottomSheetFragment()
            sortBottomSheetFragment.show(mContext.supportFragmentManager, sortBottomSheetFragment.tag)
        } else {
            ToastHelper.showToast(mContext, mContext.getString(R.string.sort_options_not_available))
        }
    }

    fun onClickFilterBtn() {
        if (mContext.mFilterInputJson.length() > 0 || mContext.mContentViewBinding.data!!.layeredData.isNotEmpty()) {
            val filterBottomSheetFragment = FilterBottomSheetFragment()
            filterBottomSheetFragment.show(mContext.supportFragmentManager, filterBottomSheetFragment.tag)
        } else {
            ToastHelper.showToast(mContext, mContext.getString(R.string.filter_options_not_available))
        }
    }

    fun onClickViewSwitcher(view: View) {
        if (mContext.mContentViewBinding.productsRv.adapter!!.getItemViewType(0) == VIEW_TYPE_LIST) {
            mContext.mContentViewBinding.productsRv.layoutManager = GridLayoutManager(mContext, 2)
            (view as AppCompatTextView).setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_grid_view_wrapper), null, null, null)
            view.text = mContext.resources.getString(R.string.grid)
            AppSharedPref.setViewType(mContext, VIEW_TYPE_GRID)
        } else {
            mContext.mContentViewBinding.productsRv.layoutManager = LinearLayoutManager(mContext)
            (view as AppCompatTextView).setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_list_view_wrapper), null, null, null)
            view.text = mContext.resources.getString(R.string.list)
            AppSharedPref.setViewType(mContext, VIEW_TYPE_LIST)
        }
    }
}