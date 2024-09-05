package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.activities.HomeActivity
import com.libraltraders.android.databinding.ItemOfferBannersRvBinding
import com.webkul.mobikul.handlers.HomePageBannerVpHandler
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

class OfferBannersRvAdapter(private val mContext: HomeActivity, private val mListData: ArrayList<BannerImage>) : RecyclerView.Adapter<OfferBannersRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OfferBannersRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_offer_banners_rv, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferBannersRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData.get(position)
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = HomePageBannerVpHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemOfferBannersRvBinding? = DataBindingUtil.bind(itemView)
    }
}