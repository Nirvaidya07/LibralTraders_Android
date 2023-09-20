package com.webkul.mobikul.handlers

import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.MobikulApplication
import com.webkul.mobikul.models.homepage.NavDrawerExtrasRvModel
import com.webkul.mobikul.models.homepage.NavDrawerExtrasRvModel.Companion.ACCOUNT

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

class NavDrawerExtrasRvHandler(private val mFragmentContext: NavDrawerStartFragment) {

    fun onClickItem(extrasRvData: NavDrawerExtrasRvModel) {
        when (extrasRvData.type) {
            ACCOUNT -> {
                if (!AppSharedPref.isLoggedIn(mFragmentContext.requireContext())) {
                    mFragmentContext.context?.startActivity((mFragmentContext.requireActivity().application as MobikulApplication).getLoginAndSignUpActivity(mFragmentContext.requireContext()))
                }
            }
        }
    }
}