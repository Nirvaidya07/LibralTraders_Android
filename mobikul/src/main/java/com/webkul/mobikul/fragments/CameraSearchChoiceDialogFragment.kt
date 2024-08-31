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

package com.webkul.mobikul.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.libraltraders.android.R
import com.libraltraders.android.databinding.FragmentCameraSearchChoiceDialogBinding
import com.webkul.mobikul.handlers.CameraSearchBottomSheetFragmentHandler

class CameraSearchChoiceDialogFragment : DialogFragment() {

    lateinit var mContentViewBinding: FragmentCameraSearchChoiceDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_search_choice_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        startInitialization()
        return mContentViewBinding.root
    }

    private fun startInitialization() {
        mContentViewBinding.handler = CameraSearchBottomSheetFragmentHandler(this)
    }
}