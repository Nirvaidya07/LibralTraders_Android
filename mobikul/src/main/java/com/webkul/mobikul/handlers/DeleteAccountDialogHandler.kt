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
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.fragments.DeleteAccountDialogFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressDetailsData
import com.webkul.mobikul.models.user.LoginFormModel
import com.webkul.mobikul.models.user.LoginResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DeleteAccountDialogHandler(var mDeleteAccountDialogFragment: DeleteAccountDialogFragment) {

    fun onClickDeleteAccountPositiveBtn(loginFormModel: LoginFormModel) {
        if (!mDeleteAccountDialogFragment.mContentViewBinding.loading!!) {
            when {
                loginFormModel.password.isBlank() -> {
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.error = mDeleteAccountDialogFragment.getString(R.string.password) + " " + mDeleteAccountDialogFragment.getString(R.string.is_required)
                    Utils.showShakeError(mDeleteAccountDialogFragment.requireContext(), mDeleteAccountDialogFragment.mContentViewBinding.passwordTil)
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.requestFocus()
                }
                loginFormModel.password.trim().length < 6 -> {
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.error = mDeleteAccountDialogFragment.getString(R.string.enter_a_valid) + " " + mDeleteAccountDialogFragment.getString(R.string.password)
                    Utils.showShakeError(mDeleteAccountDialogFragment.requireContext(), mDeleteAccountDialogFragment.mContentViewBinding.passwordTil)
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.requestFocus()
                }
                else -> {
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.isErrorEnabled = false
                    mDeleteAccountDialogFragment.mContentViewBinding.passwordTil.error = null
                    confirmPassword(loginFormModel)
                }
            }
        }
    }

    private fun confirmPassword(loginFormModel: LoginFormModel) {
        Utils.hideKeyboard(mDeleteAccountDialogFragment.mContentViewBinding.passwordTil)
        mDeleteAccountDialogFragment.mContentViewBinding.loading = true
        loginFormModel.email = AppSharedPref.getCustomerEmail(mDeleteAccountDialogFragment.requireContext())
        ApiConnection.login(mDeleteAccountDialogFragment.requireContext(), loginFormModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<LoginResponseModel>(mDeleteAccountDialogFragment.requireContext(), true) {
                    override fun onNext(loginResponseModel: LoginResponseModel) {
                        super.onNext(loginResponseModel)
                        mDeleteAccountDialogFragment.mContentViewBinding.loading = false
                        if (loginResponseModel.success) {
                            deleteAccount()
                        } else {
                            showToast(mDeleteAccountDialogFragment.requireContext(), loginResponseModel.message)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mDeleteAccountDialogFragment.mContentViewBinding.loading = false
                        onErrorResponse(e, loginFormModel)
                    }
                })
    }

    private fun onErrorResponse(error: Throwable, loginFormModel: LoginFormModel) {
        AlertDialogHelper.showNewCustomDialog(
                mDeleteAccountDialogFragment.context as BaseActivity,
                mDeleteAccountDialogFragment.getString(R.string.error),
                NetworkHelper.getErrorMessage(mDeleteAccountDialogFragment.requireContext(), error),
                false,
                mDeleteAccountDialogFragment.getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    confirmPassword(loginFormModel)
                }
                , mDeleteAccountDialogFragment.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun deleteAccount() {
        mDeleteAccountDialogFragment.mContentViewBinding.loading = true
        ApiConnection.deleteAccount(mDeleteAccountDialogFragment.requireContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mDeleteAccountDialogFragment.requireContext(), true) {
                    override fun onNext(deleteAccountResponseModel: BaseModel) {
                        super.onNext(deleteAccountResponseModel)
                        mDeleteAccountDialogFragment.mContentViewBinding.loading = false
                        showToast(mDeleteAccountDialogFragment.requireContext(), deleteAccountResponseModel.message)
                        if (deleteAccountResponseModel.success) {
                            mDeleteAccountDialogFragment.dismissAllowingStateLoss()
                            if(BaseActivity.mDataBaseHandler!=null)
                                BaseActivity.mDataBaseHandler.clearRecentlyViewedProductsTableData()
                            val customerSharedPrefEditor = AppSharedPref.getSharedPreferenceEditor(
                                    mDeleteAccountDialogFragment.requireContext(),
                                    AppSharedPref.CUSTOMER_PREF
                            )
                            customerSharedPrefEditor.clear()
                            customerSharedPrefEditor.apply()
                            AppSharedPref.setCustomerCachedNewAddress(mDeleteAccountDialogFragment.requireContext(), AddressDetailsData())
                            showToast(mDeleteAccountDialogFragment.requireContext(), mDeleteAccountDialogFragment.requireContext().getString(R.string.account_deleted_message))
                            val intent = Intent(mDeleteAccountDialogFragment.requireContext(), HomeActivity::class.java)
                            intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            mDeleteAccountDialogFragment.requireContext().startActivity(intent)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mDeleteAccountDialogFragment.mContentViewBinding.loading = false
                        showToast(mDeleteAccountDialogFragment.requireContext(), mDeleteAccountDialogFragment.getString(R.string.something_went_wrong))
                    }
                })
    }

    fun onClickDeleteAccountNegativeBtn() {
        if (!mDeleteAccountDialogFragment.mContentViewBinding.loading!!)
            mDeleteAccountDialogFragment.dismiss()
    }
}