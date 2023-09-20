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

package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.databinding.ItemCategoryBannerViewPagerBinding
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.models.homepage.BannerImage

class CategoryBannerVpAdapter(private val mContext: BaseActivity, private val mListData: ArrayList<BannerImage>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemCategoryBannerViewPagerBinding = DataBindingUtil.bind<ItemCategoryBannerViewPagerBinding>(LayoutInflater.from(mContext).inflate(R.layout.item_category_banner_view_pager, container, false))
        itemCategoryBannerViewPagerBinding?.data = mListData[position]
        itemCategoryBannerViewPagerBinding?.executePendingBindings()
        container.addView(itemCategoryBannerViewPagerBinding?.root)
        if (AppSharedPref.getStoreCode(mContext) == "ar")
            itemCategoryBannerViewPagerBinding?.mainContainer?.rotationY = 180.0f
        return itemCategoryBannerViewPagerBinding!!.root
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