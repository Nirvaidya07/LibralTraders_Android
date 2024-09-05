package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.ItemHomeTopBannerViewPagerBinding
import com.webkul.mobikul.handlers.HomePageBannerVpHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.models.homepage.BannerImage

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
class HomePageTopBannerVpAdapter(private val mContext: BaseActivity, private val mListData: ArrayList<BannerImage>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemViewPagerBannerBinding = DataBindingUtil.bind<ItemHomeTopBannerViewPagerBinding>(LayoutInflater.from(mContext).inflate(R.layout.item_home_top_banner_view_pager, container, false))
        itemViewPagerBannerBinding!!.data = mListData[position]
        itemViewPagerBannerBinding.handler = HomePageBannerVpHandler(mContext)
        itemViewPagerBannerBinding.executePendingBindings()
        container.addView(itemViewPagerBannerBinding.root)
        if (AppSharedPref.getStoreCode(mContext) == "ar")
            itemViewPagerBannerBinding.mainContainer.rotationY = 180.0f
        itemViewPagerBannerBinding.executePendingBindings()
        itemViewPagerBannerBinding.invalidateAll()
        return itemViewPagerBannerBinding.root
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return mListData.size
    }

    override fun isViewFromObject(view: View, p1: Any): Boolean {
        return view === p1
    }
}