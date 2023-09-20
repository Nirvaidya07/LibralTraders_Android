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
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.customviews.MaterialSearchView
import com.webkul.mobikul.databinding.ItemRecentSearchesBinding
import com.webkul.mobikul.handlers.RecentSearchesHandler

class RecentSearchesAdapter(private val mMaterialSearchView: MaterialSearchView, private var mListData: ArrayList<String>) : RecyclerView.Adapter<RecentSearchesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecentSearchesAdapter.ViewHolder {
        val view = LayoutInflater.from(mMaterialSearchView.mContext).inflate(R.layout.item_recent_searches, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentSearchesAdapter.ViewHolder, position: Int) {
        val eachListData = mListData.get(position)
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = RecentSearchesHandler(mMaterialSearchView)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    fun updateData(tags: ArrayList<String>) {
        this.mListData = tags
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemRecentSearchesBinding? = DataBindingUtil.bind(itemView)
    }
}