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
import com.libraltraders.android.databinding.ItemOrderInvoicesBinding
import com.webkul.mobikul.fragments.InvoicesFragment
import com.webkul.mobikul.handlers.OrderInvoicesRvHandler
import com.webkul.mobikul.models.user.InvoiceItem

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

class OrderInvoicesRvAdapter(private val mFragmentContext: InvoicesFragment, private val mListData: ArrayList<InvoiceItem>) : RecyclerView.Adapter<OrderInvoicesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderInvoicesRvAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_order_invoices, parent, false))
    }

    override fun onBindViewHolder(holder: OrderInvoicesRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = OrderInvoicesRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemOrderInvoicesBinding? = DataBindingUtil.bind(itemView)
    }
}