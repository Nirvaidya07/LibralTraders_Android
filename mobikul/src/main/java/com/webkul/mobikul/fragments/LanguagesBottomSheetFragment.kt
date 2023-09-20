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

package com.webkul.mobikul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.adapters.StoresRvAdapter
import com.webkul.mobikul.databinding.FragmentLanguageBottomSheetBinding
import com.webkul.mobikul.handlers.LanguagesBottomSheetHandler


class LanguagesBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentLanguageBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_language_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.handler = LanguagesBottomSheetHandler(this)
        setupCurrenciesRv()
    }

    private fun setupCurrenciesRv() {
        mContentViewBinding.storesRv.adapter = (context as HomeActivity).mHomePageDataModel.storeData?.let { StoresRvAdapter(this, it) }
        mContentViewBinding.storesRv.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        mContentViewBinding.storesRv.isNestedScrollingEnabled = false
    }
}