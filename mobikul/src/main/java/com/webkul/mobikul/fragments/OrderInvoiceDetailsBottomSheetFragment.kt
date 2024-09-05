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
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.OrderInvoiceItemsRvAdapter
import com.webkul.mobikul.adapters.OrderTotalsRvAdapter
import com.libraltraders.android.databinding.FragmentOrderInvoiceDetailsBottomSheetBinding
import com.webkul.mobikul.handlers.OrderInvoiceDetailsBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INVOICE_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INVOICE_INCREMENT_ID
import com.webkul.mobikul.models.user.InvoiceDetailsData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class OrderInvoiceDetailsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    companion object {
        fun newInstance(invoiceIncrementId: String, invoiceId: String,incrementId:String?): OrderInvoiceDetailsBottomSheetFragment {
            val orderInvoiceDetailsBottomSheetFragment = OrderInvoiceDetailsBottomSheetFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_INVOICE_INCREMENT_ID, invoiceIncrementId)
            args.putString(BUNDLE_KEY_INCREMENT_ID, incrementId)
            args.putString(BUNDLE_KEY_INVOICE_ID, invoiceId)
            orderInvoiceDetailsBottomSheetFragment.arguments = args
            return orderInvoiceDetailsBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentOrderInvoiceDetailsBottomSheetBinding
    var id:String=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_invoice_details_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.incrementId = requireArguments().getString(BUNDLE_KEY_INVOICE_INCREMENT_ID)
        id = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY_INVOICE_ID, "")

        callApi()
        mContentViewBinding.handler = OrderInvoiceDetailsBottomSheetFragmentHandler(this)

    }

    private fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getInvoiceDetailsData" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()) + mContentViewBinding.incrementId!!)
        ApiConnection.getInvoiceDetailsData(requireContext(),
                BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier),
                /*mContentViewBinding.invoiceId!!*/ id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<InvoiceDetailsData>(requireContext(), true) {
                    override fun onNext(invoiceDetailsData: InvoiceDetailsData) {
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
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, InvoiceDetailsData::class.java))
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

    private fun onSuccessfulResponse(invoiceDetailsData: InvoiceDetailsData) {
        mContentViewBinding.data = invoiceDetailsData
        setupOrderItemsRv()
        setupOrderTotalsRv()
    }

    private fun setupOrderItemsRv() {
        mContentViewBinding.orderItemsRv.adapter = OrderInvoiceItemsRvAdapter(requireContext(), mContentViewBinding.data!!.items)
        mContentViewBinding.orderItemsRv.isNestedScrollingEnabled = false
    }

    private fun setupOrderTotalsRv() {
        mContentViewBinding.orderTotalsRv.adapter = OrderTotalsRvAdapter(requireContext(), mContentViewBinding.data!!.totals)
        mContentViewBinding.orderTotalsRv.isNestedScrollingEnabled = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allPermissionsGranted = true
        for (eachGrantResult in grantResults) {
            if (eachGrantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
            }
        }
        if (allPermissionsGranted) {
            if (requestCode == ConstantsHelper.RC_WRITE_TO_EXTERNAL_STORAGE) {
                mContentViewBinding.handler?.onClickSaveInvoice()
            }
        }

    }

    private fun onFailureResponse(invoiceDetailsData: InvoiceDetailsData) {
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