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
import com.libraltraders.android.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.adapters.WebsitesRvAdapter
import com.libraltraders.android.databinding.FragmentWebsiteBottomSheetBinding
import com.webkul.mobikul.handlers.WebsiteBottomSheetHandler


class WebsiteBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentWebsiteBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_website_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.handler = WebsiteBottomSheetHandler(this)
        setupCurrenciesRv()
    }

    private fun setupCurrenciesRv() {
        mContentViewBinding.websitesRv.adapter = (context as HomeActivity).mHomePageDataModel.websiteData?.let { WebsitesRvAdapter(this, it) }
        mContentViewBinding.websitesRv.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        mContentViewBinding.websitesRv.isNestedScrollingEnabled = false
    }
}