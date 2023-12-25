/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.ar.core.ArCoreApk
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.*
import com.webkul.mobikul.databinding.*
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.handlers.HomeActivityHandler
import com.webkul.mobikul.handlers.HomePageProductCarouselHandler
import com.webkul.mobikul.handlers.ProductTileHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD
import com.webkul.mobikul.helpers.ApplicationConstants.DEFAULT_TIME_TO_SWITCH_BANNER_IN_MILLIS
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_HOME_BANNER_AUTO_SCROLLING
import com.webkul.mobikul.helpers.BindingAdapterUtils.Companion.setAppThem
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_OPEN_CART
import com.webkul.mobikul.helpers.ConstantsHelper.RC_LOGIN
import com.webkul.mobikul.helpers.ConstantsHelper.RC_QR_LOGIN
import com.webkul.mobikul.helpers.Utils.Companion.getVersionName
import com.webkul.mobikul.helpers.Utils.Companion.setDrawerIconColor
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.SortOrder
import com.webkul.mobikul.models.homepage.Carousel
import com.webkul.mobikul.models.homepage.HomePageDataModel
import com.webkul.mobikul.models.product.ProductTileData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.github.inflationx.calligraphy3.CalligraphyTypefaceSpan
import io.github.inflationx.calligraphy3.TypefaceUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityHomeBinding
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mBackPressedTime: Long = 0
    private var mBannerSwitcherTimerList: ArrayList<Timer> = ArrayList()
    private var mOfferSwitcherTimerList: ArrayList<Timer> = ArrayList()

    lateinit var mHomePageDataModel: HomePageDataModel
    lateinit var inAppUpdateHelper: InAppUpdateHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mHomePageDataModel = HomePageDataModel()
        mContentViewBinding.loading = true
        startInitialization()
    }

    private fun startInitialization() {
        LocaleUtils.updateConfig(this)

        initSupportActionBar()
        initDrawerToggle()
        initSwipeRefresh()
        initHomePageData()
    }

    override fun initSupportActionBar() {
        setSupportActionBar(mContentViewBinding.toolbar)
        setToolbarTitle()
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

  private fun  setToolbarTitle (){
      val title = SpannableString(getString(R.string.activity_title_home))
      title.setSpan(CalligraphyTypefaceSpan(TypefaceUtils.load(assets, CALLIGRAPHY_FONT_PATH_SEMI_BOLD)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

      if (!AppSharedPref.getAppLogo(this).isNullOrEmpty()) {
            supportActionBar?.title = ""
            mContentViewBinding.appLogo.visibility = View.VISIBLE
          try {
              val placeholder=AppSharedPref.getAppLogoDominantColor(this)
              val imageUrl=AppSharedPref.getAppLogo(this)
              if (placeholder.isNullOrBlank()) {
                  Glide.with(this)
                          .load(imageUrl ?: "")
                          .apply(RequestOptions().dontTransform()
                                  .placeholder(R.drawable.placeholder)
                                  .dontAnimate())
                          .into(mContentViewBinding.appLogo)
              } else {
                  Glide.with(this)
                          .load(imageUrl ?: "")
                          .apply(RequestOptions().dontTransform()
                                  .placeholder(ColorDrawable(Color.parseColor(placeholder)))
                                  .dontAnimate())
                          .into(mContentViewBinding.appLogo)
              }

          } catch (e: Exception) {
              e.printStackTrace()
              supportActionBar?.title = title
          }
        } else {
            supportActionBar?.title = title
            mContentViewBinding.appLogo.visibility = View.GONE

        }
    }
    private fun initDrawerToggle() {
        mDrawerToggle = ActionBarDrawerToggle(this, mContentViewBinding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        mContentViewBinding.drawerLayout.addDrawerListener(mDrawerToggle!!)
        setDrawerIconColor(mDrawerToggle,this)
    }

    private fun initSwipeRefresh() {
        mContentViewBinding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        mContentViewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkHelper.isNetworkAvailable(this)) {
                callApi()
            } else {
                checkAndLoadLocalData()

                mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                ToastHelper.showToast(this@HomeActivity, getString(R.string.you_are_offline))
            }
        }
    }

    private fun initHomePageData() {
        mHashIdentifier = Utils.getMd5String("homePageData" + AppSharedPref.getWebsiteId(this) + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCurrencyCode(this))

        if (intent.hasExtra(BUNDLE_KEY_HOME_PAGE_DATA)) {
            intent.getStringExtra(BUNDLE_KEY_HOME_PAGE_DATA)?.let {
                mHomePageDataModel = mObjectMapper.readValue(it, HomePageDataModel::class.java)
            }
            initLayout()
            if (!intent.getBooleanExtra(BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA, false))
                callApi()
        } else {
            mHashIdentifier = Utils.getMd5String("homePageData" + AppSharedPref.getWebsiteId(this) + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCurrencyCode(this))
            checkAndLoadLocalData()
            callApi()

        }
        getLatestVersionFromPlayStore()
        checkForArSupport()
    }

    private fun callApi() {
        mContentViewBinding.swipeRefreshLayout.isRefreshing = true
        mContentViewBinding.loading = true

        ApiConnection.getHomePageData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), false, "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<HomePageDataModel>(this, false) {
                    override fun onNext(homePageDataModel: HomePageDataModel) {
                        super.onNext(homePageDataModel)
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        if (homePageDataModel.success) {
                            setOnBoardingVersion(homePageDataModel)
                            onSuccessfulResponse(homePageDataModel)
                        } else {
                            onFailureResponse(homePageDataModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            mHomePageDataModel =mObjectMapper.readValue(response, HomePageDataModel::class.java)
                            initLayout()
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
    }

    private fun onSuccessfulResponse(homePageDataModel: HomePageDataModel) {
        mHomePageDataModel = homePageDataModel
        setAppSharedPrefConfigDetails()
        initLayout()
        resetThemeStyle()
    }

    private fun resetThemeStyle() {
         setAppThem(mContentViewBinding.toolbar, 1)
         Utils.setMenuItemIconColor(mContentViewBinding.toolbar.menu, this)
         setDrawerIconColor(mDrawerToggle,this)
         setToolbarTitle()
    }

    private fun setAppSharedPrefConfigDetails() {
        AppSharedPref.setIsWishlistEnabled(this, mHomePageDataModel.wishlistEnable)

        if (!mHomePageDataModel.lightSplashImage.isNullOrEmpty()) {
            AppSharedPref.setSplashScreen(this, mHomePageDataModel.lightSplashImage!!)
        }

        if (!mHomePageDataModel.darkSplashImage.isNullOrEmpty()) {
            AppSharedPref.setSplashScreenDark(this, mHomePageDataModel.darkSplashImage!!)
        }
        if (!mHomePageDataModel.launcherIconType.isNullOrEmpty()) {
            AppSharedPref.setLauncherImage(this, mHomePageDataModel.launcherIconType!!)
        }

        if (!mHomePageDataModel.appLogo.isNullOrEmpty()) {
            AppSharedPref.setAppLogo(this, mHomePageDataModel.appLogo!!)
            AppSharedPref.setAppLogoDark(this, mHomePageDataModel.darkAppLogo)
        }

        if (!mHomePageDataModel.lightAppLogoDominantColor.isNullOrEmpty()) {
            AppSharedPref.setAppLogoDominantColor(this, mHomePageDataModel.lightAppLogoDominantColor!!)
            AppSharedPref.setAppLogoDominantColorDark(this, mHomePageDataModel.darkAppLogoDominantColor)
        }

        if (!mHomePageDataModel.buttonTextColor.isNullOrEmpty()) {
            AppSharedPref.setAppButtonTextColor(this, mHomePageDataModel.buttonTextColor!!)
            AppSharedPref.setAppButtonTextColorDark(this, mHomePageDataModel.darkButtonTextColor)

        }

        if (!mHomePageDataModel.appButtonColor.isNullOrEmpty()) {
            AppSharedPref.setAppBgButtonColor(this, mHomePageDataModel.appButtonColor!!)
            AppSharedPref.setAppBgButtonColorDark(this, mHomePageDataModel.darkAppButtonColor)
        }

        if (! mHomePageDataModel.appThemeColor.isNullOrEmpty()) {
            AppSharedPref.setAppThemeColor(this, mHomePageDataModel.appThemeColor!!)
            AppSharedPref.setAppThemeColorDark(this, mHomePageDataModel.darkAppThemeColor)
        }

        if (!mHomePageDataModel.appThemeTextColor.isNullOrEmpty()) {
            AppSharedPref.setAppThemeTextColorColor(this, mHomePageDataModel.appThemeTextColor!!)
            AppSharedPref.setAppThemeTextColorDark(this, mHomePageDataModel.darkAppThemeTextColor)
        }

        AppSharedPref.setTwilioOtpLength(this, mHomePageDataModel.otpLenght?:"4")
        AppSharedPref.setIsMobileLoginEnabled(this, mHomePageDataModel.isEnableOTPLogin?:false)

        val customerDataSharedPref = AppSharedPref.getSharedPreferenceEditor(this, AppSharedPref.CUSTOMER_PREF)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_NAME, mHomePageDataModel.customerName)
        customerDataSharedPref.putString(AppSharedPref.KEY_CUSTOMER_EMAIL, mHomePageDataModel.customerEmail)
        try {
            customerDataSharedPref.putBoolean("isSeller", mHomePageDataModel.isSeller)

            customerDataSharedPref.putBoolean("isPendingSeller", mHomePageDataModel.isPending)
            customerDataSharedPref.putBoolean("isAdmin", mHomePageDataModel.isAdmin)
            mHomePageDataModel.isDisapproved?.let {
                customerDataSharedPref.putBoolean("isDisapproved",
                    it
                )
            }
            customerDataSharedPref.putBoolean("showSellerProfile", mHomePageDataModel.showSellerProfileVisible)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        customerDataSharedPref.apply()

        mHomePageDataModel.categories?.let { AppSharedPref.setCategoryData(this, it) }
    }

    override fun onFailureResponse(response: Any) {
        super.onFailureResponse(response)
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                // Do nothing as it will be handled from the super.

            }
            else -> {
                ToastHelper.showToast(this, response.message)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this) || (error is HttpException && error.code() == 304)) && mContentViewBinding.data != null) {
            // Do Nothing as the data is already loaded
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = true
                        callApi()
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
        }
    }

    private fun initLayout() {
        mContentViewBinding.loading = false
        mContentViewBinding.mainScroller.visibility = View.VISIBLE
        mContentViewBinding.data = mHomePageDataModel
        mContentViewBinding.handler = HomeActivityHandler(this)
        updateCartCount(mContentViewBinding.data!!.cartCount)

        /* Init Nav Drawer Start */
        setupNavDrawerStart()

        /* Canceling All the Timers*/
        cancelAndPurgeAllTimers()

        /* Init Carousel Layout */
        setupCarouselsLayout()

        /* Init Recently Viewed Carousel Layout */
        setupRecentlyViewedCarouselsLayout()

        /* Check to Open Cart from Notification */
        checkForCartOpening()
    }

    private fun setupNavDrawerStart() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_drawer_start_frame, (application as MobikulApplication).getNavDrawerStartFragment())
        transaction.commitAllowingStateLoss()
    }

    private fun cancelAndPurgeAllTimers() {
        if (mBannerSwitcherTimerList.isNotEmpty()) {
            for (timer in mBannerSwitcherTimerList) {
                timer.cancel()
                timer.purge()
            }
            mBannerSwitcherTimerList = ArrayList()
        }
        if (mOfferSwitcherTimerList.isNotEmpty()) {
            for (timer in mOfferSwitcherTimerList) {
                timer.cancel()
                timer.purge()
            }
            mOfferSwitcherTimerList = ArrayList()
        }
    }

    private fun setupCarouselsLayout() {
        mContentViewBinding.carouselsLayout.removeAllViews()
        val sortedOrder = getSortedCarouselsData(mHomePageDataModel.sort_order)
        if (sortedOrder.isNotEmpty()) {

            val bannerImage = Carousel()
            bannerImage.id = "bannerimage"
            bannerImage.type = "banner"
            bannerImage.banners = mHomePageDataModel.bannerImages
            mHomePageDataModel.carousel?.add(bannerImage)

            val category = Carousel()
            category.id = "featuredcategories"
            category.type = "category"
            category.featuredCategories = mHomePageDataModel.featuredCategories
            mHomePageDataModel.carousel?.add(category)

            val carousel: Carousel? = mHomePageDataModel.carousel?.singleOrNull { it.id == "bannerimage" }
            if(carousel != null) {
                setupOfferBannerRv(carousel)
            }

            sortedOrder.forEachIndexed { index, sortorder ->
                val carousel: Carousel? = mHomePageDataModel.carousel?.singleOrNull { it.id == sortorder.layout_id }
                when (carousel?.type) {
                    "product" -> {
                        val temp = carousel.productList?.sortedBy { !it.isAvailable }
                        carousel.productList = ArrayList(temp?.toMutableList())
                        Log.d("dd", carousel.productList?.size.toString())
                        addProductCarousel(carousel)
                    }
                    "image" -> {
                        addImageCarousel(carousel)
                    }
                    "category" -> {
                        setupFeaturesCategoriesRv(carousel)
                    }
                    /*"banner" -> {
                        setupOfferBannerRv(carousel)
                    }*/
                }
            }
        } else {
            val bannerImage = Carousel()
            bannerImage.id = "bannerimage"
            bannerImage.type = "banner"
            bannerImage.banners = mHomePageDataModel.bannerImages

            val category = Carousel()
            category.id = "featuredcategories"
            category.type = "category"
            category.featuredCategories = mHomePageDataModel.featuredCategories

            setupOfferBannerRv(bannerImage)
            setupFeaturesCategoriesRv(category)

            if (!mHomePageDataModel.carousel.isNullOrEmpty()) {
                mHomePageDataModel.carousel?.forEachIndexed { index, carousel ->
                    //               Handler(Looper.getMainLooper()).postDelayed({
                    when (carousel.type) {
                        "product" -> {
                            addProductCarousel(carousel)
                        }
                        "image" -> {
                            addImageCarousel(carousel)
                        }
                    }
//                }, (index * 200).toLong())
                }
            }
        }
    }

    fun getSortedCarouselsData(sortOrder: ArrayList<SortOrder>?): List<SortOrder> {
        val sortedOrder = ArrayList<SortOrder>()
        sortOrder?.forEach { indexJ ->
            if (indexJ.positionArray?.size == 1) {
                sortedOrder.add(indexJ)
            } else {
                indexJ.positionArray?.forEach {
                    it
                    val sortData = SortOrder()
                    sortData.position = it
                    sortData.layout_id = indexJ.layout_id
                    sortData.layout_id = indexJ.layout_id
                    sortData.type = indexJ.type
                    sortedOrder.add(sortData)
                }
            }
        }
        return sortedOrder.sortedWith(compareBy { it.position!!.replace(",", "").toInt() })
    }

    private fun addProductCarousel(carousel: Carousel) {
        carousel
        when (selectRandomCarouselLayout(carousel.productList!!.size)) {
            1 -> {
                loadCarouselFirstLayout(carousel)
            }
            2 -> {
                loadCarouselSecondLayout(carousel)
            }
            3 -> {
                loadCarouselThirdLayout(carousel)
            }
            4 -> {
                loadCarouselFourthLayout(carousel)
            }
        }
    }

    private fun selectRandomCarouselLayout(size: Int): Int {
        if (size > 1)
            return Random().nextInt(size - 1) + 1
        else
            return 1
    }

    private fun loadCarouselFirstLayout(carousel: Carousel) {
        val productCarouselFirstLayoutBinding = DataBindingUtil.inflate<ProductCarouselFirstLayoutBinding>(layoutInflater, R.layout.product_carousel_first_layout, mContentViewBinding.carouselsLayout, false)
        productCarouselFirstLayoutBinding.data = carousel
        productCarouselFirstLayoutBinding.handler = HomePageProductCarouselHandler(this)
        productCarouselFirstLayoutBinding.productsCarouselRv.adapter = carousel.productList?.let { ProductCarouselHorizontalRvAdapter(this, it) }
        productCarouselFirstLayoutBinding.productsCarouselRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
        productCarouselFirstLayoutBinding.productsCarouselRv.isNestedScrollingEnabled = false
        mContentViewBinding.carouselsLayout.addView(productCarouselFirstLayoutBinding.root)
    }

    private fun loadCarouselSecondLayout(carousel: Carousel) {
        val productCarouselSecondLayoutBinding = DataBindingUtil.inflate<ProductCarouselSecondLayoutBinding>(layoutInflater, R.layout.product_carousel_second_layout, mContentViewBinding.carouselsLayout, false)
        productCarouselSecondLayoutBinding.data = carousel
        productCarouselSecondLayoutBinding.handler = HomePageProductCarouselHandler(this)
        productCarouselSecondLayoutBinding.productTileHandler = carousel.productList?.let { ProductTileHandler(this, it) }
        mContentViewBinding.carouselsLayout.addView(productCarouselSecondLayoutBinding.root)
    }

    private fun loadCarouselThirdLayout(carousel: Carousel) {
        val productCarouselThirdLayoutBinding = DataBindingUtil.inflate<ProductCarouselThirdLayoutBinding>(layoutInflater, R.layout.product_carousel_third_layout, mContentViewBinding.carouselsLayout, false)
        productCarouselThirdLayoutBinding.data = carousel
        productCarouselThirdLayoutBinding.handler = HomePageProductCarouselHandler(this)
        productCarouselThirdLayoutBinding.productTileHandler = carousel.productList?.let { ProductTileHandler(this, it) }
        mContentViewBinding.carouselsLayout.addView(productCarouselThirdLayoutBinding.root)
    }

    private fun loadCarouselFourthLayout(carousel: Carousel) {
        val productCarouselFourthLayoutBinding = DataBindingUtil.inflate<ProductCarouselFourthLayoutBinding>(layoutInflater, R.layout.product_carousel_fourth_layout, mContentViewBinding.carouselsLayout, false)
        productCarouselFourthLayoutBinding.data = carousel
        productCarouselFourthLayoutBinding.handler = HomePageProductCarouselHandler(this)
        productCarouselFourthLayoutBinding.productTileHandler = carousel.productList?.let { ProductTileHandler(this, it) }
        mContentViewBinding.carouselsLayout.addView(productCarouselFourthLayoutBinding.root)
    }

    private fun addImageCarousel(carousel: Carousel) {
        val imageCarouselLayoutBinding = DataBindingUtil.inflate<ImageCarouselLayoutBinding>(layoutInflater, R.layout.image_carousel_layout, mContentViewBinding.carouselsLayout, false)
        imageCarouselLayoutBinding.data = carousel
        imageCarouselLayoutBinding.carouselBannerViewPager.adapter = carousel.banners?.let { HomePageBannerVpAdapter(this, it) }
        imageCarouselLayoutBinding.carouselBannerViewPager.offscreenPageLimit = 2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            imageCarouselLayoutBinding.carouselBannerViewPager.pageMargin = resources.displayMetrics.widthPixels / -16
        if (AppSharedPref.getStoreCode(this) == "ar")
            imageCarouselLayoutBinding.carouselBannerViewPager.rotationY = 180f
        if (ENABLE_HOME_BANNER_AUTO_SCROLLING) {
            try {
                val imageCarouselSwitcherTimer = Timer()
                imageCarouselSwitcherTimer.scheduleAtFixedRate(BannerSwitchTimerTask(imageCarouselLayoutBinding.carouselBannerViewPager, carousel.banners!!.size), ((mBannerSwitcherTimerList.size % 3) * 1000).toLong(), DEFAULT_TIME_TO_SWITCH_BANNER_IN_MILLIS.toLong())
                mBannerSwitcherTimerList.add(imageCarouselSwitcherTimer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mContentViewBinding.carouselsLayout.addView(imageCarouselLayoutBinding.root)
    }

    fun setupRecentlyViewedCarouselsLayout() {
        val recentlyViewedProductsList = mDataBaseHandler.getRecentlyViewedProducts(AppSharedPref.getStoreId(this), AppSharedPref.getCurrencyCode(this))
        if (!AppSharedPref.getRecentlyViewedProductsEnabled(this) || recentlyViewedProductsList.isEmpty()) {
            mContentViewBinding.recentlyViewedProducts.visibility = View.GONE
        } else {
            if (mContentViewBinding.recentlyViewedProductsRv.adapter == null) {
                mContentViewBinding.recentlyViewedProductsRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
                mContentViewBinding.recentlyViewedProductsRv.isNestedScrollingEnabled = false
            }
            mContentViewBinding.recentlyViewedProductsRv.adapter = ProductCarouselHorizontalRvAdapter(this, recentlyViewedProductsList)
            mContentViewBinding.recentlyViewedProducts.visibility = View.VISIBLE
        }
    }

    private fun setupFeaturesCategoriesRv(carousel: Carousel) {
        if (!carousel.featuredCategories.isNullOrEmpty()) {
            val categoryCarouselLayoutBinding = DataBindingUtil.inflate<CategoryCarouselLayoutBinding>(layoutInflater, R.layout.category_carousel_layout, mContentViewBinding.carouselsLayout, false)
            categoryCarouselLayoutBinding.data = carousel
            categoryCarouselLayoutBinding.themeType = mHomePageDataModel.themeType

            if (mHomePageDataModel.themeType == 1) {
                categoryCarouselLayoutBinding.featuredCategoriesRv.layoutManager = GridLayoutManager(this, 4)
                categoryCarouselLayoutBinding.featuredCategoriesRv.adapter = carousel.featuredCategories?.let { FeaturedCategoriesRvAdapter(this, it, ConstantsHelper.VIEW_TYPE_GRID) }
            } else {
                categoryCarouselLayoutBinding.featuredCategoriesRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_generic).toInt()))

                categoryCarouselLayoutBinding.featuredCategoriesRv.adapter = carousel.featuredCategories?.let { FeaturedCategoriesRvAdapter(this, it, ConstantsHelper.VIEW_TYPE_LIST) }
                categoryCarouselLayoutBinding.featuredCategoriesRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            }
            mContentViewBinding.carouselsLayout.addView(categoryCarouselLayoutBinding.root)
        }
    }

    private fun setupOfferBannerRv(carousel: Carousel) {
        if (!carousel.banners.isNullOrEmpty()) {
            val bannerCarouselLayoutBinding = DataBindingUtil.inflate<BannerCarouselLayoutBinding>(layoutInflater, R.layout.banner_carousel_layout, mContentViewBinding.carouselsLayout, false)
            bannerCarouselLayoutBinding.data = carousel
            bannerCarouselLayoutBinding.themeType = mHomePageDataModel.themeType

            if (mHomePageDataModel.themeType == 1) {

                bannerCarouselLayoutBinding.carouselBannerViewPager.adapter = carousel.banners?.let { HomePageTopBannerVpAdapter(this, it) }
                if (AppSharedPref.getStoreCode(this) == "ar")
                    bannerCarouselLayoutBinding.carouselBannerViewPager.rotationY = 180f
                if (ENABLE_HOME_BANNER_AUTO_SCROLLING) {
                    try {
                        val imageCarouselSwitcherTimer = Timer()
                        imageCarouselSwitcherTimer.scheduleAtFixedRate(BannerSwitchTimerTask(bannerCarouselLayoutBinding.carouselBannerViewPager, carousel.banners!!.size), ((mOfferSwitcherTimerList.size % 3) * 1000).toLong(), DEFAULT_TIME_TO_SWITCH_BANNER_IN_MILLIS.toLong())
                        mOfferSwitcherTimerList.add(imageCarouselSwitcherTimer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                bannerCarouselLayoutBinding.bannerDotsTabLayout.setupWithViewPager(bannerCarouselLayoutBinding.carouselBannerViewPager, true)

            } else {
                if (bannerCarouselLayoutBinding.offerBannersRv.adapter == null) {
                    bannerCarouselLayoutBinding.offerBannersRv.isNestedScrollingEnabled = false
                }
                bannerCarouselLayoutBinding.offerBannersRv.adapter = carousel.banners?.let { OfferBannersRvAdapter(this, it) }
            }
            mContentViewBinding.carouselsLayout.addView(bannerCarouselLayoutBinding.root)

        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle?.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //menu.findItem(R.id.menu_item_notification)?.isVisible = false
        menu.findItem(R.id.menu_item_search).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupNavDrawerStart()
        setupRecentlyViewedCarouselsLayout()
        if (this::inAppUpdateHelper.isInitialized) {
            inAppUpdateHelper.checkUpdateFinished()
        }
    }

    override fun onBackPressed() {
        if (mContentViewBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mContentViewBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (mMaterialSearchView.isOpen()) {
                mMaterialSearchView.closeSearch()
            } else {
                val time = System.currentTimeMillis()
                if (time - mBackPressedTime > 2000) {
                    mBackPressedTime = time
                    Toast.makeText(this, resources.getString(R.string.press_back_to_exit), Toast.LENGTH_SHORT).show()
                } else {
//                    finish()

                    val a = Intent(Intent.ACTION_MAIN)
                    a.addCategory(Intent.CATEGORY_HOME)
                    a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(a)
                }
            }
        }
    }

    private inner class BannerSwitchTimerTask(private val mViewPager: ViewPager, private val mBannerSize: Int) : TimerTask() {

        var firstTime = true

        override fun run() {
            try {
                runOnUiThread {
                    if (mViewPager.currentItem == mBannerSize - 1) {
                        mViewPager.currentItem = 0
                    } else {
                        if (firstTime) {
                            mViewPager.currentItem = mViewPager.currentItem
                            firstTime = false
                        } else {
                            mViewPager.currentItem = mViewPager.currentItem + 1
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_LOGIN -> {
                    callApi()
                }
                RC_QR_LOGIN -> {
                    val result = data?.getStringExtra("SCANNED_DATA")
                    if (result != null) {
                        callWebLoginApi(result)
                    } else {
                        ToastHelper.showToast(this@HomeActivity, getString(R.string.something_went_wrong))
                    }
                }
                ConstantsHelper.APP_UPDATE_REQUEST_CODE -> {
                    inAppUpdateHelper.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun callWebLoginApi(token: String) {
        mContentViewBinding.progressBar = true
        ApiConnection.customerWebLogin(this, token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(this, false) {
                    override fun onNext(baseModel: BaseModel) {
                        super.onNext(baseModel)
                        mContentViewBinding.progressBar = false
                        ToastHelper.showToast(this@HomeActivity, baseModel.message)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.progressBar = false
                        e.message?.let { ToastHelper.showToast(this@HomeActivity, it) }
                    }
                })
    }

    private fun checkForCartOpening() {
        if (intent.getBooleanExtra(BUNDLE_KEY_OPEN_CART, false)) {
            val cartBottomSheet = supportFragmentManager.findFragmentByTag(CartBottomSheetFragment::class.java.simpleName)
            if (cartBottomSheet == null) {
                CartBottomSheetFragment().show(supportFragmentManager, CartBottomSheetFragment::class.java.simpleName)
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
            if (result != null && java.lang.Double.parseDouble(getVersionName(this)) < java.lang.Double.parseDouble(result)) {
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

    private fun checkForArSupport() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (availability.isTransient) {
            Handler(Looper.getMainLooper()).postDelayed({ this.checkForArSupport() }, 200)
        } else {
            AppSharedPref.setIsArSupported(this, availability.isSupported)
        }
    }
    private fun setOnBoardingVersion(responseModel: HomePageDataModel) {
        if (!responseModel.walkThroughVersion.isNullOrEmpty() && AppSharedPref.getOnBoardVersion(this).toDouble()<responseModel.walkThroughVersion!!.toDouble()){
            AppSharedPref.setShowOnBoardVersion(this, true)
        }
    }
}