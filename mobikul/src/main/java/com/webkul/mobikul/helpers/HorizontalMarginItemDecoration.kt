package com.webkul.mobikul.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalMarginItemDecoration(private val spaceHeight: Int, private val noSpace: Boolean = false) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                if (noSpace) {
                    if (AppSharedPref.getStoreCode(view.context) == "ar") {
                        left = spaceHeight
                        right = spaceHeight
                    } else {
                        left = spaceHeight
                        right = spaceHeight
                    }
                }else{
                    if (AppSharedPref.getStoreCode(view.context) == "ar") {
                        left = spaceHeight
                        right = spaceHeight + 20
                    } else {
                        left = spaceHeight + 20
                        right = spaceHeight
                    }
                }
            } else if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                if (AppSharedPref.getStoreCode(view.context) == "ar") {
                    left = spaceHeight + 20
                    right = spaceHeight
                } else {
                    left = spaceHeight
                    right = spaceHeight + 20
                }
            } else {
                left = spaceHeight
                right = spaceHeight
            }
        }
    }
}