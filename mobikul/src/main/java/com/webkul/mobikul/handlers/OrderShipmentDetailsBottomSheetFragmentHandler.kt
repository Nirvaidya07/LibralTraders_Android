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

import com.webkul.mobikul.fragments.OrderShipmentDetailsBottomSheetFragment
import com.webkul.mobikul.fragments.TrackShipmentDialogFragment
import com.webkul.mobikul.models.user.TrackingData


class OrderShipmentDetailsBottomSheetFragmentHandler(private var mFragmentContext: OrderShipmentDetailsBottomSheetFragment) {

    fun onClickCancelBtn() {
        mFragmentContext.dismiss()
    }

    fun onClickTrackShipment(trackingData: ArrayList<TrackingData>) {
        TrackShipmentDialogFragment.newInstance(trackingData).show(mFragmentContext.childFragmentManager, TrackShipmentDialogFragment::class.java.simpleName)
    }
}