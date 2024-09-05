package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemNavDrawerCategoryBinding
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.handlers.NavDrawerCategoriesRvHandler
import com.webkul.mobikul.models.homepage.Category

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

class NavDrawerCategoriesRvAdapter(private val mFragmentContext: NavDrawerStartFragment, private val mListData: ArrayList<Category>) : RecyclerView.Adapter<NavDrawerCategoriesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavDrawerCategoriesRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_nav_drawer_category, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NavDrawerCategoriesRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = NavDrawerCategoriesRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemNavDrawerCategoryBinding? = DataBindingUtil.bind(itemView)
    }
}