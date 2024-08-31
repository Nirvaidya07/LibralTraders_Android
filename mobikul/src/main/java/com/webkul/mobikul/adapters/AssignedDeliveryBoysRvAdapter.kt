package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemAssignedDeliveryBoysBinding
import com.webkul.mobikul.fragments.ItemOrderedFragment
import com.webkul.mobikul.handlers.ItemOrderedFragmentHandler
import com.webkul.mobikul.models.user.DeliveryBoys

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

class AssignedDeliveryBoysRvAdapter(private val mContext: ItemOrderedFragment, private val mListData: ArrayList<DeliveryBoys>,private val incrementId:String) : RecyclerView.Adapter<AssignedDeliveryBoysRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext.context).inflate(R.layout.item_assigned_delivery_boys, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = ItemOrderedFragmentHandler(mContext,incrementId)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemAssignedDeliveryBoysBinding? = DataBindingUtil.bind(itemView)
    }
}