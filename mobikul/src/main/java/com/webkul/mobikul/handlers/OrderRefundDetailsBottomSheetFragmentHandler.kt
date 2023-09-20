package com.webkul.mobikul.handlers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.OrderRefundDetailsBottomSheetFragment
import com.webkul.mobikul.helpers.ConstantsHelper
import com.webkul.mobikul.models.user.RefundDetailsData

class OrderRefundDetailsBottomSheetFragmentHandler(val mFragmentContext: OrderRefundDetailsBottomSheetFragment) {

    fun onClickCancelBtn() {
        mFragmentContext.dismiss()
    }

    fun onClickSaveInvoice(data: RefundDetailsData, invoiceId: String) {
        if (mFragmentContext.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mFragmentContext.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mFragmentContext.requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //  PDFHelper.instanse.generateInvoice(mFragmentContext.requireContext(), data, invoiceId)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                (mFragmentContext.context as BaseActivity).requestPermissions(permissions, ConstantsHelper.RC_WRITE_TO_EXTERNAL_STORAGE)
            }
        }
    }
}