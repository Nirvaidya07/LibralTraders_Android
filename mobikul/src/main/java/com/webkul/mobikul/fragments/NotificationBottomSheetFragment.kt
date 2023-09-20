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

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.NotificationListRvAdapter
import com.webkul.mobikul.databinding.FragmentNotificationBottomSheetBinding
import com.webkul.mobikul.handlers.NotificationBottomSheetHandler
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.extra.NotificationListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class NotificationBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentNotificationBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.handler = NotificationBottomSheetHandler(this)
        callApi()
        checkAndLoadLocalData()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getNotificationsList" + AppSharedPref.getStoreId(requireContext()))
        ApiConnection.getNotificationsList(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<NotificationListResponseModel>(requireContext(), false) {
                    override fun onNext(notificationListResponseModel: NotificationListResponseModel) {
                        super.onNext(notificationListResponseModel)
                        mContentViewBinding.loading = false
                        if (notificationListResponseModel.success) {
                            onSuccessfulResponse(notificationListResponseModel)
                        } else {
                            onFailureResponse(notificationListResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun checkAndLoadLocalData() {
        BaseActivity.mDataBaseHandler.getResponseFromDatabaseOnThread((context as BaseActivity).mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, NotificationListResponseModel::class.java))
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

    private fun onSuccessfulResponse(notificationListResponseModel: NotificationListResponseModel) {
        if (notificationListResponseModel.notificationList.isEmpty()) {
            mContentViewBinding.notificationListRv.visibility = View.GONE
            mContentViewBinding.emptyLayout.visibility = View.VISIBLE
            val fragmentTransaction = childFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.empty_layout
                    , EmptyFragment.newInstance("empty_review_list.json", getString(R.string.empty_notification), getString(R.string.your_dont_have_any_notification), false)
                    , EmptyFragment::class.java.simpleName)
            fragmentTransaction.commitAllowingStateLoss()
        } else {
            mContentViewBinding.notificationListRv.visibility = View.VISIBLE
            mContentViewBinding.notificationListRv.adapter = NotificationListRvAdapter(requireContext(), notificationListResponseModel.notificationList)
        }
    }

    private fun onFailureResponse(notificationListResponseModel: NotificationListResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                context as BaseActivity,
                getString(R.string.error),
                notificationListResponseModel.message,
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

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(requireContext()) || (error is HttpException && error.code() == 304)) && mContentViewBinding.notificationListRv.adapter != null) {
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