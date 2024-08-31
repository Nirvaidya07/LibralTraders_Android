/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.customviews.MaterialSearchView
import com.libraltraders.android.databinding.ItemSearchViewCategoryBinding
import com.webkul.mobikul.handlers.SearchViewCategoryRvHandler
import com.webkul.mobikul.models.homepage.Category

class SearchViewCategoryRvAdapter(private val mMaterialSearchView: MaterialSearchView, private var mListData: java.util.ArrayList<Category>) : RecyclerView.Adapter<SearchViewCategoryRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): SearchViewCategoryRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mMaterialSearchView.mContext).inflate(R.layout.item_search_view_category, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewCategoryRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData.get(position)
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = SearchViewCategoryRvHandler(mMaterialSearchView)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemSearchViewCategoryBinding? = DataBindingUtil.bind(itemView)
    }
}