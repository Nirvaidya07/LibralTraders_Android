package com.webkul.mobikul.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.CustomSpinnerCountryCodeBinding
import com.webkul.mobikul.models.CountryCodeListItem

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

class CountryCodeSpinnerAdapter(private val mContext: Context, private val mListData: ArrayList<CountryCodeListItem>) :  BaseAdapter() {


    private val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner_country_code, parent, false)
            holder = ViewHolder(view)
            view?.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val eachListData = mListData[position]
        holder.mBinding?.data = eachListData
        holder.mBinding?.executePendingBindings()

        return view
    }

    override fun getItem(position: Int): Any? {
        return mListData[position];
    }

    override fun getCount(): Int {
        return mListData.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: CustomSpinnerCountryCodeBinding? = DataBindingUtil.bind(itemView)
    }
}