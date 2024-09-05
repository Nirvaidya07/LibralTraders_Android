package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemNavDrawerExtrasBinding
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.handlers.NavDrawerExtrasRvHandler
import com.webkul.mobikul.models.homepage.NavDrawerExtrasRvModel

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

class NavDrawerExtrasRvAdapter(private val mFragmentContext: NavDrawerStartFragment, private val mListData: ArrayList<NavDrawerExtrasRvModel>) : RecyclerView.Adapter<NavDrawerExtrasRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavDrawerExtrasRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_nav_drawer_extras, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NavDrawerExtrasRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = NavDrawerExtrasRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemNavDrawerExtrasBinding? = DataBindingUtil.bind(itemView)
    }
}