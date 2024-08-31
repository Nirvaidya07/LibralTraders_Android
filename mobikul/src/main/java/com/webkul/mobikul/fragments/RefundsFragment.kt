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
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.OrderRefundsRvAdapter
import com.libraltraders.android.databinding.FragmentRefundsBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ORDER_DETAILS
import com.webkul.mobikul.models.user.OrderDetailsModel

class
RefundsFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentRefundsBinding

    companion object {
        fun newInstance(incrementId: String, orderDetailsModel: OrderDetailsModel): RefundsFragment {
            val refundsFragment = RefundsFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_INCREMENT_ID, incrementId)
            args.putParcelable(BUNDLE_KEY_ORDER_DETAILS, orderDetailsModel)
            refundsFragment.arguments = args
            return refundsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_refunds, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.data = requireArguments().getParcelable(BUNDLE_KEY_ORDER_DETAILS)

        setupRefundsItemsRv()
    }

    private fun setupRefundsItemsRv() {
        mContentViewBinding.orderRefundsRv.adapter = mContentViewBinding.data!!.creditMemoList?.let { OrderRefundsRvAdapter(this, it) }
        mContentViewBinding.orderRefundsRv.isNestedScrollingEnabled = false
    }
}