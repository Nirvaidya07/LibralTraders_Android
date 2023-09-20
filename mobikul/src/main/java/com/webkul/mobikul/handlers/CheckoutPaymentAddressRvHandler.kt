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

import com.webkul.mobikul.fragments.PaymentInfoFragment


class CheckoutPaymentAddressRvHandler(val mFragmentContext: PaymentInfoFragment) {

    fun onClickAddress(position: Int, id: String) {
        mFragmentContext.mContentViewBinding.checkoutAddressData!!.selectedAddressData?.id = id
        for (noOfSwatchValue in mFragmentContext.mContentViewBinding.checkoutAddressData!!.addressData!!.indices) {
            mFragmentContext.mContentViewBinding.checkoutAddressData!!.addressData?.get(noOfSwatchValue)?.isSelected = noOfSwatchValue == position
        }
        mFragmentContext.mContentViewBinding.checkoutAddressData?.addressData?.get(position)?.isSelected = false
    }
}