package com.webkul.mobikul.customviews

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout

class CustomTabLayout(
        context: Context,
        attrs: AttributeSet?
) : TabLayout(context, attrs) {

    override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int
    ) {

        val equalTabWidth = (MeasureSpec.getSize(widthMeasureSpec) / tabCount.toFloat()).toInt()

        for (index in 0..tabCount) {
            val tab = getTabAt(index)
            val tabMeasuredWidth = tab?.view?.measuredWidth ?: equalTabWidth

            if (tabMeasuredWidth < equalTabWidth) {
                tab?.view?.minimumWidth = equalTabWidth
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}