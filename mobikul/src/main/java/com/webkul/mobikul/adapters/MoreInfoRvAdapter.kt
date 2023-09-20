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
import com.webkul.mobikul.databinding.ItemProductAdditionalInfoBinding
import com.webkul.mobikul.models.product.AdditionalInformationData
import java.util.*

class MoreInfoRvAdapter(private val mContext: Context, private val mListData: ArrayList<AdditionalInformationData>) : RecyclerView.Adapter<MoreInfoRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MoreInfoRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_product_additional_info, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoreInfoRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemProductAdditionalInfoBinding? = DataBindingUtil.bind(itemView)
    }
}