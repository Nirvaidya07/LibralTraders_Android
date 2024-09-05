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
import com.libraltraders.android.databinding.ItemOrderItemsForReviewBinding
import com.webkul.mobikul.fragments.OrderItemListBottomSheetFragment
import com.webkul.mobikul.handlers.OrderItemsForReviewRvHandler
import com.webkul.mobikul.models.user.OrderItem

class OrderItemsForReviewRvAdapter(private val mFragmentContext: OrderItemListBottomSheetFragment, private val mListData: ArrayList<OrderItem>) : RecyclerView.Adapter<OrderItemsForReviewRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_order_items_for_review, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = OrderItemsForReviewRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemOrderItemsForReviewBinding? = DataBindingUtil.bind(itemView)
    }
}