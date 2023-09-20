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

package com.webkul.mobikul.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.MyOrdersRvAdapter
import com.webkul.mobikul.databinding.FragmentDashboardRecentOrdersBinding
import com.webkul.mobikul.handlers.DashboardRecentOrdersFragmentHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.user.OrderListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DashboardRecentOrdersFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentDashboardRecentOrdersBinding
    private var mHashIdentifier: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_recent_orders, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callApi()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getOrderListForDashboard" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()))
        ApiConnection.getOrderList(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase(mHashIdentifier), 1, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<OrderListResponseModel>(requireContext(), false) {
                    override fun onNext(orderListResponseModel: OrderListResponseModel) {
                        super.onNext(orderListResponseModel)
                        mContentViewBinding.loading = false
                        if (orderListResponseModel.success) {
                            if (ApplicationConstants.ENABLE_OFFLINE_MODE && mHashIdentifier.isNotEmpty())
                                BaseActivity.mDataBaseHandler.addOrUpdateIntoOfflineTable(mHashIdentifier, orderListResponseModel.eTag, BaseActivity.mObjectMapper.writeValueAsString(orderListResponseModel))
                            onSuccessfulResponse(orderListResponseModel)
                        } else {
                            onFailureResponse(orderListResponseModel)
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
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, OrderListResponseModel::class.java))
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

    private fun onSuccessfulResponse(orderListResponseModel: OrderListResponseModel?) {
        mContentViewBinding.data = orderListResponseModel
        mContentViewBinding.handler = DashboardRecentOrdersFragmentHandler()
        if (mContentViewBinding.data!!.orderList.isEmpty()) {
            mContentViewBinding.animationView.playAnimation()
        } else {
            setupOrderListRv()
        }
    }

    private fun setupOrderListRv() {
        mContentViewBinding.ordersRv.adapter = MyOrdersRvAdapter(requireContext(), mContentViewBinding.data!!.orderList)
    }

    private fun onFailureResponse(orderListResponseModel: OrderListResponseModel) {
        mContentViewBinding.data = orderListResponseModel
        mContentViewBinding.animationView.playAnimation()
    }

    private fun onErrorResponse(e: Throwable) {
        if (mContentViewBinding.data == null)
            mContentViewBinding.data = OrderListResponseModel()
        mContentViewBinding.animationView.playAnimation()
    }
}
