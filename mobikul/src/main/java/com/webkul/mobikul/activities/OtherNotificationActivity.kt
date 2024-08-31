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
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityOtherNotificationBinding
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.extra.OtherNotificationResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OtherNotificationActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityOtherNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_other_notification)
        startInitialization()
        initSupportActionBar()
    }

    private fun startInitialization() {
        callApi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun callApi() {
        mHashIdentifier = Utils.getMd5String("getOtherNotificationData" + intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_NOTIFICATION_ID))
        ApiConnection.getOtherNotificationData(this, mHashIdentifier, intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_NOTIFICATION_ID)!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<OtherNotificationResponseModel>(this, true) {
                    override fun onNext(otherNotificationResponseModel: OtherNotificationResponseModel) {
                        super.onNext(otherNotificationResponseModel)
                        if (otherNotificationResponseModel.success) {
                            supportActionBar?.title = otherNotificationResponseModel.title
                            mContentViewBinding.data = otherNotificationResponseModel
                        } else {
                            onFailureResponse(otherNotificationResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        onErrorResponse(e)
                    }
                })
    }

    private fun onFailureResponse(otherNotificationResponseModel: OtherNotificationResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                otherNotificationResponseModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        })
    }

    private fun onErrorResponse(error: Throwable) {

        if ((!NetworkHelper.isNetworkAvailable(this@OtherNotificationActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@OtherNotificationActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@OtherNotificationActivity, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            mContentViewBinding.data =mObjectMapper.readValue(response, OtherNotificationResponseModel::class.java)
                            supportActionBar?.title = mContentViewBinding.data?.title?:""
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@OtherNotificationActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@OtherNotificationActivity, error),
                                    false,
                                    getString(R.string.try_again),
                                    { dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                        callApi()
                                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                                finish()
                            })
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
}
