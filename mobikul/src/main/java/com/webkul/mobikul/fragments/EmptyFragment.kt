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
import androidx.fragment.app.Fragment
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentEmptyBinding
import com.webkul.mobikul.handlers.EmptyFragmentHandler
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMPTY_FRAGMENT_BUTTON_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMPTY_FRAGMENT_DRAWABLE_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMPTY_FRAGMENT_HIDE_CONTINUE_SHOPPING_BTN
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMPTY_FRAGMENT_SUBTITLE_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_EMPTY_FRAGMENT_TITLE_ID

class EmptyFragment : Fragment() {

    companion object {
        fun newInstance(drawableIconId: String, title: String, subtitle: String, hideContinueShoppingBtn: Boolean = false, buttonTitle:String=""): EmptyFragment {
            val emptyFragment = EmptyFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_EMPTY_FRAGMENT_DRAWABLE_ID, drawableIconId)
            bundle.putString(BUNDLE_KEY_EMPTY_FRAGMENT_TITLE_ID, title)
            bundle.putString(BUNDLE_KEY_EMPTY_FRAGMENT_SUBTITLE_ID, subtitle)
            bundle.putBoolean(BUNDLE_KEY_EMPTY_FRAGMENT_HIDE_CONTINUE_SHOPPING_BTN, hideContinueShoppingBtn)
            bundle.putString(BUNDLE_KEY_EMPTY_FRAGMENT_BUTTON_TITLE, buttonTitle)
            emptyFragment.arguments = bundle
            return emptyFragment
        }
    }

    private lateinit var mContentViewBinding: FragmentEmptyBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_empty, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.animationView.setAnimation(requireArguments().getString(BUNDLE_KEY_EMPTY_FRAGMENT_DRAWABLE_ID))
        mContentViewBinding.title = requireArguments().getString(BUNDLE_KEY_EMPTY_FRAGMENT_TITLE_ID)
        mContentViewBinding.subtitle = requireArguments().getString(BUNDLE_KEY_EMPTY_FRAGMENT_SUBTITLE_ID)
        mContentViewBinding.buttonTitle = requireArguments().getString(BUNDLE_KEY_EMPTY_FRAGMENT_BUTTON_TITLE)?:""
        mContentViewBinding.handler = EmptyFragmentHandler()
    }
}
