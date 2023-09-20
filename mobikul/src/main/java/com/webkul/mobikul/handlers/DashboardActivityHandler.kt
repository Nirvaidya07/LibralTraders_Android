/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.webkul.mobikul.activities.AccountInfoActivity
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.DashboardActivity
import com.webkul.mobikul.fragments.FullImageDialogFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.ConstantsHelper
import com.webkul.mobikul.helpers.MobikulApplication


class DashboardActivityHandler(private val mContext: DashboardActivity) {

    fun onClickBannerImage() {
        if (AppSharedPref.isLoggedIn(mContext)) {
            mContext.mIsUpdatingProfilePic = false
            selectImage()
        } else {
            mContext.startActivityForResult((mContext.application as MobikulApplication).getLoginAndSignUpActivity(mContext), ConstantsHelper.RC_LOGIN)
        }
    }

    fun onClickProfileImage() {
        if (AppSharedPref.getCustomerImageUrl(mContext).isNotBlank())
            FullImageDialogFragment.newInstance(AppSharedPref.getCustomerImageUrl(mContext)).show((mContext as BaseActivity).supportFragmentManager, FullImageDialogFragment::class.java.simpleName)
    }

    fun onClickProfileImageEdit() {
        if (AppSharedPref.isLoggedIn(mContext)) {
            mContext.mIsUpdatingProfilePic = true
            selectImage()
        }
    }

    fun selectImage() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            CropImage.startPickImageActivity(mContext)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                mContext.requestPermissions(permissions, ConstantsHelper.RC_PICK_IMAGE)
            }
        }
    }

    fun onClickEditInfo() {
        mContext.startActivityForResult(Intent(mContext, AccountInfoActivity::class.java), ConstantsHelper.RC_LOGIN)
    }
}