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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.BookingDataAdapter
import com.webkul.mobikul.adapters.OrderShipmentItemsRvAdapter
import com.libraltraders.android.databinding.FragmentOrderShipmentDetailsBottomSheetBinding
import com.webkul.mobikul.handlers.OrderShipmentDetailsBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_SHIPMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY__ID_FROM_ORDER_DETAIL
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.models.BookingData
import com.webkul.mobikul.models.product.OrderTrackingResponse
import com.webkul.mobikul.models.product.TrackSummary
import com.webkul.mobikul.models.product.Transit
import com.webkul.mobikul.models.user.OrderShipmentItem
import com.webkul.mobikul.models.user.ShipmentDetailsData
import com.webkul.mobikul.network.ApiConnection
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException


class OrderShipmentDetailsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    companion object {
        fun newInstance(invoiceId: String, id: String): OrderShipmentDetailsBottomSheetFragment {
            val orderShipmentDetailsBottomSheetFragment = OrderShipmentDetailsBottomSheetFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_SHIPMENT_ID, invoiceId)
            args.putString(BUNDLE_KEY__ID_FROM_ORDER_DETAIL, id)
            orderShipmentDetailsBottomSheetFragment.arguments = args
            return orderShipmentDetailsBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentOrderShipmentDetailsBottomSheetBinding
    var shippmentId = ""
    var transitList = ArrayList<Transit>()
    var bookingDataList = ArrayList<BookingData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_shipment_details_bottom_sheet,
            container,
            false
        )
        return mContentViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.shipmentId = requireArguments().getString(BUNDLE_KEY_SHIPMENT_ID)
        shippmentId = requireArguments().getString(BUNDLE_KEY__ID_FROM_ORDER_DETAIL, "")
        callApi()
        mContentViewBinding.handler = OrderShipmentDetailsBottomSheetFragmentHandler(this)

    }

    private fun callApi() {
        mContentViewBinding.loading = true
        /*(context as BaseActivity).mHashIdentifier = Utils.getMd5String("getShipmentDetailsData" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()) + mContentViewBinding.shipmentId!!)
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
                })*/
        Log.d("TAG", "callApi:shipmenr " + shippmentId + "   " + context?.let {
            AppSharedPref.getCustomerId(
                it
            )
        })
        ApiConnection.getOrderTrackingData(requireContext(), shippmentId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                var response = it
                Log.d("TAG", "callApi: " + response)
                if (response.contains("tracking_number")) {
                    // response = response.replaceFirst("[","")

                    response = response.substring(1, response.length - 1)
                    handleTackingData(response)
                    //orderTrackingResponse.trackSummary;
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Shipment details not available for this order",
                        Toast.LENGTH_LONG
                    ).show()
                }
                mContentViewBinding.loading = false

            },
                {
                    mContentViewBinding.loading = false
                    Toast.makeText(
                        requireContext(),
                        "shipment details not available for this order",
                        Toast.LENGTH_LONG
                    ).show()
                })
        checkAndLoadLocalData()
    }

    private fun handleTackingData(response: String) {

        var orderTrackingResponse =
            Gson().fromJson(response, OrderTrackingResponse::class.java)
        var jsonObject = JSONObject(response);
        var carrierTitle:String=jsonObject.getString("carrier_title");
        var trackingNumber:String=jsonObject.getString("tracking_number");
        var trackSummary = jsonObject.getJSONObject("track_summary")
        Log.d("TAG", "handleTackingData: " + orderTrackingResponse.carrier_title)
        if (orderTrackingResponse.carrier_title.equals("Ecomm Ordertracking", true)) {
            var trackingNumber1 = BookingData(
                "Tracking Number: ",
                trackingNumber
            );
            var carrierTitle1 = BookingData(
                "Carrier: ",
                carrierTitle
            );
            bookingDataList.add(trackingNumber1)
            bookingDataList.add(carrierTitle1)
//                        bookingDataList.add(bookingData4)
            mContentViewBinding.orderReceiverDetail.adapter =
                BookingDataAdapter(bookingDataList)
            var objectField = trackSummary.getJSONObject("object")
            var field = objectField.getJSONArray("field")
            Log.d("TAG", "handleTackingData: array" + field.length())
            for (i in 0 until field.length()) {
                try {
                    var jsonobject = field.getJSONObject(i)
//                    Log.d("TAG", "handleTackingData: data " + jsonobject.has("object"))
                    if (jsonobject.has("object")) {
                        var childObject = jsonobject.getJSONArray("object")
                        for (i in 0 until childObject.length()) {
                            var fieldJsonArray = childObject.getJSONObject(i).getJSONArray("field")
//                            Log.d("TAG", "handleTackingData: " + fieldJsonArray)
                            for (i in 0 until fieldJsonArray.length()) {
                                Log.d("TAG", "handleTackingData: "+i+"--" + fieldJsonArray.get(i))
                            }
                            var transit = Transit();
                            transit.Job =
                                fieldJsonArray.get(4).toString() + "(" + fieldJsonArray.get(1) + ")"
                            transit.Route = fieldJsonArray.get(6)
                                .toString() + "(" + fieldJsonArray.get(9) + ")"
                            var date=fieldJsonArray.get(0).toString().split(",");
                            transit.TransitTime = date[date.size-1]
                            transit.TransitDate = fieldJsonArray.get(0).toString().split(date[date.size-1])[0]
                            transitList.add(transit)
//                            }
                        }
//                        var fieldObject=childObject.get("field")
//                        var fieldObject=childObject.getJSONArray(1)

                    }
                    setupOrderItemsRv()
                } catch (e: Exception) {

                }

            }
        } else if (orderTrackingResponse.carrier_title.equals("Trackon", true)) {
            var summartTrack = trackSummary.getJSONObject("summaryTrack");

            var bookingData = BookingData(
                "Origin: ",
                summartTrack.getString("ORIGIN")
            );
            var bookingData1 = BookingData(
                "Destination: ",
                summartTrack.getString("DESTINATION")
            );
            var bookingData2 = BookingData(
                "Booking Date: ",
                summartTrack.getString("BOOKING_DATE")
            );
            var bookingData3 = BookingData(
                "Service Type: ",
                summartTrack.getString("SERVICE_TYPE")
            );
            var trackingNumber1 = BookingData(
                "Tracking Number: ",
                trackingNumber
            );
            var carrierTitle1 = BookingData(
                "Carrier: ",
                carrierTitle
            );
            var statys = BookingData(
                "Status: ",
                summartTrack.getString("CURRENT_STATUS")
            );
            bookingDataList.add(trackingNumber1)
            bookingDataList.add(carrierTitle1)
            bookingDataList.add(bookingData)
            bookingDataList.add(bookingData1)
            bookingDataList.add(bookingData2)
            bookingDataList.add(bookingData3)
            bookingDataList.add(statys)
//                        bookingDataList.add(bookingData4)
            mContentViewBinding.orderReceiverDetail.adapter =
                BookingDataAdapter(bookingDataList)
            var scanDetailArray: JSONArray = trackSummary.getJSONArray("lstDetails")
            for (i in 0 until scanDetailArray.length()) {
                val scan = scanDetailArray.getJSONObject(i)
                var transit = Transit();
                transit.Job = scan.getString("CURRENT_CITY")
                transit.Route = scan.getString("CURRENT_STATUS")
                transit.TransitTime = scan.getString("EVENTTIME")
                transit.TransitDate = scan.getString("EVENTDATE")
                Log.d("TAG", "callApi: " + scanDetailArray.length())
                transitList.add(transit)
            }
            setupOrderItemsRv()

        } else if (orderTrackingResponse.carrier_title.equals("Bluedart", true)) {
            var trackingNumber1 = BookingData(
                "Tracking Number: ",
                trackingNumber
            );
            var carrierTitle1 = BookingData(
                "Carrier: ",
                carrierTitle
            );
            var shipment = trackSummary.getJSONObject("Shipment")
            var status = BookingData(
                "Status: ",
                shipment.getString("Status")
            );
            bookingDataList.add(trackingNumber1)
            bookingDataList.add(carrierTitle1)
            bookingDataList.add(status)
            mContentViewBinding.orderReceiverDetail.adapter =
                BookingDataAdapter(bookingDataList)
            var scanObject = shipment.getJSONObject("Scans")
            var scanDetailArray: JSONArray = scanObject.getJSONArray("ScanDetail")
            for (i in 0 until scanDetailArray.length()) {
                val scan = scanDetailArray.getJSONObject(i)
                var transit = Transit();
                transit.Job = scan.getString("Scan")
                transit.Route = scan.getString("ScannedLocation")
                transit.TransitTime = scan.getString("ScanTime")
                transit.TransitDate = scan.getString("ScanDate")
                println("${transit.Job} by")
                transitList.add(transit)
            }
            setupOrderItemsRv()

            Log.d("TAG", "callApi: bluedart " + transitList.size)

        } else if (orderTrackingResponse.carrier_title.equals("ShreeTirupati")) {
            if (orderTrackingResponse != null && orderTrackingResponse.track_summary!!.BookingData != null) {
                var bookingData = BookingData(
                    "ReceiverName: ",
                    orderTrackingResponse.track_summary!!.BookingData!!.ReceiverName
                );
                var bookingData3 = BookingData(
                    "From: ",
                    orderTrackingResponse.track_summary!!.BookingData!!.FromCenter
                );
                var bookingData4 = BookingData(
                    "To: ",
                    orderTrackingResponse.track_summary!!.BookingData!!.ToCenter
                );
                var bookingData1 = BookingData(
                    "Booking Date: ",
                    orderTrackingResponse.track_summary!!.BookingData!!.BookingDate
                );
                var bookingData2 = BookingData(
                    "Booking Time: ",
                    orderTrackingResponse.track_summary!!.BookingData!!.BookingTime
                );
                var trackingNumber1 = BookingData(
                    "Tracking Number: ",
                    trackingNumber
                );
                var carrierTitle1 = BookingData(
                    "Carrier: ",
                    carrierTitle
                );
                var statys = BookingData(
                    "Status: ",
                    orderTrackingResponse.track_summary!!.Status
                );
                bookingDataList.add(trackingNumber1)
                bookingDataList.add(carrierTitle1)
                bookingDataList.add(bookingData)
                bookingDataList.add(bookingData1)
                bookingDataList.add(bookingData2)
                bookingDataList.add(bookingData3)
                bookingDataList.add(bookingData4)
                bookingDataList.add(statys)
                mContentViewBinding.orderReceiverDetail.adapter =
                    BookingDataAdapter(bookingDataList)
            }
            if (orderTrackingResponse != null && orderTrackingResponse.track_summary != null && orderTrackingResponse.track_summary!!.TransitHistory != null && orderTrackingResponse.track_summary!!.TransitHistory!!.Transit != null) {
                orderTrackingResponse.carrier_title
                transitList =
                    orderTrackingResponse!!.track_summary!!.TransitHistory!!.Transit!!
                setupOrderItemsRv()
            }
        }

    }

    private fun checkAndLoadLocalData() {
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread((context as BaseActivity).mHashIdentifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<String> {
                override fun onNext(response: String) {
                    if (response.isNotBlank()) {
                        onSuccessfulResponse(
                            BaseActivity.mObjectMapper.readValue(
                                response,
                                ShipmentDetailsData::class.java
                            )
                        )
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
        val list = ArrayList<OrderShipmentItem>()
        list.addAll(mContentViewBinding.data!!.items)
        list.addAll(mContentViewBinding.data!!.items)
        list.addAll(mContentViewBinding.data!!.items)
        list.addAll(mContentViewBinding.data!!.items)
        list.addAll(mContentViewBinding.data!!.items)
        Log.d("TAG", "setupOrderItemsRv: " + transitList.size)
        mContentViewBinding.orderItemsRv.adapter =
            OrderShipmentItemsRvAdapter(requireContext(), transitList)
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
            }, "", null
        )
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
                },
                getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    dismiss()
                })
        }
    }
}