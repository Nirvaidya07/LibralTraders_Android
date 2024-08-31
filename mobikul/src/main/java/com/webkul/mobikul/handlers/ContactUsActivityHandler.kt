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
import com.libraltraders.android.R
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ContactUsActivityHandler(var mContext: ContactUsActivity) {
    fun onClickSubmitBtn() {
        if (isFormValidated()) {
            Utils.hideKeyboard(mContext)
            mContext.mContentViewBinding.loading = true
            ApiConnection.postContactUs(mContext, mContext.mContentViewBinding.name.text.toString().trim(), mContext.mContentViewBinding.email.text.toString().trim(), mContext.mContentViewBinding.phone.text.toString().trim(), mContext.mContentViewBinding.msg.text.toString().trim())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                        override fun onNext(postContactUsResponse: BaseModel) {
                            super.onNext(postContactUsResponse)
                            mContext.mContentViewBinding.loading = false
                            if (postContactUsResponse.success) {
                                onSuccessfulResponse(postContactUsResponse)
                            } else {
                                onFailureResponse(postContactUsResponse)
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

    private fun onSuccessfulResponse(postContactUsResponse: BaseModel) {
        ToastHelper.showToast(mContext, postContactUsResponse.message)
        mContext.finish()
    }

    private fun onFailureResponse(postContactUsResponse: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                postContactUsResponse.message,
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

    fun isFormValidated(): Boolean {
        var isFormValidated = true
        if (mContext.mContentViewBinding.msg.text!!.trim().isBlank()) {
            isFormValidated = false
            mContext.mContentViewBinding.msgTil.error = mContext.getString(R.string.comment) + " " + mContext.getString(R.string.is_required)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.msgTil)
            mContext.mContentViewBinding.msg.requestFocus()
        } else {
            mContext.mContentViewBinding.msgTil.isErrorEnabled = false
            mContext.mContentViewBinding.msgTil.error = null
        }

        if (mContext.mContentViewBinding.phone.text!!.trim().isNotBlank()) {
            if (!android.util.Patterns.PHONE.matcher(mContext.mContentViewBinding.phone.text!!.trim()).matches()) {
                isFormValidated = false
                mContext.mContentViewBinding.phoneTil.error = mContext.getString(R.string.enter_a_valid) + " " + mContext.getString(R.string.mobile)
                Utils.showShakeError(mContext, mContext.mContentViewBinding.phoneTil)
                mContext.mContentViewBinding.phone.requestFocus()
            } else {
                mContext.mContentViewBinding.phoneTil.isErrorEnabled = false
                mContext.mContentViewBinding.phoneTil.error = null
            }
        }

        if (mContext.mContentViewBinding.email.text!!.trim().isBlank()) {
            isFormValidated = false
            mContext.mContentViewBinding.emailTil.error = mContext.getString(R.string.email) + " " + mContext.getString(R.string.is_required)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.emailTil)
            mContext.mContentViewBinding.email.requestFocus()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mContext.mContentViewBinding.email.text!!.trim()).matches()) {
            isFormValidated = false
            mContext.mContentViewBinding.emailTil.error = mContext.getString(R.string.enter_a_valid) + " " + mContext.getString(R.string.email)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.emailTil)
            mContext.mContentViewBinding.email.requestFocus()
        } else {
            mContext.mContentViewBinding.emailTil.isErrorEnabled = false
            mContext.mContentViewBinding.emailTil.error = null
        }

        if (mContext.mContentViewBinding.name.text!!.trim().isBlank()) {
            isFormValidated = false
            mContext.mContentViewBinding.nameTil.error = mContext.getString(R.string.name) + " " + mContext.getString(R.string.is_required)
            Utils.showShakeError(mContext, mContext.mContentViewBinding.nameTil)
            mContext.mContentViewBinding.name.requestFocus()
        } else {
            mContext.mContentViewBinding.nameTil.isErrorEnabled = false
            mContext.mContentViewBinding.nameTil.error = null
        }

        return isFormValidated
    }
}