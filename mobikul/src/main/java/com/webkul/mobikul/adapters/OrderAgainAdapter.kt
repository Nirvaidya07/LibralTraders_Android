package com.webkul.mobikul.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemOrderAgainBinding
import com.webkul.mobikul.activities.MyOrdersActivity
import com.webkul.mobikul.models.product.ProductData
import com.webkul.mobikul.handlers.OrderAgainHandler

class OrderAgainAdapter(private val mContext: MyOrdersActivity, private val mListData: ArrayList<ProductData>) : RecyclerView.Adapter<OrderAgainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_order_again, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = OrderAgainHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemOrderAgainBinding? = DataBindingUtil.bind(itemView)
    }
}