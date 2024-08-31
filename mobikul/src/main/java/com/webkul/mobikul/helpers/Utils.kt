package com.webkul.mobikul.helpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.speech.RecognizerIntent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.libraltraders.android.BuildConfig.IS_MARKET_PLACE

import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.helpers.ApplicationConstants.API_AUTH_TYPE
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_DYNAMIC_THEME_COLOR
import com.webkul.mobikul.helpers.ApplicationConstants.MOBIKUL_APP_BUILDER
import com.webkul.mobikul.helpers.ApplicationConstants.MOBIKUL_MARKET_PLACE
import com.webkul.mobikul.helpers.ConstantsHelper.DEFAULT_DATE_FORMAT
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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

class Utils {

    companion object {
        @JvmStatic
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        @JvmStatic
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val screenDensity = Resources.getSystem().displayMetrics.density

        fun disableUserInteraction(context: Context) {
            try {
                (context as BaseActivity).window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } catch (e: Exception) {
            }
        }

        fun enableUserInteraction(context: Context) {
            try {
                (context as BaseActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } catch (e: Exception) {
            }
        }

        fun sendRegistrationTokenToServer(context: Context, token: String?) {
            AppSharedPref.setFcmToken(context, token)
            AuthKeyHelper.getInstance().token = token
            ApiConnection.uploadTokenData(context, token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(context, false) {})
        }

        fun showKeyboard(view: View) {
            view.requestFocus()
            if (!isHardKeyboardAvailable(view)) {
                val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(view, 0)
            }
        }

        private fun isHardKeyboardAvailable(view: View): Boolean {
            return view.context.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
        }

        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (activity.currentFocus!!.windowToken != null)
                    inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: Exception) {
            }
        }

        fun hideKeyboard(view: View) {
            try {
                val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (view.windowToken != null)
                    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: Exception) {
            }
        }

