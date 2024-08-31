package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemSubCategoryBinding
import com.webkul.mobikul.handlers.SubCategoriesRvHandler
import com.webkul.mobikul.models.homepage.Category


class CategoriesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val mBinding: ItemSubCategoryBinding? = DataBindingUtil.bind(itemView)
    fun bind(category: Category, position: Int, parentCategory: String?,recyclerView:RecyclerView?) {
        if (!category.isExpanded) {
        //    view.visibility = View.GONE
            mBinding?.mainContainer?.layoutParams = RecyclerView.LayoutParams(0, 0)


        } else if (view.visibility == View.GONE) {
       //     view.visibility = View.VISIBLE
            mBinding?.mainContainer?.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            recyclerView?.smoothScrollToPosition(adapterPosition)
        }
//        else {
            mBinding?.position = position
            mBinding?.data = category
            mBinding?.parentCategory = parentCategory
            mBinding?.handler = SubCategoriesRvHandler(view.context)
//        }
        mBinding?.executePendingBindings()
        mBinding?.invalidateAll()
    }


    companion object {
        fun create(parent: ViewGroup): CategoriesViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category, parent, false)
            return CategoriesViewHolder(view)
        }
    }

}

