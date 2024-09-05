package com.webkul.mobikul.activities

import android.animation.Animator
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import com.libraltraders.android.BuildConfig

import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivitySplashScreenBinding
import com.webkul.mobikul.handlers.OnBoardActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_PRICE_PATTERN
import com.webkul.mobikul.helpers.AppSharedPref.Companion.KEY_PRICE_PRECISION
import com.webkul.mobikul.helpers.AppSharedPref.Companion.PRICE_FORMAT_PREF
import com.webkul.mobikul.helpers.ApplicationConstants.BASE_URL
import com.webkul.mobikul.helpers.ApplicationConstants.DEFAULT_WEBSITE_ID
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_ON_BOARDING
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_SPLASH_ANIMATION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CATEGORY
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ON_BOARD_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.ConstantsHelper.CUSTOMER_NOT_EXIST
import com.webkul.mobikul.helpers.NetworkHelper.Companion.isNetworkAvailable
import com.webkul.mobikul.helpers.Utils.Companion.getMd5String
import com.webkul.mobikul.helpers.Utils.Companion.isDemo
import com.webkul.mobikul.launcherAlias.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.homepage.*
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

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
 *
 *
 */

open class SplashScreenActivity : BaseActivity() {

    private lateinit var mContentViewBinding: ActivitySplashScreenBinding
    private var mUrl = ""
    private var mIsAnimationFinished: Boolean = false
    private var mHomePageDataModel: HomePageDataModel? = null
    private var mIsFreshHomePageData: Boolean = false
    lateinit var inAppUpdateHelper: InAppUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        setSplashScreen()
        setLauncherIcon()
        addAnimationListener()
        checkAction()
        FirebaseAnalyticsHelper.logAppOpenEvent()
    }

    private fun setSplashScreen() {
        if (AppSharedPref.getSplashScreen(this).isNotEmpty()) {
            try {
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val splashImageUrl = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) AppSharedPref.getSplashScreenDark(this) else AppSharedPref.getSplashScreen(this)
                Glide.with(this)
                        .load(splashImageUrl)
                        .error(R.drawable.splash_screen)
                        .placeholder(R.drawable.splash_screen)
                        .into(mContentViewBinding.imgSplash)
            } catch (e: java.lang.Exception) {

            }
        }
    }

    private fun setLauncherIcon() {
        if (AppSharedPref.getLauncherImage(this).isNotEmpty()) {
            when (AppSharedPref.getLauncherImage(this)) {
                "1" -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }

                "2" -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }

                "3" -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }

                "4" -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }

                "5" -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }
                else -> {
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

                }
            }
        } else {
            packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, DefaultLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

            packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, SecondLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, ThirdLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FourthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@SplashScreenActivity, FifthLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

        }
    }

    private fun addAnimationListener() {
        if (ENABLE_SPLASH_ANIMATION) {
            mContentViewBinding.splashScreenAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    mIsAnimationFinished = true
                    if (mHomePageDataModel == null)
                        mContentViewBinding.loading = true
                    startHomeActivity()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        } else {
            mContentViewBinding.loading = true
            mIsAnimationFinished = true
        }
    }

    private fun checkAction() {
        val action = intent?.action
        val data = intent?.dataString
        if (action == Intent.ACTION_VIEW && data != null && data != BASE_URL) {
            mUrl = data
        }
        checkLocalData()
    }

    private fun checkLocalData() {
        if (AppSharedPref.getWebsiteId(this) == DEFAULT_WEBSITE_ID && AppSharedPref.getStoreId(this) == "0" && mUrl.isBlank() && isDemo(this)) {
            checkFileForHomePageData()
        } else if (AppSharedPref.getWebsiteId(this) == DEFAULT_WEBSITE_ID && AppSharedPref.getStoreId(this) == "0" && mUrl.isBlank()) {
            callApi()
        } else {
            mHashIdentifier = getMd5String("homePageData" + AppSharedPref.getWebsiteId(this) + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCurrencyCode(this) + mUrl)
            mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<String> {
                        override fun onNext(data: String) {
                            if (data.isBlank()) {
                                callApi()
                            } else {
                                updateAnimationCheckAndProceed(data)
                            }
                        }

                        override fun onError(e: Throwable) {
                            callApi()
                        }

                        override fun onSubscribe(disposable: Disposable) {
                            mCompositeDisposable.add(disposable)
                        }

                        override fun onComplete() {

                        }
                    })
        }

    }

    /*
*
* This function is only for Webkul demo. To load the home page data faster.
*
* */
    private fun checkFileForHomePageData() {
      /*  try {
            val homePageFileData = assets.open("home_page_data.json")
            val size = homePageFileData.available()
            val buffer = ByteArray(size)
            homePageFileData.read(buffer)
            homePageFileData.close()
            mIsFreshHomePageData = true
            updateAnimationCheckAndProceed(String(buffer, Charsets.UTF_8))
        } catch (ex: Exception) {
            ex.printStackTrace()*/
            callApi()
     /*   }*/

    }


    private fun updateAnimationCheckAndProceed(response: String) {
        if (!AppSharedPref.showSplash(this)) {
            mIsAnimationFinished = true
        }
        Handler(Looper.getMainLooper()).postDelayed({
            val objectMapper = ObjectMapper()
            val homePageDataModel: HomePageDataModel = objectMapper.readValue(response, HomePageDataModel::class.java)
            onSuccessfulResponse(homePageDataModel)
        }, if (ENABLE_SPLASH_ANIMATION) 0.toLong() else 300.toLong())
    }


    private fun callApi() {
        ApiConnection.getHomePageData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mUrl.isNotBlank(), mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<HomePageDataModel>(this, true) {
                    override fun onNext(responseModel: HomePageDataModel) {
                        super.onNext(responseModel)
                        if (responseModel.success) {
                            mIsFreshHomePageData = true
                            onSuccessfulResponse(responseModel)
                        } else {
                            getLatestVersionFromPlayStore()
                            onFailureResponse(responseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        getLatestVersionFromPlayStore()
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(homePageDataModel: HomePageDataModel) {
        setOnBoardingVersion(homePageDataModel)
        mHomePageDataModel = homePageDataModel
        setAppSharedPrefConfigDetails()
        mHomePageDataModel!!.priceFormat?.let { updatePriceFormatPref(it) }
        checkDeepLinkData()
    }

    private fun setAppSharedPrefConfigDetails() {
        if (!mHomePageDataModel!!.websiteId.isNullOrEmpty()) {
            mHomePageDataModel!!.websiteId?.let { AppSharedPref.setWebsiteId(this, it) }
        }

        if (!mHomePageDataModel!!.lightSplashImage.isNullOrEmpty()) {
            AppSharedPref.setSplashScreen(this, mHomePageDataModel!!.lightSplashImage!!)
        }

        if (!mHomePageDataModel!!.darkSplashImage.isNullOrEmpty()) {
            AppSharedPref.setSplashScreenDark(this, mHomePageDataModel!!.darkSplashImage!!)
        }

        if (!mHomePageDataModel!!.launcherIconType.isNullOrEmpty()) {
            AppSharedPref.setLauncherImage(this, mHomePageDataModel!!.launcherIconType!!)
        }

        if (!mHomePageDataModel!!.appLogo.isNullOrEmpty()) {
            AppSharedPref.setAppLogo(this, mHomePageDataModel!!.appLogo!!)
            AppSharedPref.setAppLogoDark(this, mHomePageDataModel!!.darkAppLogo)
        }

        if (!mHomePageDataModel!!.buttonTextColor.isNullOrEmpty()) {
            AppSharedPref.setAppButtonTextColor(this, mHomePageDataModel!!.buttonTextColor!!)
            AppSharedPref.setAppButtonTextColorDark(this, mHomePageDataModel!!.darkButtonTextColor)

        }
        if (!mHomePageDataModel!!.appButtonColor.isNullOrEmpty()) {
            AppSharedPref.setAppBgButtonColor(this, mHomePageDataModel!!.appButtonColor!!)
            AppSharedPref.setAppBgButtonColorDark(this, mHomePageDataModel!!.darkAppButtonColor)
        }
        if (!mHomePageDataModel!!.appThemeColor.isNullOrEmpty()) {
            AppSharedPref.setAppThemeColor(this, mHomePageDataModel!!.appThemeColor!!)
            AppSharedPref.setAppThemeColorDark(this, mHomePageDataModel!!.darkAppThemeColor)
        }
        if (!mHomePageDataModel!!.appThemeTextColor.isNullOrEmpty()) {
            AppSharedPref.setAppThemeTextColorColor(this, mHomePageDataModel!!.appThemeTextColor)
            AppSharedPref.setAppThemeTextColorDark(this, mHomePageDataModel!!.darkAppThemeTextColor)
        }
        if (!mHomePageDataModel?.lightAppLogoDominantColor.isNullOrEmpty()) {
            AppSharedPref.setAppLogoDominantColor(this, mHomePageDataModel!!.lightAppLogoDominantColor!!)
            AppSharedPref.setAppLogoDominantColorDark(this, mHomePageDataModel!!.darkAppLogoDominantColor)
        }

        AppSharedPref.setTwilioOtpLength(this, mHomePageDataModel!!.otpLenght?:"4")
        AppSharedPref.setIsMobileLoginEnabled(this, mHomePageDataModel!!.isEnableOTPLogin?:false)


        mHomePageDataModel!!.websiteData?.let { setWebsiteData(it) }

        if (!mHomePageDataModel!!.storeId.isNullOrEmpty()) {
            mHomePageDataModel!!.storeId?.let { AppSharedPref.setStoreId(this, it) }
            mHomePageDataModel!!.storeData?.let { setStoreData(it) }
        }
        if (AppSharedPref.getCurrencyCode(this).isEmpty()) {
            mHomePageDataModel!!.defaultCurrency?.let { AppSharedPref.setCurrencyCode(this, it) }
            mHomePageDataModel!!.allowedCurrencies?.let { setCurrencyData(it) }
        }


        AppSharedPref.setIsWishlistEnabled(this, mHomePageDataModel!!.wishlistEnable)
        mHomePageDataModel!!.categories?.let { AppSharedPref.setCategoryData(this, it) }
        AppSharedPref.setShowSplash(this, true)

        updateCartCount(mHomePageDataModel!!.cartCount)

        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(this, AppSharedPref.CUSTOMER_PREF)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_NAME, mHomePageDataModel!!.customerName)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_EMAIL, mHomePageDataModel!!.customerEmail)
        customerDataSharedPref.apply()
    }

    private fun setWebsiteData(websiteDataArray: ArrayList<WebsiteData>) {
        websiteDataArray.forEach { websiteData ->
            if (AppSharedPref.getWebsiteId(this) == websiteData.id) {
                websiteData.name?.let { AppSharedPref.setWebsiteLabel(this, it) }
            }
        }
    }

    private fun setStoreData(storeDataArray: ArrayList<StoreData>) {
        storeDataArray.forEach { storeData ->
            storeData.stores?.forEach { languageData ->
                if (AppSharedPref.getStoreId(this) == languageData.id) {
                    languageData.name?.let { AppSharedPref.setStoreLabel(this, it) }
                    languageData.code?.let { AppSharedPref.setStoreCode(this, it) }
                    LocaleUtils.updateConfig(this)
                    return
                }
            }
        }
    }

    private fun setCurrencyData(allowedCurrencies: ArrayList<Currency>) {
        allowedCurrencies.forEach { currencyData ->
            if (AppSharedPref.getCurrencyCode(this) == currencyData.code) {
                currencyData.label?.let { AppSharedPref.setCurrencyLabel(this, it) }
                return
            }
        }
    }

    private fun updatePriceFormatPref(priceFormat: PriceFormat) {
        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(this, PRICE_FORMAT_PREF)
        customerDataSharedPref.putString(KEY_PRICE_PATTERN, priceFormat.pattern)
        customerDataSharedPref.putInt(KEY_PRICE_PRECISION, priceFormat.precision)
        customerDataSharedPref.apply()
    }

    private fun checkDeepLinkData() {
        if (!mHomePageDataModel!!.productId.isNullOrEmpty()) {
            val intent = (applicationContext as MobikulApplication).getProductDetailsActivity(this)
            intent.putExtra(BUNDLE_KEY_PRODUCT_ID, mHomePageDataModel!!.productId)
            intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, mHomePageDataModel!!.productName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(BUNDLE_KEY_FROM_NOTIFICATION, true)
            startActivity(intent)
            finish()
        } else if (!mHomePageDataModel!!.categoryId.isNullOrEmpty()) {
            val intent = Intent(this, CatalogActivity::class.java)
            intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_CATEGORY)
            intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, mHomePageDataModel!!.categoryName)
            intent.putExtra(BUNDLE_KEY_CATALOG_ID, mHomePageDataModel!!.categoryId)
            intent.putExtra(BUNDLE_KEY_FROM_NOTIFICATION, true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            startHomeActivity()
        }
    }

    override fun setToolbarUpView() {
        //Nothing
    }

    private fun startHomeActivity() {

//        Log.d("Tag","startHomeActivity=====>${mHomePageDataModel?.walkThroughVersion}")
//        Log.d("Tag","startHomeActivity=====>${AppSharedPref.getShowOnBoardVersion(this)}")


        if (AppSharedPref.getShowOnBoardVersion(this) && ENABLE_ON_BOARDING && mHomePageDataModel?.walkThroughVersion != null) {
            startOnBoardingActivity()
        } else
            if (mIsAnimationFinished && mHomePageDataModel != null) {

                startActivity(homeActivityIntent)
            }
    }

    override fun onFailureResponse(response: Any) {
        super.onFailureResponse(response)
        mContentViewBinding.loading = false
        when ((response as BaseModel).otherError) {
            CUSTOMER_NOT_EXIST -> {
                // Do nothing as it will be handled from the super.
            }
            else -> {
                AlertDialogHelper.showNewCustomDialog(
                        this,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            finish()
                        }, "", null)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        mContentViewBinding.loading = false
        if ((!isNetworkAvailable(this) || (error is HttpException && error.code() == 304))) {

            mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<String> {
                        override fun onNext(response: String) {
                            if (response.isBlank()) {
                                AlertDialogHelper.showNewCustomDialog(
                                        this@SplashScreenActivity,
                                        getString(R.string.oops),
                                        NetworkHelper.getErrorMessage(this@SplashScreenActivity, error),
                                        false,
                                        getString(R.string.try_again),
                                        { dialogInterface: DialogInterface, _: Int ->
                                            dialogInterface.dismiss()
                                            mContentViewBinding.loading = true
                                            callApi()
                                        }, getString(R.string.close), { dialogInterface: DialogInterface, _: Int ->
                                    dialogInterface.dismiss()
                                    finish()
                                })
                            } else {
                                mHomePageDataModel =mObjectMapper.readValue(response, HomePageDataModel::class.java)
                                mIsFreshHomePageData = false
                                startHomeActivity()
                            }
                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onSubscribe(disposable: Disposable) {
                            mCompositeDisposable.add(disposable)
                        }

                        override fun onComplete() {

                        }
                    })
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this,
                    getString(R.string.oops),
                    NetworkHelper.getErrorMessage(this, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        mContentViewBinding.loading = true
                        callApi()
                    }, getString(R.string.close), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::inAppUpdateHelper.isInitialized) {
            inAppUpdateHelper.checkUpdateFinished()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ConstantsHelper.APP_UPDATE_REQUEST_CODE -> {
                    inAppUpdateHelper.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun getLatestVersionFromPlayStore() {
        if (ApplicationConstants.ENABLE_VERSION_CHECKER) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inAppUpdateHelper = InAppUpdateHelper(this)
                inAppUpdateHelper.checkForAppUpdate()
            } else {
                VersionChecker(this, this::onLatestVersionResponse).getUpdatedResponse()
            }
        }
    }


    private fun onLatestVersionResponse(result: String?) {

        try {
            if (result != null && java.lang.Double.parseDouble(Utils.getVersionName(this)) < java.lang.Double.parseDouble(result)) {
                AlertDialogHelper.showNewCustomDialog(
                        this,
                        getString(R.string.update_alert_title),
                        getString(R.string.new_version_available),
                        false,
                        getString(R.string.update),
                        { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")), ConstantsHelper.RC_UPDATE_APP_FROM_PLAY_STORE)
                        }, getString(R.string.later), { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun startOnBoardingActivity() {

        OnBoardActivityHandler().callApi(this, {
            val intent = Intent(this, OnBoardingActivity::class.java)
            intent.putExtra(BUNDLE_KEY_ON_BOARD_DATA, mObjectMapper.writeValueAsString(it))
            intent.putExtra(BUNDLE_KEY_HOME_PAGE_DATA, mObjectMapper.writeValueAsString(mHomePageDataModel))
            intent.putExtra(BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA, mIsFreshHomePageData)
            startActivity(intent)
        }, {
            startActivity(homeActivityIntent)
        }, {

            startActivity(homeActivityIntent)

        })
    }

    private val homeActivityIntent: Intent
        get() {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(BUNDLE_KEY_HOME_PAGE_DATA, mObjectMapper.writeValueAsString(mHomePageDataModel))
            intent.putExtra(BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA, mIsFreshHomePageData)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }


    private fun setOnBoardingVersion(responseModel: HomePageDataModel) {
        if (!responseModel.walkThroughVersion.isNullOrEmpty() && AppSharedPref.getOnBoardVersion(this).toDouble() < responseModel.walkThroughVersion!!.toDouble()) {
            AppSharedPref.setShowOnBoardVersion(this, true)
        }
    }
}