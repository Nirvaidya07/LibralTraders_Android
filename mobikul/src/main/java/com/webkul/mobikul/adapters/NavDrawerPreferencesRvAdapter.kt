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
import com.webkul.mobikul.databinding.ItemNavDrawerPreferencesBinding
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.handlers.PreferencesRvHandler
import com.webkul.mobikul.models.user.PreferencesRvModel

class NavDrawerPreferencesRvAdapter(private val mFragmentContext: NavDrawerStartFragment, private val mListData: ArrayList<PreferencesRvModel>) : RecyclerView.Adapter<NavDrawerPreferencesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(mFragmentContext.context).inflate(R.layout.item_nav_drawer_preferences, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = PreferencesRvHandler(mFragmentContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemNavDrawerPreferencesBinding? = DataBindingUtil.bind(itemView)
    }
}