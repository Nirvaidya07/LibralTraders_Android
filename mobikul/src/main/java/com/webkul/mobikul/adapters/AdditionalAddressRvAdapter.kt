package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.AddressBookActivity
import com.webkul.mobikul.databinding.ItemAdditionalAddressBinding
import com.webkul.mobikul.handlers.AddressBookActivityHandler
import com.webkul.mobikul.helpers.BindingAdapterUtils
import com.webkul.mobikul.models.user.AddressData

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

class AdditionalAddressRvAdapter(private val mContext: AddressBookActivity, private val mListData: ArrayList<AddressData>) : RecyclerView.Adapter<AdditionalAddressRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_additional_address, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = AddressBookActivityHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.mBinding?.editTv?.let { BindingAdapterUtils.setAppThem(it,12) }

    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemAdditionalAddressBinding? = DataBindingUtil.bind(itemView)
    }
}