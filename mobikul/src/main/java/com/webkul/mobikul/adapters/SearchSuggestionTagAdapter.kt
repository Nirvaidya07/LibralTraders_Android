package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.customviews.MaterialSearchView
import com.libraltraders.android.databinding.ItemSearchSuggestionTagsBinding
import com.webkul.mobikul.handlers.SearchSuggestionTagHandler
import com.webkul.mobikul.models.extra.SuggestionTagData

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
class SearchSuggestionTagAdapter(private val mMaterialSearchView: MaterialSearchView, private var mListData: ArrayList<SuggestionTagData>) : RecyclerView.Adapter<SearchSuggestionTagAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): SearchSuggestionTagAdapter.ViewHolder {
        val view = LayoutInflater.from(mMaterialSearchView.mContext).inflate(R.layout.item_search_suggestion_tags, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchSuggestionTagAdapter.ViewHolder, position: Int) {
        val eachListData = mListData.get(position)
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = SearchSuggestionTagHandler(mMaterialSearchView)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    fun updateData(tags: ArrayList<SuggestionTagData>) {
        this.mListData = tags
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemSearchSuggestionTagsBinding? = DataBindingUtil.bind(itemView)
    }
}