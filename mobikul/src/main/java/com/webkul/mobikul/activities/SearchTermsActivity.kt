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

package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.SearchTermsRvAdapter
import com.libraltraders.android.databinding.ActivitySearchTermsBinding
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.extra.SearchTermsResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SearchTermsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivitySearchTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_terms)
        startInitialization()
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_search_terms)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getSearchTermsList" + AppSharedPref.getStoreId(this))
        ApiConnection.getSearchTermsList(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<SearchTermsResponseModel>(this, true) {
                    override fun onNext(searchTermsResponseModel: SearchTermsResponseModel) {
                        super.onNext(searchTermsResponseModel)
                        mContentViewBinding.loading = false
                        if (searchTermsResponseModel.success) {
                            onSuccessfulResponse(searchTermsResponseModel)
                        } else {
                            onFailureResponse(searchTermsResponseModel)
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
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, SearchTermsResponseModel::class.java))
                        }

                    }
                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun onSuccessfulResponse(searchTermsResponseModel: SearchTermsResponseModel) {
        mContentViewBinding.data = searchTermsResponseModel
        mContentViewBinding.searchTermsRv.adapter = SearchTermsRvAdapter(this, mContentViewBinding.data!!.termList)
    }

    private fun onFailureResponse(accountInfoResponseModel: SearchTermsResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                accountInfoResponseModel.message,
                false,
                getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }
                , getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        })
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this) || (error is HttpException && error.code() == 304)) && mContentViewBinding.data != null) {
            // Do Nothing as the data is already loaded
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this, error),
                    false,
                    getString(R.string.try_again),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }
                    , getString(R.string.dismiss)
                    , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }
}