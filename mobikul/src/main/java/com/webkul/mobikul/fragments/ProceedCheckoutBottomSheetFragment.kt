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

package com.webkul.mobikul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentProceedCheckoutBottomSheetBinding
import com.webkul.mobikul.handlers.ProceedCheckoutBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CAN_GUEST_CHECKOUT_DOWNLOADABLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CONTAINS_DOWNLOADABLE_PRODUCTS
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_ALLOWED_GUEST_CHECKOUT
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_VIRTUAL_CART

class ProceedCheckoutBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(isVirtual: Boolean, isAllowedGuestCheckout: Boolean, containsDownloadableProducts: Boolean, canGuestCheckoutDownloadable: Boolean): ProceedCheckoutBottomSheetFragment {
            val proceedCheckoutBottomSheetFragment = ProceedCheckoutBottomSheetFragment()
            val args = Bundle()
            args.putBoolean(BUNDLE_KEY_IS_VIRTUAL_CART, isVirtual)
            args.putBoolean(BUNDLE_KEY_IS_ALLOWED_GUEST_CHECKOUT, isAllowedGuestCheckout)
            args.putBoolean(BUNDLE_KEY_CONTAINS_DOWNLOADABLE_PRODUCTS, containsDownloadableProducts)
            args.putBoolean(BUNDLE_KEY_CAN_GUEST_CHECKOUT_DOWNLOADABLE, canGuestCheckoutDownloadable)
            proceedCheckoutBottomSheetFragment.arguments = args
            return proceedCheckoutBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentProceedCheckoutBottomSheetBinding
    var mIsVirtual = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_proceed_checkout_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mIsVirtual = requireArguments().getBoolean(BUNDLE_KEY_IS_VIRTUAL_CART)
        checkForGuestCheckout()
        mContentViewBinding.handler = ProceedCheckoutBottomSheetFragmentHandler(this)
    }

    private fun checkForGuestCheckout() {
        if (requireArguments().getBoolean(BUNDLE_KEY_IS_ALLOWED_GUEST_CHECKOUT)) {
            if (requireArguments().getBoolean(BUNDLE_KEY_CONTAINS_DOWNLOADABLE_PRODUCTS)) {
                if (requireArguments().getBoolean(BUNDLE_KEY_CAN_GUEST_CHECKOUT_DOWNLOADABLE)) {
                    mContentViewBinding.showGuestCheckout = true
                }
            } else {
                mContentViewBinding.showGuestCheckout = true
            }
        }
    }
}