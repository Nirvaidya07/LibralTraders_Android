package com.webkul.mobikul.handlers

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.BaseActivity.Companion.mObjectMapper
import com.webkul.mobikul.activities.MyDownloadableProductsActivity
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ConstantsHelper.RC_WRITE_TO_EXTERNAL_STORAGE
import com.webkul.mobikul.models.user.DownloadProductsResponseModel
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

class MyDownloadableProductsRvHandler(val mContext: MyDownloadableProductsActivity) {

    fun onClickDetailsBtn(incrementId: String) {
        val intent = Intent(mContext, OrderDetailsActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID, incrementId)
        mContext.startActivity(intent)
    }

    fun onClickDownloadBtn(hash: String) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                mContext.requestPermissions(permissions, RC_WRITE_TO_EXTERNAL_STORAGE)
            }
        } else {
            mContext.mContentViewBinding.loading = true
            mContext.mHashIdentifier = Utils.getMd5String("downloadProduct" + AppSharedPref.getStoreId(mContext) + AppSharedPref.getCustomerToken(mContext) + hash)
            ApiConnection.downloadProduct(mContext, mDataBaseHandler.getETagFromDatabase(mContext.mHashIdentifier), hash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<DownloadProductsResponseModel>(mContext, true) {
                        override fun onNext(downloadProductsResponseModel: DownloadProductsResponseModel) {
                            super.onNext(downloadProductsResponseModel)
                            mContext.mContentViewBinding.loading = false
                            if (downloadProductsResponseModel.success) {
                                onSuccessfulResponse(downloadProductsResponseModel)
                            } else {
                                onFailureResponse(downloadProductsResponseModel)
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContext.mContentViewBinding.loading = false
                            onErrorResponse(e)
                        }
                    })
        }
    }

    private fun onSuccessfulResponse(downloadProductsResponseModel: DownloadProductsResponseModel) {
        DownloadHelper.downloadFile(mContext, downloadProductsResponseModel.url, downloadProductsResponseModel.fileName, downloadProductsResponseModel.mimeType)
    }

    private fun onFailureResponse(downloadProductsResponseModel: DownloadProductsResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                downloadProductsResponseModel.message,
                false,
                mContext.getString(R.string.ok),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }, null, null)
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(mContext) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    mContext,
                    mContext.getString(R.string.error),
                    NetworkHelper.getErrorMessage(mContext, error),
                    false,
                    mContext.getString(R.string.ok),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }, null, null)
        }


    }


    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mContext.mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, DownloadProductsResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    mContext,
                                    mContext.getString(R.string.error),
                                    NetworkHelper.getErrorMessage(mContext, error),
                                    false,
                                    mContext.getString(R.string.ok),
                                    { dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                    }, null, null)
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        mContext.mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })

    }
}