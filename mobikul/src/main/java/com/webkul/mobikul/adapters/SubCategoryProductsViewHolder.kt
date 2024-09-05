package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.ItemSubCategoryProductsBinding
import com.webkul.mobikul.handlers.SubCategoryActivityHandler
import com.webkul.mobikul.helpers.HorizontalMarginItemDecoration
import com.webkul.mobikul.models.CategoriesData
import com.webkul.mobikul.models.product.ProductTileData


class SubCategoryProductsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val mBinding: ItemSubCategoryProductsBinding? = DataBindingUtil.bind(itemView)
    fun bind(categoriesData: CategoriesData,type:Int) {
        if(type== SubCategoryFragmentAdapter.VIEW_TYPE_HOT_SELLER){
            mBinding?.viewAllCategoryBtn?.visibility=View.GONE
        }else{
            mBinding?.viewAllCategoryBtn?.visibility=View.VISIBLE
        }
        if (mBinding?.productsCarouselRv?.adapter == null)
            mBinding?.productsCarouselRv?.addItemDecoration(HorizontalMarginItemDecoration(view.context.resources.getDimension(R.dimen.spacing_tiny).toInt(),noSpace = true))

        mBinding?.data=categoriesData
        mBinding?.handler = SubCategoryActivityHandler(view.context)
        mBinding?.productsCarouselRv?.adapter = ProductCarouselHorizontalRvAdapter(view.context as BaseActivity, categoriesData.productList as ArrayList<ProductTileData>)
        mBinding?.productsCarouselRv?.isNestedScrollingEnabled = false

        mBinding?.executePendingBindings()
        mBinding?.invalidateAll()
    }



    companion object {
        fun create(parent: ViewGroup): SubCategoryProductsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category_products, parent, false)
            return SubCategoryProductsViewHolder(view)
        }
    }

}

