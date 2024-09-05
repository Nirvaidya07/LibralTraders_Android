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

import com.libraltraders.android.R
import com.webkul.mobikul.fragments.ForgotPasswordDialogFragment
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForgotPasswordDialogHandler(var mForgotPasswordDialogFragment: ForgotPasswordDialogFragment) {

    fun onClickForgotPasswordPositiveBtn(email: String) {
        if (!mForgotPasswordDialogFragment.mContentViewBinding.loading!!) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(mForgotPasswordDialogFragment.requireContext(), mForgotPasswordDialogFragment.resources.getString(R.string.enter_valid_email))
                return
            }

            mForgotPasswordDialogFragment.mContentViewBinding.loading = true
            ApiConnection.forgotPassword(mForgotPasswordDialogFragment.requireContext(), email)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mForgotPasswordDialogFragment.requireContext(), true) {
                        override fun onNext(forgotPasswordResponseModel: BaseModel) {
                            super.onNext(forgotPasswordResponseModel)
                            mForgotPasswordDialogFragment.mContentViewBinding.loading = false
                            showToast(mForgotPasswordDialogFragment.requireContext(), forgotPasswordResponseModel.message)
                            if (forgotPasswordResponseModel.success) {
                                mForgotPasswordDialogFragment.dismissAllowingStateLoss()
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mForgotPasswordDialogFragment.mContentViewBinding.loading = false
                            showToast(mForgotPasswordDialogFragment.requireContext(), mForgotPasswordDialogFragment.getString(R.string.something_went_wrong))
                        }
                    })
        }
    }

    fun onClickForgotPasswordNegativeBtn() {
        if (!mForgotPasswordDialogFragment.mContentViewBinding.loading!!)
            mForgotPasswordDialogFragment.dismiss()
    }
}