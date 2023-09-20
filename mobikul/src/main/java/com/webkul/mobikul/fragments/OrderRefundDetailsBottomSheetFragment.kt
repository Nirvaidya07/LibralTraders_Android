package com.webkul.mobikul.fragments

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.OrderRefundItemRvAdapter
import com.webkul.mobikul.adapters.OrderTotalsRvAdapter
import com.webkul.mobikul.databinding.FragmentRefundDetailsBottomSheetBinding
import com.webkul.mobikul.handlers.OrderRefundDetailsBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ConstantsHelper.RC_WRITE_TO_EXTERNAL_STORAGE
import com.webkul.mobikul.models.user.RefundDetailsData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OrderRefundDetailsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    companion object {
        fun newInstance(invoiceId: String, id: String): OrderRefundDetailsBottomSheetFragment {
            val orderInvoiceDetailsBottomSheetFragment = OrderRefundDetailsBottomSheetFragment()
            val args = Bundle()
            args.putString(BundleKeysHelper.BUNDLE_KEY_INVOICE_ID, invoiceId)
            args.putString(BundleKeysHelper.BUNDLE_KEY__ID_FROM_ORDER_DETAIL, id)
            orderInvoiceDetailsBottomSheetFragment.arguments = args
            return orderInvoiceDetailsBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentRefundDetailsBottomSheetBinding
    var id: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_refund_details_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var allPermissionsGranted = true
        for (eachGrantResult in grantResults) {
            if (eachGrantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
            }
        }
        if (allPermissionsGranted) {
            if (requestCode == RC_WRITE_TO_EXTERNAL_STORAGE)
                mContentViewBinding.handler!!.onClickSaveInvoice(mContentViewBinding.data!!, mContentViewBinding.invoiceId
                        ?: "")
        }
    }


   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.invoiceId = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY_INVOICE_ID)
        id = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY__ID_FROM_ORDER_DETAIL, "")

        callApi()
        mContentViewBinding.handler = OrderRefundDetailsBottomSheetFragmentHandler(this)

    }

    private fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getRefundDetailsData" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()) + mContentViewBinding.invoiceId!!)
        ApiConnection.getRefundDetailsData(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier), /*mContentViewBinding.invoiceId!!*/ id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<RefundDetailsData>(requireContext(), true) {
                    override fun onNext(invoiceDetailsData: RefundDetailsData) {
                        super.onNext(invoiceDetailsData)
                        mContentViewBinding.loading = false
                        if (invoiceDetailsData.success) {
                            onSuccessfulResponse(invoiceDetailsData)
                        } else {
                            onFailureResponse(invoiceDetailsData)
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
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, RefundDetailsData::class.java))
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

    private fun onSuccessfulResponse(invoiceDetailsData: RefundDetailsData) {
        mContentViewBinding.data = invoiceDetailsData
        setupOrderItemsRv()
        setupOrderTotalsRv()
    }

    private fun setupOrderItemsRv() {
        mContentViewBinding.orderItemsRv.adapter = OrderRefundItemRvAdapter(requireContext(), mContentViewBinding.data!!.items)
        mContentViewBinding.orderItemsRv.isNestedScrollingEnabled = false
    }

    private fun setupOrderTotalsRv() {
        mContentViewBinding.orderTotalsRv.adapter = OrderTotalsRvAdapter(requireContext(), mContentViewBinding.data!!.totals)
        mContentViewBinding.orderTotalsRv.isNestedScrollingEnabled = false
    }

    private fun onFailureResponse(invoiceDetailsData: RefundDetailsData) {
        AlertDialogHelper.showNewCustomDialog(
                context as BaseActivity,
                getString(R.string.error),
                invoiceDetailsData.message,
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