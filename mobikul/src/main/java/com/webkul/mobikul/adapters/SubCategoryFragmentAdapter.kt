package com.webkul.mobikul.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.models.CategoriesData

class SubCategoryFragmentAdapter(private var mListData: List<CategoriesData>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mRecyclerView: RecyclerView? = null

    companion object {
        const val VIEW_TYPE_BANNER = 10
        const val VIEW_TYPE_PRODUCTS = 11
        const val VIEW_TYPE_PARENT_CATEGORY = 12
        const val VIEW_TYPE_CATEGORY = 13
        const val VIEW_TYPE_HOT_SELLER = 14
        const val VIEW_TYPE_CATEGORY_DIVIDER = 15
//        const val VIEW_TYPE_CHILD_CATEGORY_BANNER = 16
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        (recyclerView.layoutManager as GridLayoutManager).spanSizeLookup = getSpanSizeLookup()
        (recyclerView.layoutManager as GridLayoutManager).isUsingSpansToEstimateScrollbarDimensions=true
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null
    }

    private fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mListData[position].viewType) {
                    VIEW_TYPE_CATEGORY -> 1
                    else -> 3
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (mListData[position].viewType) {
//            VIEW_TYPE_CHILD_CATEGORY_BANNER -> {
//                mListData[position].bannerImage?.let { (holder as ChildBannerViewHolder).bind(it) }
//            }
            VIEW_TYPE_BANNER -> {
                mListData[position].bannerImage?.let { (holder as BannerViewHolder).bind(it) }
            }
            VIEW_TYPE_PRODUCTS->{
                (holder as SubCategoryProductsViewHolder).bind(mListData[position],VIEW_TYPE_PRODUCTS)
            }
            VIEW_TYPE_HOT_SELLER -> {
                (holder as SubCategoryProductsViewHolder).bind(mListData[position],VIEW_TYPE_HOT_SELLER)
            }
            VIEW_TYPE_CATEGORY -> {
                mListData[position].category?.let { (holder as CategoriesViewHolder).bind(it, position, mListData[position].parentCategoryId, recyclerView = mRecyclerView) }
            }
            VIEW_TYPE_PARENT_CATEGORY -> {
                (holder as CategoryTitleViewHolder).bind(mListData[position], position, mListData) {
                    notifyItemChanged(it)
                }
            }
            VIEW_TYPE_CATEGORY_DIVIDER -> {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_BANNER -> {
                BannerViewHolder.create(parent)
            }
//            VIEW_TYPE_CHILD_CATEGORY_BANNER -> {
//                ChildBannerViewHolder.create(parent)
//            }
            VIEW_TYPE_PRODUCTS -> {
                SubCategoryProductsViewHolder.create(parent)
            }

            VIEW_TYPE_HOT_SELLER -> {
                SubCategoryProductsViewHolder.create(parent)
            }

            VIEW_TYPE_CATEGORY -> {
                CategoriesViewHolder.create(parent)
            }

            VIEW_TYPE_CATEGORY_DIVIDER -> {
                CategoryDividerViewHolder.create(parent)
            }

            else -> {
                CategoryTitleViewHolder.create(parent)
            }
        }
    }

    override fun getItemViewType(position: Int) = mListData[position].viewType

    override fun getItemCount(): Int = mListData.size

    fun updateItem(list: List<CategoriesData>) {
//        val diffCallback = RatingDiffCallback(mListData, list)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
        (mListData as ArrayList).clear()
        (mListData as ArrayList).addAll(list)
//        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }


}