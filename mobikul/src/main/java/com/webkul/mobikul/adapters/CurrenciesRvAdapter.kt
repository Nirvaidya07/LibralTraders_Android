package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ItemNavDrawerCurrenciesBinding
import com.webkul.mobikul.fragments.CurrencyBottomSheetFragment
import com.webkul.mobikul.handlers.CurrenciesRvHandler
import com.webkul.mobikul.models.homepage.Currency

/**
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

class CurrenciesRvAdapter(private val mFragmentContext: CurrencyBottomSheetFragment, private val mListData: ArrayList<Currency>) : RecyclerView.Adapter<CurrenciesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_nav_drawer_currencies, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = CurrenciesRvHandler()
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemNavDrawerCurrenciesBinding? = DataBindingUtil.bind(itemView)
    }
}