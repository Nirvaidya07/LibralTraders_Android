
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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.security.ProviderInstaller
import com.libraltraders.android.BuildConfig
import com.libraltraders.android.R
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.activities.ProductDetailsActivity
import com.webkul.mobikul.fragments.LoginBottomSheetFragment
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.fragments.SignUpBottomSheetFragment
import com.webkul.mobikul.handlers.LoginBottomSheetHandler
import com.webkul.mobikul.handlers.SignUpBottomSheetHandler
import com.webkul.mobikul.helpers.ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR
import com.webkul.mobikul.launcherAlias.*
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import zendesk.chat.Chat


open class MobikulApplication : MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

//        if(BuildConfig.DEBUG) {
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
//        }else{
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
//        }
        
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath(CALLIGRAPHY_FONT_PATH_REGULAR)
//                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

        AuthKeyHelper.getInstance().token = AppSharedPref.getFcmToken(this)?:""
        FirebaseAnalyticsHelper.initFirebaseAnalytics(this)
        upgradeSecurityProvider()
        initZopim()

    }

    private fun initZopim() {
        Chat.INSTANCE.init(applicationContext, ApplicationConstants.ZOPIM_ACCOUNT_KEY, "com.libraltraders.android")

    }


    private  fun upgradeSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {}
            override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
            }
        })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (AppSharedPref.getCartCount(this) != 0) {
            AbandonedCartAlarmHelper.scheduleAlarm(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        AbandonedCartAlarmHelper.cancelAlarm(this)
    }

    /*---#MobikulMp--*/
    fun getHomePageClass(): Class<*> {
        return HomeActivity::class.java
    }

    open fun getProductDetailsActivity(context: Context): Intent {
        return Intent(context, ProductDetailsActivity::class.java)
    }

    open fun getCatalogActivity(context: Context): Intent {
        return Intent(context, CatalogActivity::class.java)
    }

    open fun getLoginBottomSheetHandler(loginBottomSheetFragment: LoginBottomSheetFragment): LoginBottomSheetHandler {
        return LoginBottomSheetHandler(loginBottomSheetFragment)
    }

    open fun getSignUpBottomSheetHandler(signUpBottomSheetFragment: SignUpBottomSheetFragment): SignUpBottomSheetHandler {
        return SignUpBottomSheetHandler(signUpBottomSheetFragment)
    }

    open fun getNavDrawerStartFragment(): NavDrawerStartFragment {
        return NavDrawerStartFragment()
    }

    open fun getSellerDashboardActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerOrdersActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerOrderDetailsActivity(context: Context): Intent? {
        return null
    }


    open fun getAskAdminActivity(context: Context): Intent? {
        return null
    }

    open fun getBecomeSellerActivity(context: Context): Intent? {
        return null
    }

    open fun getCreateAttributeActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerProfileEditActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerAddProductActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerProductsListActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerTransactionsListActivity(context: Context): Intent? {
        return null
    }

    open fun getManagePrintPdfHeaderActivity(context: Context): Intent? {
        return null
    }

    open fun getSellerProfileActivity(context: Context): Intent? {
        return null
    }

    open fun getChatRelatedActivity(mContext: Context): Intent? {
        return null

    }

    open fun getSellerChatActivity(mContext: Context): Intent? {
        return null

    }

    open fun getCustomerListActivity(mContext: Context): Intent? {
        return null
    }

    open fun getLoginAndSignUpActivity(mContext: Context): Intent? {
        return Intent(mContext, LoginAndSignUpActivity::class.java)
    }
}

/*
*  To check the build error run the below command
*
*  ./gradlew assemble --stacktrace --info
* */