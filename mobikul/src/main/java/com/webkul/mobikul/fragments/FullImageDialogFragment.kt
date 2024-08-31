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
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.libraltraders.android.R
import com.libraltraders.android.databinding.FragmentFullImageDialogBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IMAGE_URL
import com.webkul.mobikul.helpers.ImageHelper


class FullImageDialogFragment : DialogFragment() {

    lateinit var mContentViewBinding: FragmentFullImageDialogBinding

    companion object {
        fun newInstance(imageUrl: String): FullImageDialogFragment {
            val fullImageDialogFragment = FullImageDialogFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_IMAGE_URL, imageUrl)
            fullImageDialogFragment.arguments = args
            return fullImageDialogFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_full_image_dialog, container, false)
        try {
            isCancelable = true
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImageHelper.load(mContentViewBinding.image, requireArguments().getString(BUNDLE_KEY_IMAGE_URL), null)
    }
}