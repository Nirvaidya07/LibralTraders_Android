package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.CategoryBannerVpAdapter
import com.webkul.mobikul.adapters.ProductCarouselHorizontalRvAdapter
import com.webkul.mobikul.adapters.SubCategoriesRvAdapter
import com.libraltraders.android.databinding.ActivitySubCategoryBinding
import com.webkul.mobikul.handlers.SubCategoryActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.catalog.SubCategoryResponseModel
import com.webkul.mobikul.models.homepage.BannerImage
import com.webkul.mobikul.models.homepage.Category
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

class SubCategoryActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivitySubCategoryBinding
    private var mCategoryId: String = ""
    private var mCategoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_sub_category)
        startInitialization()
    }

    fun startInitialization() {
        mCategoryName = intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE)!!
        mCategoryId = intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_ID)!!
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        val title = SpannableString(mCategoryName)
        title.setSpan(CalligraphyTypefaceSpan(TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getSubCategoryData" + AppSharedPref.getStoreId(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCustomerToken(this) + mCategoryId)
        ApiConnection.getSubCategoryData(this
                , mDataBaseHandler.getETagFromDatabase(mHashIdentifier)
                , mCategoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<SubCategoryResponseModel>(this, false) {
                    override fun onNext(subCategoryResponseModel: SubCategoryResponseModel) {
                        super.onNext(subCategoryResponseModel)
                        mContentViewBinding.loading = false
                        if (subCategoryResponseModel.success) {
                            onSuccessfulResponse(subCategoryResponseModel)
                        } else {
                            onFailureResponse(subCategoryResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
        checkAndLoadLocalData()
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, SubCategoryResponseModel::class.java))
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

    private fun onSuccessfulResponse(subCategoryResponseModel: SubCategoryResponseModel) {
        subCategoryResponseModel.id = mCategoryId
        subCategoryResponseModel.name = mCategoryName
        mContentViewBinding.data = subCategoryResponseModel
        mContentViewBinding.handler = SubCategoryActivityHandler(this)

        // Setup Banner View Pager
        if (subCategoryResponseModel.bannerImage.isNotEmpty()) {
            setupBannersViewPager(subCategoryResponseModel.bannerImage)
        }

        // Setup Products data
        if (subCategoryResponseModel.hotSeller.isNotEmpty()) {
            setupHotSellersLayout(subCategoryResponseModel.hotSeller)
        }

        // Setup Subcategories data
        if (subCategoryResponseModel.subCategoriesList.isNotEmpty()) {
            setupSubCategoriesLayout(subCategoryResponseModel.subCategoriesList)
        }

        // Setup Products data
        if (subCategoryResponseModel.productList.isNotEmpty()) {
            setupProductsLayout(subCategoryResponseModel.productList)
        }
    }

    private fun setupBannersViewPager(bannerImage: ArrayList<BannerImage>) {
        if (mContentViewBinding.categoryBannerViewPager.adapter == null) {
            mContentViewBinding.categorySliderDotsTabLayout.setupWithViewPager(mContentViewBinding.categoryBannerViewPager, true)
            val imageCarouselSwitcherTimer = Timer()
            imageCarouselSwitcherTimer.scheduleAtFixedRate(BannerSwitchTimerTask(mContentViewBinding.categoryBannerViewPager, bannerImage.size), 1000.toLong(), ApplicationConstants.DEFAULT_TIME_TO_SWITCH_BANNER_IN_MILLIS.toLong())
        }
        mContentViewBinding.categoryBannerViewPager.adapter = CategoryBannerVpAdapter(this, bannerImage)
        mContentViewBinding.categoryBannerViewPager.offscreenPageLimit = 2
    }

    private fun setupHotSellersLayout(productsList: ArrayList<ProductTileData>) {
        if (mContentViewBinding.hotSellersCarouselRv.adapter == null)
            mContentViewBinding.hotSellersCarouselRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
        mContentViewBinding.hotSellersCarouselRv.adapter = ProductCarouselHorizontalRvAdapter(this, productsList)
        mContentViewBinding.hotSellersCarouselRv.isNestedScrollingEnabled = false
    }

    private fun setupSubCategoriesLayout(subCategoriesList: ArrayList<Category>) {
        mContentViewBinding.subCategoriesRv.adapter = SubCategoriesRvAdapter(this, subCategoriesList)
    }

    private fun setupProductsLayout(productsList: ArrayList<ProductTileData>) {
        if (mContentViewBinding.productsCarouselRv.adapter == null)
            mContentViewBinding.productsCarouselRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
        mContentViewBinding.productsCarouselRv.adapter = ProductCarouselHorizontalRvAdapter(this, productsList)
        mContentViewBinding.productsCarouselRv.isNestedScrollingEnabled = false
    }

    private fun onFailureResponse(subCategoryResponseModel: SubCategoryResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                subCategoryResponseModel.message,
                false,
                getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }
                , getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        })
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
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }
                    , getString(R.string.dismiss)
                    , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }

    private inner class BannerSwitchTimerTask internal constructor(private val mViewPager: ViewPager, private val mBannerSize: Int) : TimerTask() {

        internal var firstTime = true

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
}
