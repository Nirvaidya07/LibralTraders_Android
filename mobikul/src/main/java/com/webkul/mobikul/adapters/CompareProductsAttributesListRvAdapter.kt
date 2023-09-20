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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ItemCompareProductsAttributeValueBinding
import java.util.*

class CompareProductsAttributesListRvAdapter(private val mContext: Context, private val mListData: ArrayList<String>) : RecyclerView.Adapter<CompareProductsAttributesListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CompareProductsAttributesListRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_compare_products_attribute_value, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompareProductsAttributesListRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData.replace("<style([\\s\\S]+?)</style>", "")
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemCompareProductsAttributeValueBinding? = DataBindingUtil.bind(itemView)
    }
}