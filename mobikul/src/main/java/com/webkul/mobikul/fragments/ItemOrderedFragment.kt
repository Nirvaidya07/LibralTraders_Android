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
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.AssignedDeliveryBoysRvAdapter
import com.webkul.mobikul.adapters.OrderItemsRvAdapter
import com.webkul.mobikul.adapters.OrderTotalsRvAdapter
import com.webkul.mobikul.databinding.FragmentItemOrderedBinding
import com.webkul.mobikul.handlers.ItemOrderedFragmentHandler
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ORDER_DETAILS
import com.webkul.mobikul.models.user.OrderDetailsModel

class ItemOrderedFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentItemOrderedBinding

    companion object {
        fun newInstance(incrementId: String, orderDetailsModel: OrderDetailsModel): ItemOrderedFragment {
            val itemOrderedFragment = ItemOrderedFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_INCREMENT_ID, incrementId)
            args.putParcelable(BUNDLE_KEY_ORDER_DETAILS, orderDetailsModel)
            itemOrderedFragment.arguments = args
            return itemOrderedFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_ordered, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.data = requireArguments().getParcelable(BUNDLE_KEY_ORDER_DETAILS)

        setupOrderItemsRv()
        setupDeliveryBoysRv()
        setupOrderTotalsRv()

        mContentViewBinding.handler = ItemOrderedFragmentHandler(this, requireArguments().getString(BUNDLE_KEY_INCREMENT_ID))
    }

    private fun setupDeliveryBoysRv() {
        mContentViewBinding.data?.deliveryBoys?.let {
            mContentViewBinding.assignedDeliveryBoys.adapter = mContentViewBinding.data!!.incrementId?.let { it1 -> AssignedDeliveryBoysRvAdapter(this, it, it1) }
        }

    }

    private fun setupOrderItemsRv() {
        mContentViewBinding.orderItemsRv.adapter = mContentViewBinding.data!!.orderData?.items?.let { OrderItemsRvAdapter(requireContext(), it) }
        mContentViewBinding.orderItemsRv.isNestedScrollingEnabled = false
    }

    private fun setupOrderTotalsRv() {
        mContentViewBinding.orderTotalsRv.adapter = mContentViewBinding.data!!.orderData?.totals?.let { OrderTotalsRvAdapter(requireContext(), it) }
        mContentViewBinding.orderTotalsRv.isNestedScrollingEnabled = false
    }
}