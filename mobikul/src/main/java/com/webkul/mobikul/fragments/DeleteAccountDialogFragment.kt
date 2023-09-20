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
import com.webkul.mobikul.databinding.FragmentDeleteAccountDialogBinding
import com.webkul.mobikul.handlers.DeleteAccountDialogHandler
import com.webkul.mobikul.models.user.LoginFormModel

class DeleteAccountDialogFragment : DialogFragment() {

    lateinit var mContentViewBinding: FragmentDeleteAccountDialogBinding

    companion object {
        fun newInstance(): DeleteAccountDialogFragment {
            return DeleteAccountDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_account_dialog, container, false)
        isCancelable = false
        mContentViewBinding.loading = false
        mContentViewBinding.data = LoginFormModel()
        mContentViewBinding.handler = DeleteAccountDialogHandler(this)
        return mContentViewBinding.root
    }
}
