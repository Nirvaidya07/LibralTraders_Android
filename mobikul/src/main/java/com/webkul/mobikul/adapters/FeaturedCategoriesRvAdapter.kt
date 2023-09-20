package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.databinding.ItemFeaturedCategoryBinding
import com.webkul.mobikul.databinding.ItemFeaturedCategoryGridBinding
import com.webkul.mobikul.handlers.FeaturedCategoriesRvHandler
import com.webkul.mobikul.helpers.ConstantsHelper
import com.webkul.mobikul.models.homepage.FeaturedCategory

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
class FeaturedCategoriesRvAdapter(private val mContext: HomeActivity, private val mListData: ArrayList<FeaturedCategory>, val layoutView: Int) : RecyclerView.Adapter<FeaturedCategoriesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (layoutView == ConstantsHelper.VIEW_TYPE_LIST) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_featured_category, parent, false))
        } else {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_featured_category_grid, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]

        if (layoutView == ConstantsHelper.VIEW_TYPE_LIST) {
            (holder.mBinding as ItemFeaturedCategoryBinding).data = eachListData
            holder.mBinding.handler = FeaturedCategoriesRvHandler(mContext)
        } else {
            (holder.mBinding as ItemFeaturedCategoryGridBinding).data = eachListData
            holder.mBinding.handler = FeaturedCategoriesRvHandler(mContext)
        }
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ViewDataBinding? = DataBindingUtil.bind(itemView)
    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (mContext.mContentViewBinding.featuredCategoriesRv.layoutManager is GridLayoutManager) {
//            ConstantsHelper.VIEW_TYPE_GRID
//        } else {
//            ConstantsHelper.VIEW_TYPE_LIST
//        }
//    }
}