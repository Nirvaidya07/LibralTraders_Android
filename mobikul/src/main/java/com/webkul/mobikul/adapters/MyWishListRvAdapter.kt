package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.MyWishListActivity
import com.webkul.mobikul.databinding.ItemMyWishListBinding
import com.webkul.mobikul.handlers.MyWishListRvHandler
import com.webkul.mobikul.models.user.WishListItem

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

class MyWishListRvAdapter(private val mContext: MyWishListActivity, private val mListData: ArrayList<WishListItem>) : RecyclerView.Adapter<MyWishListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_my_wish_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        holder.mBinding?.position = position
        holder.mBinding?.data = eachListData
        holder.mBinding?.handler = MyWishListRvHandler(mContext)
        holder.mBinding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    fun getItemList(): ArrayList<WishListItem> {
        return mListData
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ItemMyWishListBinding? = DataBindingUtil.bind(itemView)
    }
}