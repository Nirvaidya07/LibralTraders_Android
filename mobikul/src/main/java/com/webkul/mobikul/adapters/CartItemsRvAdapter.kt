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

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemCartItemListBinding
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.handlers.CartItemsRvHandler
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.catalog.CartItem
import io.github.inflationx.calligraphy3.TypefaceUtils


class CartItemsRvAdapter(private val mFragmentContext: CartBottomSheetFragment, private val mListData: java.util.ArrayList<CartItem>, private val mShowThreshold: Boolean) : RecyclerView.Adapter<CartItemsRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_cart_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.position = position
        holder.mBinding?.data = eachListData

        if (!mFragmentContext.mContentViewBinding.data!!.containsDownloadableProducts)
            mFragmentContext.mContentViewBinding.data!!.containsDownloadableProducts = eachListData.typeId == "downloadable"

        // Setting selected options
        if (eachListData.optionItems?.size ?:0> 0) {
            holder.mBinding?.optionTableLayout?.removeAllViews()
            for (optionIterator in 0 until (eachListData.optionItems?.size?:0)) {

                val optionItem = eachListData.optionItems?.get(optionIterator)

                val tableRow = TableRow(mFragmentContext.context)
                tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                tableRow.gravity = Gravity.CENTER_VERTICAL

                val labelTv = TextView(mFragmentContext.context)
                labelTv.layoutParams = TableRow.LayoutParams((Utils.screenWidth / 4), TableRow.LayoutParams.WRAP_CONTENT)
                labelTv.setPadding(0, 12, 0, 0)
                labelTv.textSize = 12f
                labelTv.setTextColor(ContextCompat.getColor(mFragmentContext.requireContext(), R.color.text_color_secondary))
                labelTv.typeface = TypefaceUtils.load(mFragmentContext.requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
                labelTv.text = optionItem?.label + mFragmentContext.getString(R.string.colon)
                tableRow.addView(labelTv)

                val valueTv = TextView(mFragmentContext.context)
                valueTv.layoutParams = TableRow.LayoutParams((Utils.screenWidth / 3.5).toInt(), TableRow.LayoutParams.WRAP_CONTENT)
                valueTv.setPadding(10, 12, 10, 0)
                valueTv.textSize = 12f
                valueTv.setTextColor(ContextCompat.getColor(mFragmentContext.requireContext(), R.color.text_color_primary))
                valueTv.typeface = TypefaceUtils.load(mFragmentContext.requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
                var value = optionItem?.value?.get(0)
                for (noOfValues in 1 until (optionItem?.value?.size?:0)){
                    value = value + "\n" + (optionItem?.value?.get(noOfValues) ?: "")
                }
                valueTv.text = value
                tableRow.addView(valueTv)
                holder.mBinding?.optionTableLayout?.addView(tableRow)
            }
        }

        // Setting messages
        if (eachListData.messages.isNotEmpty()) {
            holder.mBinding?.itemMessageLl?.removeAllViews()
            eachListData.messages.forEach { eachMessage ->
                val errorTv = TextView(mFragmentContext.context)
                errorTv.text = eachMessage.text
                errorTv.textSize = 12f
                errorTv.setPadding(10, 5, 10, 5)
                if (eachMessage.type == "error") {
                    errorTv.setTextColor(ContextCompat.getColor(mFragmentContext.requireContext(), android.R.color.holo_red_light))
                } else {
                    errorTv.setTextColor(ContextCompat.getColor(mFragmentContext.requireContext(), android.R.color.holo_orange_light))
                }
                holder.mBinding?.itemMessageLl?.addView(errorTv)
            }
        }

        holder.mBinding?.handler = CartItemsRvHandler(mFragmentContext)
        holder.mBinding?.showThreshold = mShowThreshold
        mListData[position].qty = eachListData.qty
        holder.mBinding?.executePendingBindings()
    }

    fun isCartItemQtyChanged(): Boolean {
        for (eachCartItem in mListData) {
            if (eachCartItem.isCartItemQtyChanged())
                return true
        }
        return false
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    fun getItemList(): ArrayList<CartItem> {
        return mListData
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemCartItemListBinding? = DataBindingUtil.bind(itemView)
    }
}