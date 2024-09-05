package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.libraltraders.android.BuildConfig
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityBrandsCatBinding
import com.webkul.mobikul.adapters.BrandCatAdapter
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.catalog.BrandCategoryResponseModel
import com.webkul.mobikul.network.ApiConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class BrandCatActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityBrandsCatBinding
    private var mCategoryId: String = ""
    private var mCategoryName: String = ""
    private var mPageNumber = 1
    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_brands_cat)
        initSupportActionBar()
        startInitialization()
    }

    override fun initSupportActionBar() {
        setSupportActionBar(mContentViewBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.shop_by_brands)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun startInitialization() {
        mCategoryName = intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE)!!
        mCategoryId = intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_ID)!!
        initSupportActionBar()

        callApi()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        val mHashIdentifier = Utils.getMd5String(
            "getSubCategoryData" + AppSharedPref.getStoreId(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCustomerToken(
                this
            ) + mCategoryId
        )
        val username = BuildConfig.ADMIN_USERNAME
        val password = BuildConfig.ADMIN_PASSWORD

        val disposable: Disposable = ApiConnection.getBrandCategoryData(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    // Handle successful response
                    mContentViewBinding.loading = false
                    onSuccessfulResponse(response)
                }, { error ->
                    // Handle error
                    mContentViewBinding.loading = false
                    // Log or show error message
                })
        compositeDisposable.add(disposable)

//                .subscribe(object : ApiCustomCallback<BrandCategoryResponseModel>(this, false) {
//                override fun onNext(subCategoryResponseModel: BrandCategoryViewModel) {
//                    super.onNext(subCategoryResponseModel)
//                    mContentViewBinding.loading = false
//                    if (subCategoryResponseModel.success) {
//                        onSuccessfulResponse(subCategoryResponseModel)
//                    } else {
//                        onFailureResponse(subCategoryResponseModel)
//                    }
//                }
//
//                override fun onError(e: Throwable) {
//                    super.onError(e)
//                    mContentViewBinding.loading = false
//                    onErrorResponse(e)
//                }
//            })
    }

    private fun onSuccessfulResponse(brandCategoryResponseModel: List<BrandCategoryResponseModel>) {
        Log.d("TAG", "onSuccessfulResponse: ${brandCategoryResponseModel.getOrNull(0)}")

        // Ensure brandList is initialized
        val brandList = mContentViewBinding.data?.brandList ?: mutableListOf()
        brandList.addAll(brandCategoryResponseModel)

        // Ensure RecyclerView is updated on the main thread
        mContentViewBinding.brandCatRecyclerView.layoutManager = LinearLayoutManager(mContentViewBinding.root.context)

        if (brandList.isNotEmpty()) {
            mContentViewBinding.brandCatRecyclerView.adapter = BrandCatAdapter(this, brandList)
        }
    }


    private fun onFailureResponse(responseModel: BrandCategoryResponseModel) {
        // Handle API failure case
        AlertDialogHelper.showNewCustomDialog(
            this,
            getString(R.string.error),
            responseModel.message ?: getString(R.string.something_went_wrong),
            false,
            getString(R.string.try_again),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                callApi()
            },
            getString(R.string.dismiss),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            }
        )
    }

    private fun onErrorResponse(error: Throwable) {
        if (!NetworkHelper.isNetworkAvailable(this) || (error is HttpException && error.code() == 304)) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                NetworkHelper.getErrorMessage(this, error),
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mPageNumber--
                    callApi()
                },
                getString(R.string.dismiss),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    finish()
                }
            )
        }
    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<String> {
                override fun onNext(response: String) {
                    if (response.isNotBlank()) {
//                        onSuccessfulResponse(
//                            mObjectMapper.readValue(response, BrandCategoryResponseModel::class.java)
//                        )
                    } else {
                        onErrorResponse(error)
                    }
                }

                override fun onError(e: Throwable) {}

                override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
                    mCompositeDisposable.add(disposable)
                }

                override fun onComplete() {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
