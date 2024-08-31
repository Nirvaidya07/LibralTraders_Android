package com.webkul.mobikul.handlers

import android.content.DialogInterface
import android.view.View
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.DeliveryboyMakeReviewFragment
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.DeliveryboyMakeReviewData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Webkul Software.
 *
 * @author Webkul
 * @category Mobikul
 * @package com.webkul.mobikulmp.fragments
 * @copyright Copyright (c) 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html
 * @date 26/07/19
 */
class DeliveryboyMakeReviewHandler(private val mFragmentContext: DeliveryboyMakeReviewFragment, private val deliveryboyId: String?, private val customerId: String?) {

    fun onClickCancelBtn() {
        mFragmentContext.dismiss()
    }

    fun onClickSaveReview(view: View, data: DeliveryboyMakeReviewData) {
        if (data.isFormValidated(mFragmentContext)) {
            Utils.hideKeyboard(view)
            callApi(data)
        }
    }

    private fun callApi(data: DeliveryboyMakeReviewData) {
        mFragmentContext.mContentViewBinding.loading = true
        ApiConnection.saveDeliveryboyReview(
                mFragmentContext.requireContext(),
                deliveryboyId,
                customerId,
                data.nickName,
                data.summary,
                data.comment,
                data.rating)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                    override fun onNext(t: BaseModel) {
                        super.onNext(t)
                        mFragmentContext.mContentViewBinding.loading = false
                        if (t.success) {
                            mFragmentContext.context?.let { ToastHelper.showToast(it, t.message) }
                            mFragmentContext.dismiss()
                        } else {
                            onFailureResponse(t)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mFragmentContext.mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    fun onFailureResponse(response: Any) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.activity as BaseActivity,
                mFragmentContext.getString(R.string.error),
                (response as BaseModel).message,
                false,
                mFragmentContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mFragmentContext.dismiss()
                }
                , ""
                , null)
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.requireActivity() as BaseActivity,
                mFragmentContext.getString(R.string.oops),
                NetworkHelper.getErrorMessage(mFragmentContext.context, error),
                false,
                mFragmentContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi(mFragmentContext.mContentViewBinding.data!!)
                }
                , mFragmentContext.getString(com.webkul.mobikul.R.string.close)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })

    }

}
