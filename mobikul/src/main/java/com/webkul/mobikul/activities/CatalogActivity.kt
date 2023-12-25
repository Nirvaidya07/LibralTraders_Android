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

package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.webkul.arcore.activities.ArActivity
import com.webkul.arcore.activities.CameraWithImageActivity
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.CatalogProductsRvAdapter
import com.webkul.mobikul.adapters.CategoryBannerVpAdapter
import com.webkul.mobikul.adapters.CriteriaDataAdapter
import com.webkul.mobikul.databinding.ActivityCatalogBinding
import com.webkul.mobikul.fragments.EmptyFragment
import com.webkul.mobikul.handlers.CatalogActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION
import com.webkul.mobikul.helpers.ConstantsHelper.RC_AR
import com.webkul.mobikul.helpers.ConstantsHelper.VIEW_TYPE_GRID
import com.webkul.mobikul.models.catalog.CatalogProductsResponseModel
import com.webkul.mobikul.models.homepage.BannerImage
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.github.inflationx.calligraphy3.CalligraphyTypefaceSpan
import io.github.inflationx.calligraphy3.TypefaceUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import retrofit2.HttpException
import java.util.*

open class CatalogActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityCatalogBinding
    var mFromNotification: Boolean = false

    var mPageNumber = 1
    var mCatalogType = ""
    var mCatalogName = ""
    var mCatalogId = ""
    var mSortingInputJson = JSONArray()
    var mFilterInputJson = JSONArray()
    var mSellerAttributesIdOptionCodeMap: HashMap<String, String> = HashMap()
    var mSelectedProduct: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_catalog)
        startInitialization(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        removeEmptyLayout()
        startInitialization(intent!!)
    }

    open fun startInitialization(intent: Intent) {
        if (intent.hasExtra(BUNDLE_KEY_CATALOG_TYPE) && intent.hasExtra(BUNDLE_KEY_CATALOG_TITLE) && intent.hasExtra(BUNDLE_KEY_CATALOG_ID)) {
            mFromNotification = intent.hasExtra(BUNDLE_KEY_FROM_NOTIFICATION)
            mCatalogType = intent.getStringExtra(BUNDLE_KEY_CATALOG_TYPE)!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCatalogName = Html.fromHtml(intent.getStringExtra(BUNDLE_KEY_CATALOG_TITLE), Html.FROM_HTML_MODE_LEGACY).toString()

            } else {
                mCatalogName = Html.fromHtml(intent.getStringExtra(BUNDLE_KEY_CATALOG_TITLE)).toString()
            }
            mCatalogId = intent.getStringExtra(BUNDLE_KEY_CATALOG_ID)!!
        } else {
            ToastHelper.showToast(this, getString(R.string.something_went_wrong))
        }
        initSupportActionBar()
        mPageNumber = 1
        callApi()
        checkAndLoadLocalData()
    }

    override fun initSupportActionBar() {
        setSupportActionBar(mContentViewBinding.toolbar)
        val title = SpannableString(mCatalogName)
        title.setSpan(CalligraphyTypefaceSpan(TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    open fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getCatalogProductData" + AppSharedPref.getStoreId(this) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCustomerToken(this) + mCatalogType + mCatalogId + mPageNumber + mSortingInputJson + mFilterInputJson)
        print(mSortingInputJson)
        ApiConnection.getCatalogProductData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mCatalogType, mCatalogId, mPageNumber++, mSortingInputJson, mFilterInputJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CatalogProductsResponseModel>(this, false) {
                    override fun onNext(catalogProductsResponseModel: CatalogProductsResponseModel) {
                        super.onNext(catalogProductsResponseModel)
                        mContentViewBinding.loading = false
                        if (catalogProductsResponseModel.success) {
                            onSuccessfulResponse(catalogProductsResponseModel)
                        } else {
                            onFailureResponse(catalogProductsResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            val catalogResponse =mObjectMapper.readValue(response, CatalogProductsResponseModel::class.java)
                            if (catalogResponse.productList.isNotEmpty())
                                onSuccessfulResponse(catalogResponse)
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

    fun onSuccessfulResponse(catalogProductsResponseModel: CatalogProductsResponseModel) {
        if (mPageNumber == 2) {
            mContentViewBinding.data = catalogProductsResponseModel
            mContentViewBinding.handler = CatalogActivityHandler(this)

            if (mContentViewBinding.data!!.productList.isEmpty() && mContentViewBinding.data!!.bannerImage.isEmpty()) {
                if (mFilterInputJson.length() == 0)
                    mContentViewBinding.productsOptionsLayout.visibility = View.GONE
                addEmptyLayout()
            } else {
                removeEmptyLayout()
                setupBannersViewPager(catalogProductsResponseModel.bannerImage)
                setupCriteriaDataRv()
                setupProductsRv()
            }
        } else {
            mContentViewBinding.data!!.productList.addAll(catalogProductsResponseModel.productList)
            mContentViewBinding.productsRv.adapter?.notifyItemRangeChanged(mContentViewBinding.data!!.productList.size - catalogProductsResponseModel.productList.size, mContentViewBinding.data!!.productList.size)
        }
    }

    private fun setupCriteriaDataRv() {
        mContentViewBinding.criteriaDataRv.adapter = CriteriaDataAdapter(this, mContentViewBinding.data!!.criteriaData)
    }

    private fun setupProductsRv() {
        if (mContentViewBinding.productsRv.adapter == null) {
            if (AppSharedPref.getViewType(this) == VIEW_TYPE_GRID) {
                mContentViewBinding.productsRv.layoutManager = GridLayoutManager(this, 2)
            } else {
                mContentViewBinding.productsRv.layoutManager = LinearLayoutManager(this)
            }
            mContentViewBinding.productsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lastCompletelyVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!mContentViewBinding.loading!! && mContentViewBinding.data!!.productList.size < mContentViewBinding.data!!.totalCount
                            && lastCompletelyVisibleItemPosition > mContentViewBinding.data!!.productList.size - 4) {
                        callApi()
                    }
                }
            })
        }
        mContentViewBinding.productsRv.adapter = CatalogProductsRvAdapter(this, mContentViewBinding.data!!.productList)
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


    private fun onFailureResponse(catalogProductsResponseModel: CatalogProductsResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                catalogProductsResponseModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mPageNumber--
                    callApi()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        })
    }

    open fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this@CatalogActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@CatalogActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@CatalogActivity, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        mPageNumber--
                        callApi()
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }
    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, CatalogProductsResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@CatalogActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@CatalogActivity, error),
                                    false,
                                    getString(R.string.try_again),
                                    { dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                        callApi()
                                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                                finish()
                            })
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

    private fun addEmptyLayout() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.catalog_product_list_layout, EmptyFragment.newInstance("empty_cart.json", getString(R.string.empty_product_catalog), getString(R.string.try_different_category_or_search_keyword_maybe)), EmptyFragment::class.java.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun removeEmptyLayout() {
        val emptyFragment = supportFragmentManager.findFragmentByTag(EmptyFragment::class.java.simpleName)
        if (emptyFragment != null) {
            supportFragmentManager.beginTransaction().remove(emptyFragment).commitAllowingStateLoss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allPermissionsGranted = true
        for (eachGrantResult in grantResults) {
            if (eachGrantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
            }
        }
        if (allPermissionsGranted) {
            if (requestCode == RC_AR) {
                startArActivity()
            }
        }
    }

    fun startArActivity() {
        val intent: Intent = if (mContentViewBinding.data!!.productList[mSelectedProduct].arType == "3D") {
            Intent(this, ArActivity::class.java)
        } else {
            Intent(this, CameraWithImageActivity::class.java)
        }
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME, mContentViewBinding.data!!.productList[mSelectedProduct].name)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_AR_MODEL_URL, mContentViewBinding.data!!.productList[mSelectedProduct].arModelUrl)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (mFromNotification) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            super.onBackPressed()
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