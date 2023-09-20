package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.AdditionalAddressRvAdapter
import com.webkul.mobikul.databinding.ActivityAddressBookBinding
import com.webkul.mobikul.handlers.AddressBookActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ConstantsHelper.RC_ADD_EDIT_ADDRESS
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressBookResponseModel
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

class AddressBookActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityAddressBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_address_book)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
        checkAndLoadLocalData()
    }

      override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_address_book)
         super.initSupportActionBar()
    }

    fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getAddressBookData" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this))
        ApiConnection.getAddressBookData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddressBookResponseModel>(this, true) {
                    override fun onNext(addressBookResponseModel: AddressBookResponseModel) {
                        super.onNext(addressBookResponseModel)
                        mContentViewBinding.loading = false
                        if (addressBookResponseModel.success) {
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
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            val addressBookResponseModel: AddressBookResponseModel =mObjectMapper.readValue(response, AddressBookResponseModel::class.java)
                            if (addressBookResponseModel.addressCount > 0) {
                                onSuccessfulResponse(addressBookResponseModel)
                            }
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

    private fun onSuccessfulResponse(addressBookResponseModel: AddressBookResponseModel) {
        mContentViewBinding.data = addressBookResponseModel
        setupAdditionalAddressRv(addressBookResponseModel)
        mContentViewBinding.animationView.playAnimation()
        mContentViewBinding.handler = AddressBookActivityHandler(this)
    }

    private fun setupAdditionalAddressRv(addressBookResponseModel: AddressBookResponseModel) {
        mContentViewBinding.additionalAddressRv.isNestedScrollingEnabled = false
        mContentViewBinding.additionalAddressRv.adapter = AdditionalAddressRvAdapter(this, addressBookResponseModel.additionalAddress)
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
                            finish()
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
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }
                    , getString(R.string.dismiss)
                    , { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == RC_ADD_EDIT_ADDRESS) {
                callApi()
                setResult(AppCompatActivity.RESULT_OK, Intent())
            }
        }
    }
}