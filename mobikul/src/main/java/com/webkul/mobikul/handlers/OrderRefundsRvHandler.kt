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

import com.webkul.mobikul.fragments.OrderInvoiceDetailsBottomSheetFragment
import com.webkul.mobikul.fragments.OrderRefundDetailsBottomSheetFragment
import com.webkul.mobikul.fragments.RefundsFragment


class OrderRefundsRvHandler(private val mFragmentContext: RefundsFragment) {

    fun onClickViewRefund(incrementId: String, id: String) {
        OrderRefundDetailsBottomSheetFragment.newInstance(incrementId,id).show(mFragmentContext.childFragmentManager, OrderInvoiceDetailsBottomSheetFragment::class.java.simpleName)

    }

    fun onClickSaveRefund() {

    }
}