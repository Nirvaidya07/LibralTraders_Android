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

import androidx.fragment.app.Fragment
import com.webkul.mobikul.fragments.PaymentInfoFragment
import com.webkul.mobikul.fragments.ShippingInfoFragment
import com.webkul.mobikul.models.checkout.BillingShippingAddress


class CheckoutShippingAddressRvHandler(val mFragmentContext: Fragment?) {

    fun onClickAddress(billingShippingAddress: BillingShippingAddress) {
        if (mFragmentContext is ShippingInfoFragment) {
            mFragmentContext.setSelectedAddressData(billingShippingAddress)
        } else if (mFragmentContext is PaymentInfoFragment) {
            mFragmentContext.setSelectedAddressData(billingShippingAddress)
        }
        mFragmentContext!!.requireActivity().supportFragmentManager.popBackStack()
    }
}