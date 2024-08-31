package com.webkul.mobikul.handlers

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.libraltraders.android.R
import com.webkul.mobikul.activities.AccountInfoActivity
import com.webkul.mobikul.fragments.DeleteAccountDialogFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.AppSharedPref.Companion.CUSTOMER_PREF
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_EMAIL
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_CUSTOMER_NAME
import com.webkul.mobikul.models.user.AccountInfoResponseModel
import com.webkul.mobikul.models.user.SaveAccountInfoResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
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

class AccountInfoActivityHandler(private val mContext: AccountInfoActivity) {

    fun onClickDatePicker(v: View, selectedDate: String, dateFormat: String) {
        val dobCalendar = Calendar.getInstance()
        var selectedYear = dobCalendar.get(Calendar.YEAR)
        var selectedMonth = dobCalendar.get(Calendar.MONTH)
        var selectedDay = dobCalendar.get(Calendar.DAY_OF_MONTH)
        if (selectedDate.isNotBlank()) {
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            val d = sdf.parse(selectedDate)
            dobCalendar.time = d
            selectedYear = dobCalendar.get(Calendar.YEAR)
            selectedMonth = dobCalendar.get(Calendar.MONTH)
            selectedDay = dobCalendar.get(Calendar.DAY_OF_MONTH)
        }
        val dateOfBirth = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dobCalendar.set(Calendar.YEAR, year)
            dobCalendar.set(Calendar.MONTH, month)
            dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dob = SimpleDateFormat(dateFormat, Locale.US)
            (v as TextInputEditText).setText(dob.format(dobCalendar.time))
        }

        val datePickerDialog = DatePickerDialog(mContext, R.style.AlertDialogTheme, dateOfBirth, selectedYear, selectedMonth, selectedDay)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    fun onClickSave(accountInfoResponseModel: AccountInfoResponseModel) {
        if (accountInfoResponseModel.isFormValidated(mContext)) {
            mContext.mContentViewBinding.loading = true
            Utils.hideKeyboard(mContext)
            ApiConnection.saveAccountInfo(mContext, accountInfoResponseModel)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<SaveAccountInfoResponseModel>(mContext, true) {
                        override fun onNext(saveAccountInfoResponse: SaveAccountInfoResponseModel) {
                            super.onNext(saveAccountInfoResponse)
                            mContext.mContentViewBinding.loading = false
                            ToastHelper.showToast(mContext, saveAccountInfoResponse.message)
                            if (saveAccountInfoResponse.success) {
                                val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(mContext, CUSTOMER_PREF)
                                if (saveAccountInfoResponse.customerName.isNotBlank())
                                    customerDataSharedPref.putString(KEY_CUSTOMER_NAME, saveAccountInfoResponse.customerName)
                                if (mContext.mContentViewBinding.changeEmail.isChecked)
                                    customerDataSharedPref.putString(KEY_CUSTOMER_EMAIL, accountInfoResponseModel.email)
                                customerDataSharedPref.apply()
                                mContext.setResult(AppCompatActivity.RESULT_OK, Intent())
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContext.mContentViewBinding.loading = false
                            onErrorResponse(e, accountInfoResponseModel)
                        }
                    })
        }
    }

    private fun onErrorResponse(error: Throwable, accountInfoResponseModel: AccountInfoResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mContext, error),
                false,
                mContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickSave(accountInfoResponseModel)
                }
                , mContext.getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun onClickDeleteAccount() {
        AlertDialogHelper.showNewCustomDialog(
                mContext,
                mContext.getString(R.string.delete_account),
                mContext.getString(R.string.delete_account_warning),
                false,
                mContext.getString(R.string.delete),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    DeleteAccountDialogFragment.newInstance().show((mContext as AccountInfoActivity).supportFragmentManager, DeleteAccountDialogFragment::class.java.simpleName)
                }
                , mContext.getString(R.string.cancel)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }
}