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
import androidx.fragment.app.DialogFragment
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.ShipmentTrackingDetailsRvAdapter
import com.libraltraders.android.databinding.FragmentTrackShipmentDialogBinding
import com.webkul.mobikul.handlers.TrackShipmentDialogHandler
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_TRACKING_DATA
import com.webkul.mobikul.models.user.TrackingData

class TrackShipmentDialogFragment : DialogFragment() {

    lateinit var mContentViewBinding: FragmentTrackShipmentDialogBinding
    var mTrackingData: ArrayList<TrackingData> = ArrayList()

    companion object {
        fun newInstance(trackingData: ArrayList<TrackingData>): TrackShipmentDialogFragment {
            val trackShipmentDialogFragment = TrackShipmentDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(BUNDLE_KEY_TRACKING_DATA, trackingData)
            trackShipmentDialogFragment.arguments = args
            return trackShipmentDialogFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_shipment_dialog, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTrackingData = requireArguments().getParcelableArrayList(BUNDLE_KEY_TRACKING_DATA)?:ArrayList()
        setupTrackDetailsRv()
        mContentViewBinding.handler = TrackShipmentDialogHandler(this)
    }

    private fun setupTrackDetailsRv() {
        mContentViewBinding.trackingDetailsRv.adapter = ShipmentTrackingDetailsRvAdapter(requireContext(), mTrackingData)
        mContentViewBinding.trackingDetailsRv.isNestedScrollingEnabled = false
    }
}