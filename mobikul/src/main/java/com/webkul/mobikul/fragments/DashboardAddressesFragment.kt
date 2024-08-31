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


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.FragmentDashboardAddressesBinding
import com.webkul.mobikul.handlers.DashboardAddressesFragmentHandler
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.ConstantsHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.user.AddressBookResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DashboardAddressesFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentDashboardAddressesBinding
    private var mHashIdentifier: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_addresses, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callApi()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getAddressBookDataForDashboard" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()))
        ApiConnection.getAddressBookData(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase(mHashIdentifier), true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddressBookResponseModel>(requireContext(), false) {
                    override fun onNext(addressBookResponseModel: AddressBookResponseModel) {
                        super.onNext(addressBookResponseModel)
                        mContentViewBinding.loading = false
                        if (addressBookResponseModel.success) {
                            if (ApplicationConstants.ENABLE_OFFLINE_MODE && mHashIdentifier.isNotEmpty())
                                BaseActivity.mDataBaseHandler.addOrUpdateIntoOfflineTable(mHashIdentifier, addressBookResponseModel.eTag, BaseActivity.mObjectMapper.writeValueAsString(addressBookResponseModel))
                            onSuccessfulResponse(addressBookResponseModel)
                        } else {
                            onFailureResponse(addressBookResponseModel)
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
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, AddressBookResponseModel::class.java))
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

    private fun onSuccessfulResponse(addressBookResponseModel: AddressBookResponseModel?) {
        mContentViewBinding.data = addressBookResponseModel
        mContentViewBinding.handler = DashboardAddressesFragmentHandler(this)
    }

    private fun onFailureResponse(addressBookResponseModel: AddressBookResponseModel) {

    }

    private fun onErrorResponse(e: Throwable) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == ConstantsHelper.RC_ADD_EDIT_ADDRESS) {
                callApi()
            }
        }
    }
}
