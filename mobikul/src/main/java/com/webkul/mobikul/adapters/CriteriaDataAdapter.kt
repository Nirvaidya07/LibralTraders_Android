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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemCriteriaDataBinding

class CriteriaDataAdapter(private val mContext: Context, private val mListData: ArrayList<String>) : RecyclerView.Adapter<CriteriaDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CriteriaDataAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_criteria_data, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CriteriaDataAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemCriteriaDataBinding? = DataBindingUtil.bind(itemView)
    }
}