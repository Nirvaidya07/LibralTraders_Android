package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.databinding.ItemNavDrawerStoresBinding
import com.webkul.mobikul.fragments.LanguagesBottomSheetFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.LocaleUtils
import com.webkul.mobikul.models.homepage.StoreData

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

class StoresRvAdapter(private val mFragmentContext: LanguagesBottomSheetFragment, private val mListData: ArrayList<StoreData>) : RecyclerView.Adapter<StoresRvAdapter.ViewHolder>() {

    private var currentPosition = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StoresRvAdapter.ViewHolder {
        val view = LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_nav_drawer_stores, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoresRvAdapter.ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        if (eachListData.stores?.size ?: 0 > 0) {
            onBindChildViewHolder(holder, position, eachListData)
        } else {
            holder.mBinding?.storeLabel?.setOnClickListener {
                if (AppSharedPref.getStoreId(mFragmentContext.requireContext()) != eachListData.id) {
                    (mFragmentContext.context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
                    currentPosition = holder.adapterPosition
                    eachListData.id?.let { it1 -> AppSharedPref.setStoreId(mFragmentContext.requireContext(), it1) }
                    eachListData.code?.let { it1 -> AppSharedPref.setStoreCode(mFragmentContext.requireContext(), it1) }
                    eachListData.name?.let { it1 -> AppSharedPref.setStoreLabel(mFragmentContext.requireContext(), it1) }
                    AppSharedPref.setCurrencyCode(mFragmentContext.requireContext(),"")
                    LocaleUtils.changeLanguage(mFragmentContext.requireContext())
                    notifyDataSetChanged()
                }
            }
        }
        holder.mBinding?.executePendingBindings()
    }

    private fun onBindChildViewHolder(holder: StoresRvAdapter.ViewHolder, position: Int, eachListData: StoreData) {
        holder.mBinding?.navDrawerStoreViewRv?.adapter = eachListData.stores?.let { StoresRvAdapter(mFragmentContext, it) }
        holder.mBinding?.navDrawerStoreViewRv?.isNestedScrollingEnabled = false
        if (currentPosition == position) {
            if (eachListData.isExpanded) {
                val slideDown = AnimationUtils.loadAnimation(mFragmentContext.context, R.anim.slide_up)
                eachListData.isExpanded = false
                holder.mBinding?.storeViewIndicator?.rotation = 0f
                holder.mBinding?.navDrawerStoreViewRv?.startAnimation(slideDown)
            } else {
                val slideDown = AnimationUtils.loadAnimation(mFragmentContext.context, R.anim.slide_down)
                eachListData.isExpanded = true
                holder.mBinding?.storeViewIndicator?.rotation = 90f
                holder.mBinding?.navDrawerStoreViewRv?.startAnimation(slideDown)
            }
        }

        holder.mBinding?.storeLabel?.setOnClickListener {
            currentPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemNavDrawerStoresBinding? = DataBindingUtil.bind(itemView)
    }
}