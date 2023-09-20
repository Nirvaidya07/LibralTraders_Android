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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ItemProductCreateReviewRatingBinding
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.product.RatingFormData
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ProductCreateReviewRatingDataRvAdapter(private val mContext: Context, private val mListData: ArrayList<RatingFormData>) : RecyclerView.Adapter<ProductCreateReviewRatingDataRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_product_create_review_rating, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemProductCreateReviewRatingBinding? = DataBindingUtil.bind(itemView)
    }

    fun getRatingData(): JSONObject {
        val ratingJsonObject = JSONObject()
        for (ratingId in mListData.indices) {
            try {
                ratingJsonObject.put(mListData[ratingId].id.toString(), mListData[ratingId].getSelectedRatingValue())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return ratingJsonObject
    }

    fun isRatingFilled(): Boolean {
        for (ratingIdx in mListData.indices) {
            if (mListData[ratingIdx].ratingValue == 0f) {
                ToastHelper.showToast(mContext, mContext.getString(R.string.alert_please_select_one_of_each_rating))
                return false
            }
        }
        return true
    }
}