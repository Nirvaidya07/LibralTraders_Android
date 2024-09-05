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
import com.libraltraders.android.databinding.ItemOrderShipmentsBinding
import com.webkul.mobikul.fragments.ShipmentsFragment
import com.webkul.mobikul.handlers.OrderShipmentsRvHandler
import com.webkul.mobikul.models.user.ShipmentItem

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

class OrderShipmentsRvAdapter(private val mFragmentContext: ShipmentsFragment, private val mListData: ArrayList<ShipmentItem>) : RecyclerView.Adapter<OrderShipmentsRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_order_shipments, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = OrderShipmentsRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemOrderShipmentsBinding? = DataBindingUtil.bind(itemView)
    }
}