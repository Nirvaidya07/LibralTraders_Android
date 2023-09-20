/*
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

package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.ProductDetailsActivity
import com.webkul.mobikul.databinding.ItemAttributeSwatchBinding
import com.webkul.mobikul.handlers.CatalogAttributesSwatchHandler
import com.webkul.mobikul.models.product.SwatchData
import java.util.*

class ProductAttributesSwatchRvAdapter(private val mContext: ProductDetailsActivity, private val mRecyclerViewTag: Int, private val mUpdateProductPreviewImage: Boolean, private val mListData: ArrayList<SwatchData>) : RecyclerView.Adapter<ProductAttributesSwatchRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_attribute_swatch, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        eachListData.position = position
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = CatalogAttributesSwatchHandler(mContext, mRecyclerViewTag, mUpdateProductPreviewImage, mListData)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    fun getSwatchValuesData(): ArrayList<SwatchData> {
        return mListData
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemAttributeSwatchBinding? = DataBindingUtil.bind(itemView)
    }
}