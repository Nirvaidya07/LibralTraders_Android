package com.webkul.mobikul.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemMyOrdersBinding
import com.webkul.mobikul.handlers.MyOrdersRvHandler
import com.webkul.mobikul.models.user.OrderList

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

class MyOrdersRvAdapter(private val mContext: Context, private val mListData: ArrayList<OrderList>) : RecyclerView.Adapter<MyOrdersRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_my_orders, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = MyOrdersRvHandler(mContext, eachListData.orderId)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemMyOrdersBinding? = DataBindingUtil.bind(itemView)
    }
}