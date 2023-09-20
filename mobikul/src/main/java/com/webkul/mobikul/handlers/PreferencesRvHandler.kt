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

package com.webkul.mobikul.handlers

import android.os.Looper
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.fragments.*
import com.webkul.mobikul.models.user.PreferencesRvModel.Companion.CURRENCY
import com.webkul.mobikul.models.user.PreferencesRvModel.Companion.LANGUAGE
import com.webkul.mobikul.models.user.PreferencesRvModel.Companion.SETTINGS
import com.webkul.mobikul.models.user.PreferencesRvModel.Companion.WEBSITE

class PreferencesRvHandler(val mFragmentContext: NavDrawerStartFragment) {

    fun onClickRvItem(type: Int) {
        mFragmentContext.context?.let { context ->

            android.os.Handler(Looper.getMainLooper()).postDelayed({
                when (type) {
                    WEBSITE -> {
                        WebsiteBottomSheetFragment().show(
                            ( context as HomeActivity).supportFragmentManager,
                            WebsiteBottomSheetFragment::class.java.simpleName
                        )
                    }
                    LANGUAGE -> {
                        LanguagesBottomSheetFragment().show(
                            ( context as HomeActivity).supportFragmentManager,
                            LanguagesBottomSheetFragment::class.java.simpleName
                        )
                    }
                    CURRENCY -> {
                        CurrencyBottomSheetFragment().show(
                            ( context as HomeActivity).supportFragmentManager,
                            CurrencyBottomSheetFragment::class.java.simpleName
                        )
                    }
                    SETTINGS -> {
                        SettingsBottomSheetFragment().show(
                            ( context as HomeActivity).supportFragmentManager,
                            SettingsBottomSheetFragment::class.java.simpleName
                        )
                    }
                }
            }, 300)
            (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
        }
    }
}