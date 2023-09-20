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
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.databinding.ItemCheckoutAddressBinding
import com.webkul.mobikul.fragments.AddressListFragment
import com.webkul.mobikul.fragments.PaymentInfoFragment
import com.webkul.mobikul.fragments.ShippingInfoFragment
import com.webkul.mobikul.handlers.CheckoutShippingAddressRvHandler
import com.webkul.mobikul.models.checkout.BillingShippingAddress
import java.util.*

class CheckoutShippingAddressRvAdapter(private val mFragmentContext: AddressListFragment, private val mListData: ArrayList<BillingShippingAddress>) : RecyclerView.Adapter<CheckoutShippingAddressRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_checkout_address, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.position = position
        holder.mBinding?.data = eachListData

        val shippingInfoFragment = (mFragmentContext.context as CheckoutActivity).supportFragmentManager.findFragmentByTag(ShippingInfoFragment::class.java.simpleName)
        if (shippingInfoFragment != null && shippingInfoFragment.isVisible) {
            eachListData.isSelected = (eachListData.id == (shippingInfoFragment as ShippingInfoFragment).mContentViewBinding.data!!.selectedAddressData?.id)
            holder.mBinding?.handler = CheckoutShippingAddressRvHandler(shippingInfoFragment)
        }

        val paymentInfoFragment = (mFragmentContext.context as CheckoutActivity).supportFragmentManager.findFragmentByTag(PaymentInfoFragment::class.java.simpleName)
        if (paymentInfoFragment != null && paymentInfoFragment.isVisible) {
            eachListData.isSelected = (eachListData.id == (paymentInfoFragment as PaymentInfoFragment).mContentViewBinding.checkoutAddressData!!.selectedAddressData?.id)
            holder.mBinding?.handler = CheckoutShippingAddressRvHandler(paymentInfoFragment)
        }

        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemCheckoutAddressBinding? = DataBindingUtil.bind(itemView)
    }
}