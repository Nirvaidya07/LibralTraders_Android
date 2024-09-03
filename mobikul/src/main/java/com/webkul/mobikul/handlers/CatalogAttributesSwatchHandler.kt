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

package com.webkul.mobikul.handlers

import com.webkul.mobikul.activities.ProductDetailsActivity
import com.webkul.mobikul.models.product.SwatchData
import java.util.*


class CatalogAttributesSwatchHandler(val mContext: ProductDetailsActivity, val mRecyclerViewTag: Int, val mUpdateProductPreviewImage: Boolean, val mCatalogAttributeSwatchValuesDatas: ArrayList<SwatchData>) {

    fun onAttributeSelected(position: Int) {
        setSelectedAttribute(position)

        if (mRecyclerViewTag + 1 < mContext.mContentViewBinding.data!!.configurableData.attributes?.size?:0) {
            mContext.initializeConfigurableAttributeOption(mRecyclerViewTag + 1)
        }
        mContext.updatePrice()
    }

    private fun setSelectedAttribute(position: Int) {
        for (noOfSwatchValue in mCatalogAttributeSwatchValuesDatas.indices) {
            mCatalogAttributeSwatchValuesDatas[noOfSwatchValue].isSelected = noOfSwatchValue == position
        }
    }
}