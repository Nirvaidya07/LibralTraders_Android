package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_PARENT_CATEGORY
import com.libraltraders.android.databinding.ItemCategoryTitleBinding
import com.webkul.mobikul.handlers.SubCategoriesRvHandler
import com.webkul.mobikul.helpers.Animations
import com.webkul.mobikul.models.CategoriesData


class CategoryTitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val mBinding: ItemCategoryTitleBinding? = DataBindingUtil.bind(itemView)

    fun bind(categoriesData: CategoriesData, position: Int, mListData: List<CategoriesData>, setValue: (Int) -> Unit) {
        mBinding?.position = position
        mBinding?.data = categoriesData.category
        mBinding?.parentCategoryId = categoriesData.parentCategoryId
        mBinding?.handler = SubCategoriesRvHandler(view.context)
        mBinding?.viewMoreBtn?.rotation=0f
        mBinding?.shopByProduct?.setOnClickListener {

            mBinding.data!!.isParentExpanded = !mBinding.data!!.isParentExpanded
            Animations.toggleArrow(mBinding.viewMoreBtn, mBinding.data!!.isParentExpanded)
            mListData.forEachIndexed { index, category ->
                if (category.parentCategoryId == categoriesData.parentCategoryId && category.viewType != VIEW_TYPE_PARENT_CATEGORY) {
                    category.category?.isExpanded = !category.category?.isExpanded!!
                    setValue(index)
                }
            }
        }

        mBinding?.executePendingBindings()
        mBinding?.invalidateAll()

    }

    companion object {
        fun create(parent: ViewGroup): CategoryTitleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_title, parent, false)
            return CategoryTitleViewHolder(view)
        }
    }
}

