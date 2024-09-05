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

package com.webkul.mobikul.handlers

import android.content.Intent
import com.libraltraders.android.R
import com.webkul.mobikul.activities.AddEditAddressActivity
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.fragments.AddressListFragment
import com.webkul.mobikul.fragments.ShippingInfoFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADDRESS_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_GUEST_USER
import com.webkul.mobikul.helpers.ConstantsHelper.RC_ADD_EDIT_ADDRESS
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.helpers.Utils

class ShippingInfoFragmentHandler(val mFragmentContext: ShippingInfoFragment) {

    fun onClickChangeAddress() {
        val fragmentManager = mFragmentContext.requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        fragmentTransaction.add(android.R.id.content, AddressListFragment.newInstance(mFragmentContext.mContentViewBinding.data!!))
        fragmentTransaction.addToBackStack(AddressListFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickAddEditNewAddress() {
        val intent = Intent(mFragmentContext.context, AddEditAddressActivity::class.java)
        if (AppSharedPref.getCustomerCachedNewAddress(mFragmentContext.requireContext()).firstname.isNullOrEmpty() && !AppSharedPref.isLoggedIn(mFragmentContext.requireContext())) {
            intent.putExtra(BUNDLE_KEY_ADDRESS_DATA, AppSharedPref.getCustomerCachedNewAddress(mFragmentContext.requireContext()))
            intent.putExtra(BUNDLE_KEY_IS_GUEST_USER, true)
        } else {
            if (!AppSharedPref.isLoggedIn(mFragmentContext.requireContext()))
                intent.putExtra(BUNDLE_KEY_IS_GUEST_USER, true)

            intent.putExtra(BUNDLE_KEY_ADDRESS_DATA, mFragmentContext.mContentViewBinding.data!!.newAddressData)
        }
        mFragmentContext.startActivityForResult(intent, RC_ADD_EDIT_ADDRESS)
    }

    fun onClickNextBtn() {
        if (mFragmentContext.mContentViewBinding.data!!.selectedAddressData?.id == "0") {
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.shipping_address_select_error))
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.addEditAddBtn)
            mFragmentContext.mContentViewBinding.scrollView.smoothScrollTo(0, 0)
        } else if (mFragmentContext.mContentViewBinding.data!!.selectedShippingMethod.isNullOrEmpty()) {
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.shipping_method_select_error))
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.shippingMethodRg)
            mFragmentContext.mContentViewBinding.scrollView.smoothScrollTo(0, mFragmentContext.mContentViewBinding.scrollView.bottom)
        } else {
            mFragmentContext.mContentViewBinding.data!!.selectedShippingMethod?.let { (mFragmentContext.context as CheckoutActivity).setupPaymentInfoFragment(it, mFragmentContext.mContentViewBinding.data) }
        }
    }
}