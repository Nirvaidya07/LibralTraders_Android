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
import android.view.View
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.ProductCreateReviewRatingDataRvAdapter
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.product.CreateReviewData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ProductCreateReviewBottomSheetFragmentHandler(val mFragmentContext: ProductCreateReviewBottomSheetFragment) {

    fun onClickCancelBtn() {
        Utils.hideKeyboard(mFragmentContext.mContentViewBinding.nicknameTil)
        mFragmentContext.dismiss()
    }

    fun onClickSubmitReview(view: View, createReviewData: CreateReviewData, productId: String) {
        if ((mFragmentContext.mContentViewBinding.ratingDataRv.adapter as ProductCreateReviewRatingDataRvAdapter).isRatingFilled() && createReviewData.isFormValidated(mFragmentContext)) {
            Utils.hideKeyboard(mFragmentContext.mContentViewBinding.nicknameTil)
            mFragmentContext.mContentViewBinding.loading = true
            val mRatingObj = (mFragmentContext.mContentViewBinding.ratingDataRv.adapter as ProductCreateReviewRatingDataRvAdapter).getRatingData()
            ApiConnection.saveReview(mFragmentContext.requireContext(), productId, createReviewData.summary, createReviewData.comment, createReviewData.nickName, mRatingObj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                        override fun onNext(saveReviewResponseModel: BaseModel) {
                            super.onNext(saveReviewResponseModel)
                            mFragmentContext.mContentViewBinding.loading = false
                            if (saveReviewResponseModel.success) {
                                onSuccessfulResponse(saveReviewResponseModel)
                            } else {
                                onFailureResponse(saveReviewResponseModel)
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mFragmentContext.mContentViewBinding.loading = false
                            onErrorResponse(e)
                        }
                    })
        }
    }

    private fun onSuccessfulResponse(saveReviewResponseModel: BaseModel) {
        ToastHelper.showToast(mFragmentContext.requireContext(), saveReviewResponseModel.message)
        clearRatingData()
        mFragmentContext.dismiss()
    }

    private fun clearRatingData() {
        mFragmentContext.mRatingFormData.forEach { ratingFormData ->
            ratingFormData.ratingValue = 0f
        }
    }

    private fun onFailureResponse(saveReviewResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                saveReviewResponseModel.message,
                false,
                mFragmentContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.context, error),
                false,
                mFragmentContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                , null
                , null)
    }
}