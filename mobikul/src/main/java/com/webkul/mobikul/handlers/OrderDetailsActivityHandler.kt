/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import com.webkul.mobikul.R
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.fragments.InvoicesFragment
import com.webkul.mobikul.fragments.ItemOrderedFragment
import com.webkul.mobikul.fragments.RefundsFragment
import com.webkul.mobikul.fragments.ShipmentsFragment

class OrderDetailsActivityHandler(private val mContext: OrderDetailsActivity) {

    fun onClickItemOrdered() {
        mContext.mNavigationIconClickListener.onMenuItemClick(mContext.mMenuItemMoreOptions)
        mContext.supportActionBar?.title = mContext.getString(R.string.item_ordered)
        val fragmentTransaction = mContext.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, ItemOrderedFragment.newInstance(mContext.mIncrementId, mContext.mContentViewBinding.data!!), ItemOrderedFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickInvoices() {
        mContext.mNavigationIconClickListener.onMenuItemClick(mContext.mMenuItemMoreOptions)
        mContext.supportActionBar?.title = mContext.getString(R.string.invoices)
        val fragmentTransaction = mContext.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, InvoicesFragment.newInstance(mContext.mIncrementId, mContext.mContentViewBinding.data!!), InvoicesFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickShipments() {
        mContext.mNavigationIconClickListener.onMenuItemClick(mContext.mMenuItemMoreOptions)
        mContext.supportActionBar?.title = mContext.getString(R.string.order_shipment)
        val fragmentTransaction = mContext.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, ShipmentsFragment.newInstance(mContext.mIncrementId, mContext.mContentViewBinding.data!!), ShipmentsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickRefunds() {
        mContext.mNavigationIconClickListener.onMenuItemClick(mContext.mMenuItemMoreOptions)
        mContext.supportActionBar?.title = mContext.getString(R.string.refunds)
        val fragmentTransaction = mContext.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, RefundsFragment.newInstance(mContext.mIncrementId, mContext.mContentViewBinding.data!!), RefundsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }
}