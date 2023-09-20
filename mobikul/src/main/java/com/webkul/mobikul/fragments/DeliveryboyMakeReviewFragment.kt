package com.webkul.mobikul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentDeliveryboyMakeReviewBinding
import com.webkul.mobikul.handlers.DeliveryboyMakeReviewHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CUSTOMER_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_DELIVERY_BOY_ID
import com.webkul.mobikul.models.DeliveryboyMakeReviewData

/**
 * Webkul Software.
 *
 * @author Webkul
 * @category Mobikul
 * @package com.webkul.mobikulmp.fragments
 * @copyright Copyright (c) 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html
 * @date 26/07/19
 */
class DeliveryboyMakeReviewFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentDeliveryboyMakeReviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_deliveryboy_make_review, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.data = DeliveryboyMakeReviewData()
        mContentViewBinding.handler = DeliveryboyMakeReviewHandler(this, requireArguments().getString(BUNDLE_KEY_DELIVERY_BOY_ID), requireArguments().getString(BUNDLE_KEY_CUSTOMER_ID))

        if (AppSharedPref.isLoggedIn(requireContext())) {
            mContentViewBinding.data!!.nickName = AppSharedPref.getCustomerName(requireContext())
            mContentViewBinding.executePendingBindings()
        }

        mContentViewBinding.commentEt.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (mContentViewBinding.commentEt.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    companion object {

        fun newInstance(deliveryBoyId: String, customerId: String): DeliveryboyMakeReviewFragment {
            val args = Bundle()
            args.putString(BUNDLE_KEY_DELIVERY_BOY_ID, deliveryBoyId)
            args.putString(BUNDLE_KEY_CUSTOMER_ID, customerId)
            val fragment = DeliveryboyMakeReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }


}
