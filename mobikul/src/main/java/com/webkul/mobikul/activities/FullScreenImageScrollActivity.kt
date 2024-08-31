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

package com.webkul.mobikul.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.libraltraders.android.R
import com.webkul.mobikul.customviews.TouchImageView
import com.libraltraders.android.databinding.ActivityFullScreenImageScrollBinding
import com.libraltraders.android.databinding.ItemProductSmallImageViewBinding
import com.webkul.mobikul.helpers.ImageHelper
import com.webkul.mobikul.models.product.ImageGalleryData
import java.util.*

class FullScreenImageScrollActivity : BaseActivity() {

    companion object {
        const val KEY_IMAGE_GALLERY_DATA = "imageGalleryData"
        const val KEY_SMALL_IMAGE_GALLERY_DATA = "smallImageGalleryData"
        const val KEY_LARGE_IMAGE_GALLERY_DATA = "largeImageGalleryData"
        const val KEY_CURRENT_ITEM_POSITION = "position"
    }

    lateinit var mContentViewBinding: ActivityFullScreenImageScrollBinding

    private var mImageList: ArrayList<ImageGalleryData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen_image_scroll)
        mImageList = intent.getParcelableArrayListExtra(KEY_IMAGE_GALLERY_DATA)
        startInitialization()
    }

    private fun startInitialization() {
        for (smallImageListIndex in mImageList!!.indices) {
            val eachSmallProductImage = mImageList!![smallImageListIndex]
            val itemProductSmallImageViewBinding = DataBindingUtil.bind<ItemProductSmallImageViewBinding>(layoutInflater.inflate(R.layout.item_product_small_image_view, mContentViewBinding.smallImageContainer, false))
            itemProductSmallImageViewBinding!!.data = eachSmallProductImage
            itemProductSmallImageViewBinding.root.tag = smallImageListIndex
            itemProductSmallImageViewBinding.executePendingBindings()
            mContentViewBinding.smallImageContainer.addView(itemProductSmallImageViewBinding.root)
        }

        mContentViewBinding.fullScreenProductViewPager.adapter = TouchImageAdapter()
        mContentViewBinding.fullScreenProductViewPager.currentItem = intent.getIntExtra(KEY_CURRENT_ITEM_POSITION, 0)
    }

    fun changeProductsLargeImage(v: View) {
        mContentViewBinding.fullScreenProductViewPager.currentItem = v.tag as Int
    }

    private inner class TouchImageAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return mImageList!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val touchImageView = TouchImageView(container.context, mContentViewBinding.smallImageBtnHsv)
            try {
                ImageHelper.load(touchImageView, mImageList!![position].largeImage, mImageList!![position].dominantColor)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            container.addView(touchImageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            return touchImageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }
}