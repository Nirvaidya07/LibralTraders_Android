package com.webkul.mobikul.handlers

import android.content.DialogInterface
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.WishListSharingActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.NetworkHelper
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

class WishListSharingActivityHandler(val mContext: WishListSharingActivity) {

    fun onClickShareWishList() {
        if (mContext.mContentViewBinding.data!!.isFormValidated(mContext)) {
            mContext.mContentViewBinding.loading = true
            ApiConnection.shareWishList(mContext, mContext.mContentViewBinding.data!!.emails, mContext.mContentViewBinding.data!!.message)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                        override fun onNext(shareWishListResponseModel: BaseModel) {
                            super.onNext(shareWishListResponseModel)
                            mContext.mContentViewBinding.loading = false
                            if (shareWishListResponseModel.success) {
                                onSuccessfulResponse(shareWishListResponseModel)
                            } else {
                                onFailureResponse(shareWishListResponseModel)
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

    private fun onSuccessfulResponse(shareWishListResponseModel: BaseModel) {
        ToastHelper.showToast(mContext, shareWishListResponseModel.message)
        mContext.finish()
    }

    private fun onFailureResponse(shareWishListResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                shareWishListResponseModel.message,
                false,
                mContext.getString(R.string.dismiss),
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
                mContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickShareWishList()
                }
                , mContext.getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            mContext.finish()
        })
    }
}