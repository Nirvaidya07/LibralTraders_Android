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
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemProductPageViewPagerBinding
import com.webkul.mobikul.handlers.ProductSliderHandler
import com.webkul.mobikul.models.product.ImageGalleryData

class ProductPageSliderAdapter(private val mContext: ProductDetailsActivity, private val mProductName: String, private val mListData: ArrayList<ImageGalleryData>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemProductPageViewPagerBinding = DataBindingUtil.bind<ItemProductPageViewPagerBinding>(LayoutInflater.from(mContext).inflate(R.layout.item_product_page_view_pager, container, false))
        itemProductPageViewPagerBinding!!.data = mListData[position]
        itemProductPageViewPagerBinding.handler = ProductSliderHandler(mListData)
        itemProductPageViewPagerBinding.productName = mProductName
        itemProductPageViewPagerBinding.position = position
        itemProductPageViewPagerBinding.executePendingBindings()
        container.addView(itemProductPageViewPagerBinding.root)
        return itemProductPageViewPagerBinding.root
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