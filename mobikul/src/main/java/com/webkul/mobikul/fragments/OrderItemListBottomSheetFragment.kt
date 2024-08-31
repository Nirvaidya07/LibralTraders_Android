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

package com.webkul.mobikul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.OrderItemsForReviewRvAdapter
import com.libraltraders.android.databinding.FragmentOrderItemListBottomSheetBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ORDER_ITEMS
import com.webkul.mobikul.models.user.OrderItem

class OrderItemListBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(productOptions: ArrayList<OrderItem>): OrderItemListBottomSheetFragment {
            val orderItemListBottomSheetFragment = OrderItemListBottomSheetFragment()
            val args = Bundle()
            args.putParcelableArrayList(BUNDLE_KEY_ORDER_ITEMS, productOptions)
            orderItemListBottomSheetFragment.arguments = args
            return orderItemListBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentOrderItemListBottomSheetBinding
    private var mOrderItems: ArrayList<OrderItem> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_item_list_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderItems = requireArguments().getParcelableArrayList(BUNDLE_KEY_ORDER_ITEMS) ?: ArrayList()
        startInitialization()
    }

    private fun startInitialization() {
        setupOrderItemsRv()
    }

    private fun setupOrderItemsRv() {
        mContentViewBinding.orderItemsRv.adapter = OrderItemsForReviewRvAdapter(this, mOrderItems)
        mContentViewBinding.orderItemsRv.isNestedScrollingEnabled = false
    }
}