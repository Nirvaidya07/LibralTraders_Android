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

package com.webkul.mobikul.helpers

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.SplashScreenActivity
import java.util.*


class LocaleUtils {

    companion object {
        private var sLocale: Locale? = null

        fun changeLanguage(context: Context) {
            LocaleUtils.updateConfig(context)

            AppSharedPref.setShowSplash(context, false)

            val intent = Intent(context, SplashScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            (context as BaseActivity).finish()
        }

        fun updateConfig(context: Context) {
            val lang = AppSharedPref.getStoreCode(context)
            val myLocale: Locale
            myLocale = when (lang) {
                "en_US" -> Locale.ENGLISH
                "zh_Hans_CN" -> Locale.SIMPLIFIED_CHINESE
                "zh_Hant_HK" -> Locale.TRADITIONAL_CHINESE
                else -> Locale(lang)
            }

            setLocale(myLocale)
            val config = Configuration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(sLocale)
            } else {
                config.locale = sLocale
            }

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        private fun setLocale(locale: Locale) {
            sLocale = locale
            if (sLocale != null) {
                Locale.setDefault(sLocale)
            }
        }
    }
}