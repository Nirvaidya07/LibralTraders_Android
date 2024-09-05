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

package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.content.Intent
import com.libraltraders.android.R
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.activities.OrdersAndReturnsActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.user.GuestOrderDetailsResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OrdersAndReturnsActivityHandler(val mContext: OrdersAndReturnsActivity) {

    fun onClickSubmitBtn() {
        if (isFormValidated()) {
            mContext.mContentViewBinding.loading = true
            ApiConnection.getGuestOrderDetails(mContext, mContext.mOrderType, mContext.mContentViewBinding.orderId.text!!.toString(), mContext.mContentViewBinding.lastName.text!!.toString(), mContext.mContentViewBinding.email.text!!.toString(), mContext.mContentViewBinding.zip.text!!.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<GuestOrderDetailsResponseModel>(mContext, true) {
                        override fun onNext(guestOrderDetailsResponse: GuestOrderDetailsResponseModel) {
                            super.onNext(guestOrderDetailsResponse)
                            mContext.mContentViewBinding.loading = false
                            if (guestOrderDetailsResponse.success) {
                                onSuccessfulResponse(guestOrderDetailsResponse)
                            } else {
                                onFailureResponse(guestOrderDetailsResponse)
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

    private fun onSuccessfulResponse(guestOrderDetailsResponse: GuestOrderDetailsResponseModel) {
        val intent = Intent(mContext, OrderDetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_INCREMENT_ID, mContext.mContentViewBinding.orderId.text!!.toString())
        mContext.startActivity(intent)
        mContext.finish()
    }

    private fun onFailureResponse(guestOrderDetailsResponse: GuestOrderDetailsResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                guestOrderDetailsResponse.message,
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun isFormValidated(): Boolean {
        var isFormValidated = true
        if (mContext.mOrderType == "zip") {
            if (mContext.mContentViewBinding.zip.text!!.toString().isBlank()) {
                isFormValidated = false
                mContext.mContentViewBinding.zipTil.error = mContext.getString(R.string.zip_code) + " " + mContext.getString(R.string.is_required)
                Utils.showShakeError(mContext, mContext.mContentViewBinding.zipTil)
                mContext.mContentViewBinding.zip.requestFocus()
            } else {
                mContext.mContentViewBinding.zipTil.isErrorEnabled = false
                mContext.mContentViewBinding.zipTil.error = null
            }
        } else {
            if (mContext.mContentViewBinding.email.text!!.toString().isBlank()) {
                isFormValidated = false
                mContext.mContentViewBinding.emailTil.error = mContext.getString(R.string.email) + " " + mContext.getString(R.string.is_required)
                Utils.showShakeError(mContext, mContext.mContentViewBinding.emailTil)
                mContext.mContentViewBinding.email.requestFocus()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mContext.mContentViewBinding.email.text!!.toString().trim()).matches()) {
                isFormValidated = false
                mContext.mContentViewBinding.emailTil.error = mContext.getString(R.string.enter_a_valid) + " " + mContext.getString(R.string.email)
                Utils.showShakeError(mContext, mContext.mContentViewBinding.emailTil)
                mContext.mContentViewBinding.email.requestFocus()
            } else {
                mContext.mContentViewBinding.emailTil.isErrorEnabled = false
                mContext.mContentViewBinding.emailTil.error = null
            }
        }

        if (mContext.mContentViewBinding.lastName.getText()!!.toString().trim().isEmpty()) {
            isFormValidated = false
            mContext.mContentViewBinding.lastNameTil.error = mContext.getString(R.string.last_name) + " " + mContext.getString(R.string.is_required)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.lastNameTil)
            mContext.mContentViewBinding.lastName.requestFocus()
        } else {
            mContext.mContentViewBinding.lastNameTil.isErrorEnabled = false
            mContext.mContentViewBinding.lastNameTil.error = null
        }

        if (mContext.mContentViewBinding.orderId.getText()!!.toString().trim().isEmpty()) {
            isFormValidated = false
            mContext.mContentViewBinding.orderIdTil.error = mContext.getString(R.string.order_id) + " " + mContext.getString(R.string.is_required)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.orderIdTil)
            mContext.mContentViewBinding.orderId.requestFocus()
        } else {
            mContext.mContentViewBinding.orderIdTil.isErrorEnabled = false
            mContext.mContentViewBinding.orderIdTil.error = null
        }
        return isFormValidated
    }
}