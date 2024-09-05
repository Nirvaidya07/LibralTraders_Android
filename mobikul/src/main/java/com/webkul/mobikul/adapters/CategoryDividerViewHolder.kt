package com.webkul.mobikul.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R


class CategoryDividerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


    companion object {
        fun create(parent: ViewGroup): CategoryDividerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_divider, parent, false)
            return CategoryDividerViewHolder(view)
        }
    }
}

