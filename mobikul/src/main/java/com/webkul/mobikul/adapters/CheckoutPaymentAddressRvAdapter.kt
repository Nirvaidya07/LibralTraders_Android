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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemCheckoutPaymentAddressBinding
import com.webkul.mobikul.fragments.PaymentInfoFragment
import com.webkul.mobikul.handlers.CheckoutPaymentAddressRvHandler
import com.webkul.mobikul.models.checkout.BillingShippingAddress

class CheckoutPaymentAddressRvAdapter(private val mContext: PaymentInfoFragment, private val mListData: java.util.ArrayList<BillingShippingAddress>) : RecyclerView.Adapter<CheckoutPaymentAddressRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutPaymentAddressRvAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext.context).inflate(R.layout.item_checkout_payment_address, parent, false))
    }

    override fun onBindViewHolder(holder: CheckoutPaymentAddressRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.position = position
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = CheckoutPaymentAddressRvHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemCheckoutPaymentAddressBinding? = DataBindingUtil.bind(itemView)
    }
}