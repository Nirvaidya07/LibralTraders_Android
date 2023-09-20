package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.MyDownloadableProductsRvAdapter
import com.webkul.mobikul.databinding.ActivityMyDownloadableProductsBinding
import com.webkul.mobikul.fragments.EmptyFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.DownloadableProductsListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
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

class MyDownloadableProductsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityMyDownloadableProductsBinding
    private var mPageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_downloadable_products)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.menu_item_notification).isVisible = false
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_my_downloadable_products)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getDownloadableProductsList" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + mPageNumber)
        ApiConnection.getDownloadableProductsList(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mPageNumber++)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<DownloadableProductsListResponseModel>(this, true) {
                    override fun onNext(downloadableProductsListResponseModel: DownloadableProductsListResponseModel) {
                        super.onNext(downloadableProductsListResponseModel)
                        mContentViewBinding.loading = false
                        if (downloadableProductsListResponseModel.success) {
                            onSuccessfulResponse(downloadableProductsListResponseModel)
                        } else {
                            onFailureResponse(downloadableProductsListResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)


                    }
                })
        if(mPageNumber==2)
        checkAndLoadLocalData()
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            val downloadableProductsListResponseModel: DownloadableProductsListResponseModel =mObjectMapper.readValue(response, DownloadableProductsListResponseModel::class.java)
                                onSuccessfulResponse(downloadableProductsListResponseModel)
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

    private fun onSuccessfulResponse(downloadableProductsListResponseModel: DownloadableProductsListResponseModel) {
        if (mPageNumber == 2) {
            mContentViewBinding.data = downloadableProductsListResponseModel
            if (mContentViewBinding.data!!.downloadsList.isEmpty()) {
                addEmptyLayout()
            } else {
                removeEmptyLayout()
                setupDownloadsListRv()
            }
        } else {
            mContentViewBinding.data!!.downloadsList.addAll(downloadableProductsListResponseModel.downloadsList)
            mContentViewBinding.downloadableProductsRv.adapter?.notifyItemRangeChanged(mContentViewBinding.data!!.downloadsList.size - downloadableProductsListResponseModel.downloadsList.size, downloadableProductsListResponseModel.downloadsList.size)
        }
    }

    private fun addEmptyLayout() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(android.R.id.content
                , EmptyFragment.newInstance("empty_downloadable_list.json", getString(R.string.empty_downloadable_products_list), getString(R.string.your_didnt_purchase_any_downloadable_product_yet), false)
                , EmptyFragment::class.java.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun removeEmptyLayout() {
        val emptyFragment = supportFragmentManager.findFragmentByTag(EmptyFragment::class.java.simpleName)
        if (emptyFragment != null)
            supportFragmentManager.beginTransaction().remove(emptyFragment).commitAllowingStateLoss()
    }

    private fun setupDownloadsListRv() {
        mContentViewBinding.downloadableProductsRv.adapter = MyDownloadableProductsRvAdapter(this, mContentViewBinding.data!!.downloadsList)

        mContentViewBinding.downloadableProductsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastCompletelyVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (!mContentViewBinding.loading!! && mContentViewBinding.data!!.downloadsList.size < mContentViewBinding.data!!.totalCount
                        && lastCompletelyVisibleItemPosition > mContentViewBinding.data!!.downloadsList.size - 3) {
                    callApi()
                }
            }
        })
    }

    override fun onFailureResponse(response: Any) {
        super.onFailureResponse(response)
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                // Do nothing as it will be handled from the super.
            }
            else -> {
                AlertDialogHelper.showNewCustomDialog(
                        this,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            mPageNumber--
                            callApi()
                        }
                        , ""
                        , null)
            }
        }
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
                        mPageNumber--
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