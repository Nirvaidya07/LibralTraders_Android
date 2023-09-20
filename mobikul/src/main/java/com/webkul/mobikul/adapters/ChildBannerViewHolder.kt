package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.databinding.ItemSubCategoryBannerBinding
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.models.homepage.BannerImage
import java.util.*


class ChildBannerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val mBinding: ItemSubCategoryBannerBinding? = DataBindingUtil.bind(itemView)
    fun bind(bannerImage: List<BannerImage>) {

        if (mBinding?.categoryBannerViewPager?.adapter == null) {
            val imageCarouselSwitcherTimer = Timer()
            imageCarouselSwitcherTimer.scheduleAtFixedRate(mBinding?.categoryBannerViewPager?.let { BannerSwitchTimerTask(it, bannerImage.size) }, 1000.toLong(), ApplicationConstants.DEFAULT_TIME_TO_SWITCH_BANNER_IN_MILLIS.toLong())
        }
        mBinding?.bannerSize=bannerImage.size
        mBinding?.categoryBannerViewPager?.adapter = CategoryBannerVpAdapter(view.context as BaseActivity, bannerImage as ArrayList<BannerImage>)
        mBinding?.categoryBannerViewPager?.offscreenPageLimit = 2
        mBinding?.executePendingBindings()
    }


    companion object {
        fun create(parent: ViewGroup): ChildBannerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category_banner, parent, false)
            return ChildBannerViewHolder(view)
        }
    }

    private inner class BannerSwitchTimerTask internal constructor(private val mViewPager: ViewPager, private val mBannerSize: Int) : TimerTask() {

        internal var firstTime = true

        override fun run() {
            try {
                (mViewPager.context as BaseActivity).runOnUiThread {
                    if (mViewPager.currentItem == mBannerSize - 1) {
                        mViewPager.currentItem = 0
                    } else {
                        if (firstTime) {
                            mViewPager.currentItem = mViewPager.currentItem
                            firstTime = false
                        } else {
                            mViewPager.currentItem = mViewPager.currentItem + 1
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

