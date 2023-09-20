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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentForgotPasswordDialogBinding
import com.webkul.mobikul.handlers.ForgotPasswordDialogHandler
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMAIL
import com.webkul.mobikul.models.user.SignInForgetPasswordData

class ForgotPasswordDialogFragment : DialogFragment() {

    lateinit var mContentViewBinding: FragmentForgotPasswordDialogBinding

    companion object {
        fun newInstance(email: String): ForgotPasswordDialogFragment {
            val forgotPasswordDialogFragment = ForgotPasswordDialogFragment()
            val args = Bundle()
            args.putString(BundleKeysHelper.BUNDLE_KEY_EMAIL, email)
            forgotPasswordDialogFragment.arguments = args
            return forgotPasswordDialogFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password_dialog, container, false)
        isCancelable = false
        mContentViewBinding.loading = false
        mContentViewBinding.data = SignInForgetPasswordData(requireArguments().getString(BUNDLE_KEY_EMAIL))
        mContentViewBinding.handler = ForgotPasswordDialogHandler(this)
        return mContentViewBinding.root
    }
}
