package com.webkul.mobikul.handlers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
//import com.webkul.mlkit.activities.CameraSearchActivity
import com.webkul.mlkit.activities.CameraSearchActivity
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.fragments.CameraSearchChoiceDialogFragment
import com.webkul.mobikul.helpers.BundleKeysHelper.CAMERA_SELECTED_MODEL
import com.webkul.mobikul.helpers.ConstantsHelper.RC_CAMERA_SEARCH

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

class CameraSearchBottomSheetFragmentHandler(var mFragmentContext: CameraSearchChoiceDialogFragment) {

    fun onClickScanAsText() {
        (mFragmentContext.context as AppCompatActivity).startActivityForResult(Intent(mFragmentContext.activity as BaseActivity, CameraSearchActivity::class.java).putExtra(CAMERA_SELECTED_MODEL, CameraSearchActivity.TEXT_RECOGNITION), RC_CAMERA_SEARCH)
        mFragmentContext.dismiss()
    }

    fun onClickScanAsProduct() {
        (mFragmentContext.context as AppCompatActivity).startActivityForResult(Intent(mFragmentContext.activity as BaseActivity, CameraSearchActivity::class.java).putExtra(CAMERA_SELECTED_MODEL, CameraSearchActivity.IMAGE_LABELING), RC_CAMERA_SEARCH)
        mFragmentContext.dismiss()
    }
}