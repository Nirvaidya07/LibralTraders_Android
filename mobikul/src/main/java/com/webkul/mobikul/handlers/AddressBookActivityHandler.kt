package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.content.Intent
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.AddEditAddressActivity
import com.webkul.mobikul.activities.AddressBookActivity
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.ConstantsHelper.RC_ADD_EDIT_ADDRESS
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

class AddressBookActivityHandler(val mContext: AddressBookActivity) {

    fun onClickAddress(addressId: String) {
        val intent = Intent(mContext, AddEditAddressActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_ADDRESS_ID, addressId)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_ADDRESS_COUNT, mContext.mContentViewBinding.data!!.addressCount)
        mContext.startActivityForResult(intent, RC_ADD_EDIT_ADDRESS)
    }

    fun onClickDeleteAddress(addressId: String) {
        AlertDialogHelper.showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.remove_item),
                mContext.getString(R.string.remove_address_item_msg),
                false,
                mContext.getString(R.string.yes_remove_it),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mContext.mContentViewBinding.loading = true
                    ApiConnection.deleteAddress(mContext, addressId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : ApiCustomCallback<BaseModel>(mContext, false) {
                                override fun onNext(deleteAddress: BaseModel) {
                                    super.onNext(deleteAddress)
                                    mContext.mContentViewBinding.loading = false
                                    ToastHelper.showToast(mContext, deleteAddress.message)
                                    if (deleteAddress.success) {
                                        mContext.callApi()
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    mContext.mContentViewBinding.loading = false
                                    ToastHelper.showToast(mContext, mContext.getString(R.string.something_went_wrong))
                                }
                            })
                },
                mContext.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }
}