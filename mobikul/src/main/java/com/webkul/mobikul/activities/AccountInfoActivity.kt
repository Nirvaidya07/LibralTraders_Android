package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityAccountInfoBinding
import com.webkul.mobikul.handlers.AccountInfoActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AccountInfoResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*

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

class AccountInfoActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityAccountInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_info)
        startInitialization()
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_account_info)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getAccountInfo" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this))
        ApiConnection.getAccountInfo(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AccountInfoResponseModel>(this, true) {
                    override fun onNext(accountInfoResponseModel: AccountInfoResponseModel) {
                        super.onNext(accountInfoResponseModel)
                        mContentViewBinding.loading = false
                        if (accountInfoResponseModel.success) {
                            onSuccessfulResponse(accountInfoResponseModel)
                        } else {
                            onFailureResponse(accountInfoResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(accountInfoResponseModel: AccountInfoResponseModel) {
        mContentViewBinding.data = accountInfoResponseModel

        setUpPrefix()
        setUpSuffix()
        setUpGender()
        mContentViewBinding.handler = AccountInfoActivityHandler(this)
    }

    private fun setUpPrefix() {
        if (mContentViewBinding.data!!.isPrefixVisible && mContentViewBinding.data!!.prefixHasOptions) {
            val prefixSpinnerData = ArrayList<String>()
            prefixSpinnerData.add("")
            for (prefixIterator in 0 until mContentViewBinding.data!!.prefixOptions.size) {
                prefixSpinnerData.add(mContentViewBinding.data!!.prefixOptions[prefixIterator])
            }

            mContentViewBinding.prefixSp.adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_item, prefixSpinnerData)
            mContentViewBinding.prefixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mContentViewBinding.data!!.prefixValue = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            for ((prefixIndex, prefixValue) in prefixSpinnerData.withIndex()) {
                if (mContentViewBinding.data!!.prefixValue == prefixValue) {
                    mContentViewBinding.prefixSp.setSelection(prefixIndex)
                }
            }
        }
    }

    private fun setUpSuffix() {
        if (mContentViewBinding.data!!.isSuffixVisible && mContentViewBinding.data!!.suffixHasOptions) {
            val suffixSpinnerData = ArrayList<String>()
            suffixSpinnerData.add("")
            for (prefixIterator in 0 until mContentViewBinding.data!!.suffixOptions.size) {
                suffixSpinnerData.add(mContentViewBinding.data!!.suffixOptions[prefixIterator])
            }

            mContentViewBinding.suffixSp.adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_item, suffixSpinnerData)
            mContentViewBinding.suffixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mContentViewBinding.data!!.suffixValue = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            for ((suffixIndex, suffixValue) in suffixSpinnerData.withIndex()) {
                if (mContentViewBinding.data!!.suffixValue == suffixValue) {
                    mContentViewBinding.suffixSp.setSelection(suffixIndex)
                }
            }
        }
    }

    private fun setUpGender() {
        if (mContentViewBinding.data!!.isGenderVisible) {
            val genderSpinnerData = ArrayList<String>()
            genderSpinnerData.add("")
            genderSpinnerData.add(resources.getString(R.string.male))
            genderSpinnerData.add(resources.getString(R.string.female))
            genderSpinnerData.add(resources.getString(R.string.not_specified))

            mContentViewBinding.genderSp.adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_item, genderSpinnerData)
        }
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
                        DialogInterface.OnClickListener{ dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            callApi()
                        }, "", null)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this@AccountInfoActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@AccountInfoActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@AccountInfoActivity, error),
                    false,
                    getString(R.string.try_again),
                    DialogInterface.OnClickListener{ dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }, getString(R.string.dismiss),DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
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
                            onSuccessfulResponse(mObjectMapper.readValue(response, AccountInfoResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@AccountInfoActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@AccountInfoActivity, error),
                                    false,
                                    getString(R.string.try_again),
                                    DialogInterface.OnClickListener{ dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                        callApi()
                                    }, getString(R.string.dismiss),DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
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