        fun getMd5String(stringToConvert: String): String {
            try {
                // Create MD5 Hash
                val digest = java.security.MessageDigest
                        .getInstance(API_AUTH_TYPE)
//                        .getInstance("MD5")
                digest.update(stringToConvert.toByteArray())
                val messageDigest = digest.digest()

                // Create Hex String
                val hexString = StringBuilder()
                for (aMessageDigest in messageDigest) {
                    var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                    while (h.length < 2)
                        h = "0$h"
                    hexString.append(h)
                }
                return hexString.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

        fun showShakeError(context: Context, viewToAnimate: View) {
            try {
                viewToAnimate.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_error))
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun isVoiceAvailable(context: Context): Boolean {
            return context.packageManager.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size > 0
        }

        fun shareProduct(context: Context, url: String) {
            FirebaseAnalyticsHelper.logShareEvent(url)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, url)
            sendIntent.type = "text/plain"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.choose_an_action), null))
            } else {
                context.startActivity(sendIntent)
            }
        }

        fun setSpinnerError(spinner: Spinner, errorString: String) {
            val selectedView = spinner.selectedView
            if (selectedView != null && selectedView is TextView) {
                spinner.requestFocus()
                selectedView.setTextColor(Color.RED)
                selectedView.text = errorString
                spinner.performClick()
            }
        }

        fun setBadgeCount(context: Context, icon: LayerDrawable, cartCount: Int) {
            val badge: BadgeDrawable
            // Reuse drawable if possible
            val reuse = icon.findDrawableByLayerId(R.id.ic_menu_badge)
            if (reuse != null && reuse is BadgeDrawable) {
                badge = reuse
            } else {
                badge = BadgeDrawable(context)
            }
            badge.setCount(cartCount.toString())
            icon.mutate()
            icon.setDrawableByLayerId(R.id.ic_menu_badge, badge)
        }

        fun logoutAndGoToHome(context: Context) {
            val customerSharedPrefEditor = AppSharedPref.getSharedPreferenceEditor(context, AppSharedPref.CUSTOMER_PREF)
            customerSharedPrefEditor.clear()
            customerSharedPrefEditor.apply()

            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        fun generateRandomPassword(): String {
            val random = SecureRandom()
            val letters = "abcdefghjklmnopqrstuvwxyzABCDEFGHJKMNOPQRSTUVWXYZ1234567890"
            val numbers = "1234567890"
            val specialChars = "!@#$%^&*_=+-/"
            var pw = ""
            for (i in 0..7) {
                val index = (random.nextDouble() * letters.length).toInt()
                pw += letters.substring(index, index + 1)
            }
            val indexA = (random.nextDouble() * numbers.length).toInt()
            pw += numbers.substring(indexA, indexA + 1)
            val indexB = (random.nextDouble() * specialChars.length).toInt()
            pw += specialChars.substring(indexB, indexB + 1)
            return pw
        }


        fun isValidEmailId(email: String): Boolean {
            return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches()
        }

        fun formatDate(dateFormat: String?, year: Int, month: Int, day: Int): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = 0
            cal.set(year, month, day)
            val date = cal.time
            val sdf = SimpleDateFormat(dateFormat ?: DEFAULT_DATE_FORMAT, Locale.getDefault())
            return sdf.format(date)
        }

        fun generateRandomId(): String {
            val random = SecureRandom()
            val letters = "abcdefghjklmnopqrstuvwxyzABCDEFGHJKMNOPQRSTUVWXYZ1234567890"
            val numbers = "1234567890"
            var pw = ""
            for (i in 0..7) {
                val index = (random.nextDouble() * letters.length).toInt()
                pw += letters.substring(index, index + 1)
            }
            val indexA = (random.nextDouble() * numbers.length).toInt()
            pw += numbers.substring(indexA, indexA + 1)
            return pw
        }

        fun validateUrlForSpecialCharacter(url: String): Boolean {
            if (url.contains(" "))
                return false
            return url.matches("[A-Za-z0-9^.-]*".toRegex())
        }

        fun isValidPhone(phone: String): Boolean {
            val PHONE_PATTERN = "^(91)?[6-9][0-9]{9}$"
            val pattern = Pattern.compile(PHONE_PATTERN)
            val matcher = pattern.matcher(phone)
            return matcher.matches()
        }

        @JvmStatic
        fun getVersionName(ctx: Context): String {
            return try {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "1.0"
            }
        }

        fun setToolbarThemeColor(toolbar: Toolbar?) {

            if (ENABLE_DYNAMIC_THEME_COLOR && toolbar !=null) {

                if (AppSharedPref.getAppThemeColor(toolbar.context).isNotEmpty()) {
                    toolbar.background = ColorDrawable(ContextCompat.getColor(toolbar.context, R.color.colorPrimary))
                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.context, R.color.colorPrimary))
                }
                val foreground = if (AppSharedPref.getAppThemeTextColor(toolbar.context).isNotEmpty()) {
                    Color.parseColor(AppSharedPref.getAppThemeTextColor(toolbar.context))
                } else {
                    ContextCompat.getColor(toolbar.context, R.color.actionBarItemsColor)
                }
                toolbar.setTitleTextColor(foreground)
                toolbar.setSubtitleTextColor(foreground)
                val colorFilter = PorterDuffColorFilter(foreground, PorterDuff.Mode.MULTIPLY)

                // Overflow icon
                val overflowIcon: Drawable? = toolbar.overflowIcon
                if (overflowIcon != null) {
                    overflowIcon.colorFilter = colorFilter
                    toolbar.overflowIcon = overflowIcon
                }

                //Overflow navigation icon
                val navigationIcon: Drawable? = toolbar.navigationIcon
                if (navigationIcon != null) {
                    navigationIcon.colorFilter = colorFilter
                    toolbar.navigationIcon = navigationIcon
                }

            }
        }

        fun setActionBarThemeColor(actionBar: androidx.appcompat.app.ActionBar?, context: Context){

            if (ENABLE_DYNAMIC_THEME_COLOR && actionBar != null) {
                val backgroundColor= if (AppSharedPref.getAppThemeColor(context).isNotEmpty()) {
                    Color.parseColor(AppSharedPref.getAppThemeColor(context))
                } else {
                    ContextCompat.getColor(context, R.color.colorPrimary)
                }

                val foregroundColor = if (AppSharedPref.getAppThemeTextColor(context).isNotEmpty()) {
                    Color.parseColor(AppSharedPref.getAppThemeTextColor(context))
                } else {
                    ContextCompat.getColor(context, R.color.actionBarItemsColor)
                }

                actionBar.apply {
                    val text = SpannableString(title ?: "")
                    text.setSpan(ForegroundColorSpan(foregroundColor),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    val upArrow =ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material)
                    upArrow?.colorFilter= PorterDuffColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)
                    title = text
                    setBackgroundDrawable(ColorDrawable(backgroundColor))
                    setHomeAsUpIndicator(upArrow)
                }
            }
        }


        fun setMenuItemIconColor(menu: Menu?, context: Context) {
            if (ENABLE_DYNAMIC_THEME_COLOR && menu!=null) {

                val foregroundColor = if (AppSharedPref.getAppThemeTextColor(context).isNotEmpty()) {
                    Color.parseColor(AppSharedPref.getAppThemeTextColor(context))
                } else {
                    ContextCompat.getColor(context, R.color.actionBarItemsColor)
                }
                try {
                    val iconTint = PorterDuffColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)
                    for (i in 0 until menu.size()) {
                        val drawable = menu.getItem(i).icon
                        drawable?.apply {
                            mutate()
                            drawable.colorFilter = iconTint
                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }

        fun setDrawerIconColor(mDrawerToggle: ActionBarDrawerToggle?, context: Context) {
            if (ENABLE_DYNAMIC_THEME_COLOR && AppSharedPref.getAppThemeTextColor(context).isNotEmpty()) {
                mDrawerToggle?.drawerArrowDrawable?.color = Color.parseColor(AppSharedPref.getAppThemeTextColor(context))
            } else {
                mDrawerToggle?.drawerArrowDrawable?.color = ContextCompat.getColor(context, R.color.actionBarItemsColor)
            }
        }

        /* fun lottieTint(view: LottieAnimationView?) {
             if (view != null) {
                 if (AppSharedPref.getAppBgButtonColor(view?.context).isNotEmpty()) {
                     view?.addValueCallback(
                             KeyPath("**"),
                             LottieProperty.COLOR_FILTER,
                             { PorterDuffColorFilter(ColorUtils.setAlphaComponent(Color.parseColor(AppSharedPref.getAppBgButtonColor(view?.context)), 180), PorterDuff.Mode.SRC_ATOP) }
                     )

                 }
             }
         }*/

        fun isDemo(mContext:Context):Boolean{
            return  when {
                mContext.packageName== MOBIKUL_APP_BUILDER &&  !IS_MARKET_PLACE -> true
                mContext.packageName== MOBIKUL_MARKET_PLACE &&  IS_MARKET_PLACE -> true
                else -> false
            }
        }
    }
}