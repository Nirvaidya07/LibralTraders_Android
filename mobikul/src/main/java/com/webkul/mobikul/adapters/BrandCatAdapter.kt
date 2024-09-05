package com.webkul.mobikul.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemBrandCatBinding
import com.webkul.mobikul.handlers.BrandsRvHandler
import com.webkul.mobikul.models.catalog.BrandCategoryResponseModel

class BrandCatAdapter(private val mContext: Activity, private val items: MutableList<BrandCategoryResponseModel>?) : RecyclerView.Adapter<BrandCatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_brand_cat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = items?.get(position)
        holder.mBinding?.item = category
        holder.mBinding?.handler = BrandsRvHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemBrandCatBinding? = DataBindingUtil.bind(itemView)
    }
}
