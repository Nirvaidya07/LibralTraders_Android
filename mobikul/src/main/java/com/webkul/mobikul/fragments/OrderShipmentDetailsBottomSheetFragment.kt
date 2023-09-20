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

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.OrderShipmentItemsRvAdapter
import com.webkul.mobikul.databinding.FragmentOrderShipmentDetailsBottomSheetBinding
import com.webkul.mobikul.handlers.OrderShipmentDetailsBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_SHIPMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY__ID_FROM_ORDER_DETAIL
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.user.ShipmentDetailsData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class OrderShipmentDetailsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    companion object {
        fun newInstance(invoiceId: String,id:String): OrderShipmentDetailsBottomSheetFragment {
            val orderShipmentDetailsBottomSheetFragment = OrderShipmentDetailsBottomSheetFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_SHIPMENT_ID, invoiceId)
            args.putString(BUNDLE_KEY__ID_FROM_ORDER_DETAIL, id)
            orderShipmentDetailsBottomSheetFragment.arguments = args
            return orderShipmentDetailsBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentOrderShipmentDetailsBottomSheetBinding
    var shippmentId=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_shipment_details_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.shipmentId = requireArguments().getString(BUNDLE_KEY_SHIPMENT_ID)
        shippmentId=requireArguments().getString(BUNDLE_KEY__ID_FROM_ORDER_DETAIL,"")
        callApi()
        mContentViewBinding.handler = OrderShipmentDetailsBottomSheetFragmentHandler(this)

    }

    private fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getShipmentDetailsData" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()) + mContentViewBinding.shipmentId!!)
        ApiConnection.getShipmentDetailsData(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier), shippmentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ShipmentDetailsData>(requireContext(), true) {
                    override fun onNext(shipmentDetailsData: ShipmentDetailsData) {
                        super.onNext(shipmentDetailsData)
                        mContentViewBinding.loading = false
                        if (shipmentDetailsData.success) {
                            onSuccessfulResponse(shipmentDetailsData)
                        } else {
                            onFailureResponse(shipmentDetailsData)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
        checkAndLoadLocalData()
    }

    private fun checkAndLoadLocalData() {
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread((context as BaseActivity).mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, ShipmentDetailsData::class.java))
                        }

                    }
                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        (context as BaseActivity).mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun onSuccessfulResponse(shipmentDetailsData: ShipmentDetailsData) {
        mContentViewBinding.data = shipmentDetailsData
        setupOrderItemsRv()
        mContentViewBinding.handler = OrderShipmentDetailsBottomSheetFragmentHandler(this)
    }

    private fun setupOrderItemsRv() {
        mContentViewBinding.orderItemsRv.adapter = OrderShipmentItemsRvAdapter(requireContext(), mContentViewBinding.data!!.items)
        mContentViewBinding.orderItemsRv.isNestedScrollingEnabled = false
    }

    private fun onFailureResponse(shipmentDetailsData: ShipmentDetailsData) {
        AlertDialogHelper.showNewCustomDialog(
                context as BaseActivity,
                getString(R.string.error),
                shipmentDetailsData.message,
                false,
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    dismiss()
                }

                , ""
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(requireContext()) || (error is HttpException && error.code() == 304))) {
            // Do Nothing as the data is already loaded
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    context as BaseActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(requireContext(), error),
                    false,
                    getString(R.string.try_again),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }
                    , getString(R.string.dismiss)
                    , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                dismiss()
            })
        }
    }
}