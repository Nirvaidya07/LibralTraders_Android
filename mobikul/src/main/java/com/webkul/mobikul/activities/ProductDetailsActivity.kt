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

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.fasterxml.jackson.databind.ObjectMapper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.webkul.arcore.activities.MeasurementActivity
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.*
import com.libraltraders.android.databinding.ActivityProductDetailsBinding
import com.webkul.mobikul.handlers.ProductDetailsActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_WISHLIST
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ITEM_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.ConstantsHelper.RC_AR_MEASURE
import com.webkul.mobikul.helpers.ConstantsHelper.RC_LOGIN
import com.webkul.mobikul.helpers.ConstantsHelper.RC_PICK_SINGLE_FILE
import com.webkul.mobikul.helpers.ConstantsHelper.RC_WISHLIST
import com.webkul.mobikul.helpers.ConstantsHelper.RC_WRITE_TO_EXTERNAL_STORAGE
import com.webkul.mobikul.helpers.DownloadHelper.Companion.downloadFile
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.catalog.CartItem
import com.webkul.mobikul.models.product.Attribute
import com.webkul.mobikul.models.product.ImageGalleryData
import com.webkul.mobikul.models.product.SwatchData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.github.inflationx.calligraphy3.TypefaceUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class ProductDetailsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityProductDetailsBinding
    private var mFromNotification: Boolean = false

    var mItemId: String = ""

    var mProductId: String = ""
    private var mProductName: String = ""
    private var mProductImage: String = ""
    private var mProductDominantColor: String = ""

    private var mSeletedCustomOption: Int = 0
    var mSelectedImageUri = HashMap<Int, Uri>()

    var cartData: CartItem? = CartItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_product_details)

        startInitialization()
    }

    override fun initSupportActionBar() {
        super.initSupportActionBar()
        supportActionBar?.title=mProductName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.menu_item_notification)?.isVisible = false
        if (ENABLE_WISHLIST && AppSharedPref.isWishlistEnabled(this)) {
            menu.findItem(R.id.menu_item_wishlist)?.isVisible = true
        }
        Utils.setMenuItemIconColor(menu, this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_item_wishlist -> {
                if (AppSharedPref.isLoggedIn(this)) {
                    startActivityForResult(
                        Intent(this, MyWishListActivity::class.java),
                        RC_WISHLIST
                    )
                } else {
                    if (mContentViewBinding.handler != null)
                        mContentViewBinding.handler?.showLoginAlertDialog(getString(R.string.login_to_see_wishlist))
                }
            }
        }
        return true
    }

    private fun startInitialization() {
        getDataFromIntent()
        initSupportActionBar()
        setTemporaryData()
        mHashIdentifier = Utils.getMd5String(
            "getProductPageData" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(
                this
            ) + AppSharedPref.getQuoteId(this) + AppSharedPref.getCurrencyCode(this) + mProductId
        )
        checkAndLoadLocalData()
        callApi()
    }

    private fun getDataFromIntent() {
        mFromNotification = intent.hasExtra(BUNDLE_KEY_FROM_NOTIFICATION)

        if (intent.hasExtra(BUNDLE_KEY_ITEM_ID)) {
            mItemId = intent.getStringExtra(BUNDLE_KEY_ITEM_ID)!!
            mContentViewBinding.floatingAddToCartBtn.visibility = View.GONE
            mContentViewBinding.staticAddToCartBtn.visibility = View.GONE
            mContentViewBinding.floatingBuyNowBtn.text = getString(R.string.update_cart)
            mContentViewBinding.staticBuyNowBtn.text = getString(R.string.update_cart)
        } else {
            LocaleUtils.updateConfig(this)
            // retrieve resources from desired locale
            mContentViewBinding.floatingBuyNowBtn.text = getString(R.string.buy_now)
            mContentViewBinding.floatingAddToCartBtn.text = getString(R.string.add_to_cart)
            mContentViewBinding.staticBuyNowBtn.text = getString(R.string.buy_now)
            mContentViewBinding.staticAddToCartBtn.text = getString(R.string.add_to_cart)
            mContentViewBinding.descriptionHeading.text = getString(R.string.details)
            mContentViewBinding.moreInformationHeading.text = getString(R.string.more_information)
            mContentViewBinding.qtyTv.text = getString(R.string.quantity)
        }

        mProductId = intent.getStringExtra(BUNDLE_KEY_PRODUCT_ID) ?: ""
        mProductName = intent.getStringExtra(BUNDLE_KEY_PRODUCT_NAME) ?: ""
        mProductImage = intent.getStringExtra(BUNDLE_KEY_PRODUCT_IMAGE) ?: ""
        mProductDominantColor = intent.getStringExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR) ?: ""
        FirebaseAnalyticsHelper.logViewItemEvent(mProductId, mProductName)

    }

    private fun setTemporaryData() {
        val productImage: ArrayList<ImageGalleryData> = ArrayList()
        val imageGalleryData = ImageGalleryData()
        imageGalleryData.dominantColor = mProductDominantColor
        productImage.add(imageGalleryData)
        mContentViewBinding.productSliderViewPager.adapter =
            ProductPageSliderAdapter(this, mProductName, productImage)
        mContentViewBinding.productSliderDotsTabLayout.setupWithViewPager(
            mContentViewBinding.productSliderViewPager,
            true
        )
        if (AppSharedPref.getStoreCode(this) == "ar")
            mContentViewBinding.productSliderDotsTabLayout.rotationY = 180f

        mContentViewBinding.productName = mProductName
    }

    open fun callApi() {
        mContentViewBinding.loading = true


        ApiConnection.getProductPageData(
            this,
            mDataBaseHandler.getETagFromDatabase(mHashIdentifier),
            mProductId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCustomCallback<ProductDetailsPageModel>(this, false) {
                override fun onNext(productDetailsPageModel: ProductDetailsPageModel) {
                    super.onNext(productDetailsPageModel)
                    mContentViewBinding.loading = false
                    if (productDetailsPageModel.success) {
                        if (AppSharedPref.getRecentlyViewedProductsEnabled(this@ProductDetailsActivity) && NetworkHelper.isNetworkAvailable(this@ProductDetailsActivity)) {
                            mDataBaseHandler.addRecentlyViewed(
                                AppSharedPref.getStoreId(this@ProductDetailsActivity),
                                AppSharedPref.getCurrencyCode(this@ProductDetailsActivity),
                                productDetailsPageModel.id,
                                ObjectMapper().writeValueAsString(productDetailsPageModel)
                            )
                        }
                        onSuccessfulResponse(productDetailsPageModel)
                    } else {
                        onFailureResponse(productDetailsPageModel)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    mContentViewBinding.loading = false
                    onErrorResponse(e)
                }
            })

    }

    open fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<String> {
                override fun onNext(response: String) {
                    if (response.isNotBlank()) {
                        onSuccessfulResponse(
                            mObjectMapper.readValue(
                                response,
                                ProductDetailsPageModel::class.java
                            )
                        )
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

    fun onSuccessfulResponse(productDetailsPageModel: ProductDetailsPageModel) {
        Log.d("TAG", "getProductPageData: ${productDetailsPageModel.groupedPrice}>Price${productDetailsPageModel.groupedData}>>${productDetailsPageModel.finalPrice}")
        mContentViewBinding.loading=false
        mContentViewBinding.data = productDetailsPageModel
        mContentViewBinding.handler = ProductDetailsActivityHandler(this)

        setupCartUpdate()
        setupImageGallery()
        setupTierPriceGallery()
        setupProductOptions()
        setupMoreInfoRv()
        setupReviewsLayoutAndRv()
        setupRelatedProductsRv()
        setupUpsellProductsRv()
        setupFloatingBuyLayoutHiding()

        mContentViewBinding.executePendingBindings()
        mContentViewBinding.invalidateAll()
    }

    private fun setupCartUpdate() {
        if (intent.hasExtra(BundleKeysHelper.BUNDLE_KEY_CART_DATA)) {
            cartData = intent.getParcelableExtra(BundleKeysHelper.BUNDLE_KEY_CART_DATA)!!
            mContentViewBinding.data?.qty = cartData?.qty ?: "1"
            mContentViewBinding.data?.formattedFinalPrice = cartData?.formattedFinalPrice ?: "0"
            mContentViewBinding.data?.formattedPrice = cartData?.formattedPrice ?: "0"
            mContentViewBinding.data?.finalPrice = cartData?.finalPrice ?: 0.0
            mContentViewBinding.data?.price = cartData?.price ?: 0.0
        }
    }

    private fun setupImageGallery() {
        mContentViewBinding.productSliderViewPager.adapter =
            ProductPageSliderAdapter(this, mProductName, mContentViewBinding.data!!.imageGallery)
        mContentViewBinding.productSliderDotsTabLayout.setupWithViewPager(
            mContentViewBinding.productSliderViewPager,
            true
        )
    }

    private fun setupTierPriceGallery() {
        mContentViewBinding.tierPriceRv.adapter =
            TierPriceRvAdapter(this, mContentViewBinding.data!!.tierPrices)
    }

    private fun setupProductOptions() {
        mContentViewBinding.productCustomOptionsContainer.removeAllViews()
        if (mContentViewBinding.data!!.customOptions.size != 0 && mContentViewBinding.data!!.isAvailable) {
            loadCustomOption()
        }

        mContentViewBinding.otherProductOptionsContainer.removeAllViews()
        when (mContentViewBinding.data!!.typeId) {
            "bundle" -> loadBundledProductData()
            "grouped" -> loadGroupedProductData()
            "downloadable" -> loadDownloadableProductData()
            "configurable" -> loadConfigurableProductData()
        }
    }

    private fun loadCustomOption() {
        try {
            mContentViewBinding.productCustomOptionsContainer.removeAllViews()
            for (customOptIndex in 0 until mContentViewBinding.data!!.customOptions.size) {
                setCustomOptionName(customOptIndex)
                when (mContentViewBinding.data!!.customOptions[customOptIndex].type) {
                    "field", "area" -> loadCustomOptionFieldAreaType(customOptIndex)

                    "file" -> loadCustomOptionFileType(customOptIndex)

                    "arfield" -> loadCustomOptionARFieldType(customOptIndex)

                    "drop_down" -> loadCustomOptionDropDownType(customOptIndex)

                    "radio" -> loadCustomOptionRadioType(customOptIndex)

                    "checkbox", "multiple" -> loadCustomOptionCheckBoxType(customOptIndex)

                    "date" -> loadCustomOptionDateType(customOptIndex)

                    "time" -> loadCustomOptionTimeType(customOptIndex)

                    "date_time" -> loadCustomOptionDateTimeType(customOptIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCustomOptionName(customOptIndex: Int) {
        try {
            /*String*/
            val newCustomOptionLabelSpannable =
                SpannableString(mContentViewBinding.data!!.customOptions[customOptIndex].title)
            newCustomOptionLabelSpannable.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_secondary
                    )
                ), 0, newCustomOptionLabelSpannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            val customOptionsName = TextView(this)
            customOptionsName.typeface =
                TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
            customOptionsName.textSize = 14f
            customOptionsName.text = newCustomOptionLabelSpannable
            if (customOptIndex == 0) {
                customOptionsName.setPadding(0, 10, 0, 10)
            } else {
                customOptionsName.setPadding(0, 30, 0, 10)
            }

            var newCustomOptionLabel = ""
            if (mContentViewBinding.data!!.customOptions[customOptIndex].unformatted_default_price != 0.0) {
                var formattedDefaultPrice =
                    mContentViewBinding.data!!.customOptions[customOptIndex].formatted_default_price
                if (mContentViewBinding.data!!.customOptions[customOptIndex].price_type != "fixed") {
                    var price = mContentViewBinding.data!!.finalPrice
                    val unformattedDefaultPrice =
                        mContentViewBinding.data!!.customOptions[customOptIndex].unformatted_default_price!!
                    val pattern = mContentViewBinding.data!!.priceFormat.pattern
                    val precision = mContentViewBinding.data!!.priceFormat.precision
                    price = price * unformattedDefaultPrice / 100
                    val precisionFormat = "%." + precision + "f"
                    val formattedPrice = String.format(precisionFormat, price)
                    formattedDefaultPrice = pattern?.replace("%s".toRegex(), formattedPrice)
                    newCustomOptionLabel += "  (+$formattedDefaultPrice)"
                } else {
                    newCustomOptionLabel += "  (+$formattedDefaultPrice)"
                }
            }
            val prefixText = "As low as "
            val prefixSpannable = SpannableString(prefixText)
            val customOptionNameSpannable = SpannableString(newCustomOptionLabel)
            customOptionNameSpannable.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                ), 0, customOptionNameSpannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            customOptionsName.append(customOptionNameSpannable)

            if (mContentViewBinding.data!!.customOptions[customOptIndex].is_require == 1) {
                val requiredSign = SpannableString("*")
                requiredSign.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    requiredSign.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                val combinedSpannable = SpannableStringBuilder().apply {
                    append(prefixSpannable)
                    append(customOptionNameSpannable)
                }
                customOptionsName.text = combinedSpannable
               // customOptionsName.append(requiredSign)
            }

            mContentViewBinding.productCustomOptionsContainer.addView(customOptionsName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionFieldAreaType(customOptIndex: Int) {
        try {
            val editTextView = AppCompatEditText(this)
            editTextView.setSingleLine()
            editTextView.tag = customOptIndex
            editTextView.textSize = 14f
            editTextView.setPadding(15, 15, 15, 15)
            editTextView.background =
                ContextCompat.getDrawable(this, R.drawable.shape_rect_white_bg_black_border_1_dp)
            editTextView.addTextChangedListener(GenericTextWatcher())
            editTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
            mContentViewBinding.productCustomOptionsContainer.addView(editTextView)

            if (mContentViewBinding.data!!.customOptions[customOptIndex].max_characters != "0") {
                val maxNoOfCharacters = TextView(this)
                maxNoOfCharacters.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                maxNoOfCharacters.textSize = 12f
                maxNoOfCharacters.text = getString(R.string.maximum_number_of_characters_)
                maxNoOfCharacters.append(" " + mContentViewBinding.data!!.customOptions[customOptIndex].max_characters)
                mContentViewBinding.productCustomOptionsContainer.addView(maxNoOfCharacters)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var mSelectedEditText: AppCompatEditText? = null

    private fun loadCustomOptionARFieldType(customOptIndex: Int) {
        try {
            val eachARFieldTypeCustomOptionView = layoutInflater.inflate(
                R.layout.custom_option_ar_field_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            eachARFieldTypeCustomOptionView.tag = customOptIndex

            val arFieldEt =
                eachARFieldTypeCustomOptionView.findViewById<AppCompatEditText>(R.id.ar_field_et)
            arFieldEt.setSingleLine()
            arFieldEt.tag = "arField$customOptIndex"
            arFieldEt.textSize = 14f
            arFieldEt.setPadding(15, 15, 15, 15)
            arFieldEt.addTextChangedListener(GenericTextWatcher())

            val arButtonEt =
                eachARFieldTypeCustomOptionView.findViewById<AppCompatButton>(R.id.ar_button)
            if (mContentViewBinding.data!!.customOptions[customOptIndex].isAr && AppSharedPref.getIsArSupported(
                    this
                )
            ) {
                arButtonEt.setOnClickListener {
                    mSelectedEditText = arFieldEt
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivityForResult(
                            Intent(this, MeasurementActivity::class.java),
                            RC_AR_MEASURE
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val permissions = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            requestPermissions(permissions, RC_AR_MEASURE)
                        }
                    }
                }
            } else {
                arButtonEt.visibility = View.GONE
            }

            mContentViewBinding.productCustomOptionsContainer.addView(
                eachARFieldTypeCustomOptionView
            )

            if (mContentViewBinding.data!!.customOptions[customOptIndex].max_characters != "0") {
                val maxNoOfCharacters = TextView(this)
                maxNoOfCharacters.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                maxNoOfCharacters.textSize = 12f
                maxNoOfCharacters.text = getString(R.string.maximum_number_of_characters_)
                maxNoOfCharacters.append(" " + mContentViewBinding.data!!.customOptions[customOptIndex].max_characters)
                mContentViewBinding.productCustomOptionsContainer.addView(maxNoOfCharacters)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionFileType(customOptIndex: Int) {
        try {
            val eachFileTypeCustomOptionView = layoutInflater.inflate(
                R.layout.custom_option_file_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            eachFileTypeCustomOptionView.tag = customOptIndex

            val fileNameTv =
                eachFileTypeCustomOptionView.findViewById<AppCompatTextView>(R.id.fileSelectedTV)
            fileNameTv.tag = "fileName$customOptIndex"
            fileNameTv.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))

            val browseButton =
                eachFileTypeCustomOptionView.findViewById<AppCompatButton>(R.id.browseButton)
            browseButton.tag = customOptIndex
            browseButton.setOnClickListener {
                mSeletedCustomOption = customOptIndex
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        RC_WRITE_TO_EXTERNAL_STORAGE
                    )
                } else {
                    showFileChooser()
                }
            }

            mContentViewBinding.productCustomOptionsContainer.addView(eachFileTypeCustomOptionView)

            val fileExtension =
                " <b>" + mContentViewBinding.data!!.customOptions[customOptIndex].file_extension + "</b>"

            if (mContentViewBinding.data!!.customOptions[customOptIndex].file_extension != "null") {
                val allowedFileExt = TextView(this)
                allowedFileExt.textSize = 12f
                allowedFileExt.typeface =
                    TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
                allowedFileExt.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_secondary
                    )
                )
                allowedFileExt.text =
                    Html.fromHtml(resources.getString(R.string.allowed_file_extensions_to_upload_) + fileExtension)
                allowedFileExt.setPadding(0, 2, 0, 2)
                mContentViewBinding.productCustomOptionsContainer.addView(allowedFileExt)
            }

            if (mContentViewBinding.data!!.customOptions[customOptIndex].image_size_x != "0") {
                val imageSizeX = TextView(this)
                imageSizeX.typeface =
                    TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
                imageSizeX.setPadding(0, 2, 0, 2)
                imageSizeX.textSize = 12f
                imageSizeX.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))
                val imageSizeXValue =
                    "<b>" + mContentViewBinding.data!!.customOptions[customOptIndex].image_size_x + "</b>"
                imageSizeX.text =
                    Html.fromHtml(resources.getString(R.string.maximum_image_width) + imageSizeXValue)
                mContentViewBinding.productCustomOptionsContainer.addView(imageSizeX)
            }

            if (mContentViewBinding.data!!.customOptions[customOptIndex].image_size_y != "0") {
                val imageSizeY = TextView(this)
                imageSizeY.setPadding(0, 2, 0, 2)
                imageSizeY.textSize = 12f
                imageSizeY.typeface =
                    TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
                imageSizeY.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))
                val imageSizeYValue =
                    "<b>" + mContentViewBinding.data!!.customOptions[customOptIndex].image_size_y + "</b>"
                imageSizeY.text =
                    Html.fromHtml(resources.getString(R.string.maximum_image_height) + imageSizeYValue)
                mContentViewBinding.productCustomOptionsContainer.addView(imageSizeY)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        try {
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_picture)),
                RC_PICK_SINGLE_FILE
            )
        } catch (ex: android.content.ActivityNotFoundException) {
            showToast(this, getString(R.string.file_manager_error), Toast.LENGTH_SHORT)
        }
    }

    private fun loadCustomOptionDropDownType(customOptIndex: Int) {
        try {
            val spinnerOptions = ArrayList<String>()
            spinnerOptions.add(resources.getString(R.string._please_select_))
            val optionValues =
                mContentViewBinding.data!!.customOptions[customOptIndex].optionValues!!

            for (noOfOptions in optionValues.indices) {
                var optionString = optionValues[noOfOptions].title!!
                if (optionValues[noOfOptions].defaultPrice != 0.0) {
                    if (optionValues[noOfOptions].defaultPriceType == "fixed") {
                        optionString += "  +" + optionValues[noOfOptions].formattedDefaultPrice
                    } else {
                        var price = mContentViewBinding.data!!.finalPrice
                        val pattern = mContentViewBinding.data!!.priceFormat.pattern
                        val precision = mContentViewBinding.data!!.priceFormat.precision
                        val defPrice = optionValues[noOfOptions].defaultPrice
                        price = price * defPrice / 100
                        val precisionFormat = "%." + precision + "f"
                        val formattedPrice = String.format(precisionFormat, price)
                        val newPattern = pattern?.replace("%s".toRegex(), formattedPrice)
                        optionString += " +$newPattern"
                    }
                }
                spinnerOptions.add(optionString)
            }

            val spinnerArrayAdapter =
                ArrayAdapter(this, R.layout.custom_spinner_item, spinnerOptions)
            val spinner = AppCompatSpinner(this)
            spinner.tag = customOptIndex
            spinner.background =
                ContextCompat.getDrawable(this, R.drawable.shape_rect_white_bg_black_border_1_dp)
            spinner.adapter = spinnerArrayAdapter
            spinner.setSelection(0, false)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    updatePrice()
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    updatePrice()
                }
            }
            mContentViewBinding.productCustomOptionsContainer.addView(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionRadioType(customOptIndex: Int) {
        try {
            val optionValues = mContentViewBinding.data!!.customOptions[customOptIndex].optionValues
            val radioGroupCustomOption = RadioGroup(this) // Create the RadioGroup
            radioGroupCustomOption.tag = customOptIndex
            var eachOptionInRadioTypeCustomOption = RadioButton(this)

            if (mContentViewBinding.data!!.customOptions[customOptIndex].is_require == 0) {
                eachOptionInRadioTypeCustomOption.text = getString(R.string.none)
                eachOptionInRadioTypeCustomOption.textSize = 14f
                eachOptionInRadioTypeCustomOption.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                radioGroupCustomOption.addView(eachOptionInRadioTypeCustomOption)
                eachOptionInRadioTypeCustomOption.tag = ""
                radioGroupCustomOption.check(eachOptionInRadioTypeCustomOption.id)
            }

            for (noOfOptions in optionValues!!.indices) {

                eachOptionInRadioTypeCustomOption = RadioButton(this)
                eachOptionInRadioTypeCustomOption.text = optionValues[noOfOptions].title!!
                eachOptionInRadioTypeCustomOption.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                eachOptionInRadioTypeCustomOption.tag = optionValues[noOfOptions].optionTypeId

                var optionString: Spannable = SpannableString("")
                if (optionValues[noOfOptions].defaultPrice != 0.0) {
                    if (optionValues[noOfOptions].defaultPriceType == "fixed") {
                        optionString =
                            SpannableString(" (+" + optionValues[noOfOptions].formattedDefaultPrice + ")")
                    } else {
                        var price = mContentViewBinding.data!!.finalPrice
                        val pattern = mContentViewBinding.data!!.priceFormat.pattern
                        val precision = mContentViewBinding.data!!.priceFormat.precision
                        val defPrice = optionValues[noOfOptions].defaultPrice
                        price = price * defPrice / 100
                        val precisionFormat = "%." + precision + "f"
                        val formattedPrice = String.format(precisionFormat, price)
                        val newPattern = pattern?.replace("%s".toRegex(), formattedPrice)
                        optionString = SpannableString(" (+$newPattern)")
                    }
                }
                optionString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                        )
                    ), 0, optionString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                eachOptionInRadioTypeCustomOption.append(optionString)
                radioGroupCustomOption.addView(eachOptionInRadioTypeCustomOption)
            }
            radioGroupCustomOption.setOnCheckedChangeListener { _, _ -> updatePrice() }
            mContentViewBinding.productCustomOptionsContainer.addView(radioGroupCustomOption)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionCheckBoxType(customOptIndex: Int) {
        try {
            val optionValues =
                mContentViewBinding.data!!.customOptions[customOptIndex].optionValues!!
            val checkBoxCustomOptionLayout = LinearLayoutCompat(this)
            checkBoxCustomOptionLayout.orientation = LinearLayoutCompat.VERTICAL
            checkBoxCustomOptionLayout.tag = customOptIndex

            for (noOfOptions in optionValues.indices) {
                val eachCheckBoxTypeOptionValue = CheckBox(this)
                eachCheckBoxTypeOptionValue.tag = optionValues[noOfOptions].optionTypeId
                eachCheckBoxTypeOptionValue.text = optionValues[noOfOptions].title
                eachCheckBoxTypeOptionValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                if (Build.VERSION.SDK_INT < 21) {
                    CompoundButtonCompat.setButtonTintList(eachCheckBoxTypeOptionValue, ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent)))//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                } else {
                    eachCheckBoxTypeOptionValue.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent))//setButtonTintList is accessible directly on API>19
                }
                var optionString: Spannable = SpannableString("")
                if (optionValues[noOfOptions].defaultPrice != 0.0) {
                    if (optionValues[noOfOptions].defaultPriceType == "fixed") {
                        optionString =
                            SpannableString(" (+" + optionValues[noOfOptions].formattedDefaultPrice + ")")
                    } else {
                        var price = mContentViewBinding.data!!.finalPrice
                        val pattern = mContentViewBinding.data!!.priceFormat.pattern
                        val precision = mContentViewBinding.data!!.priceFormat.precision
                        val defPrice = optionValues[noOfOptions].defaultPrice
                        price = price * defPrice / 100
                        val precisionFormat = "%." + precision + "f"
                        val formattedPrice = String.format(precisionFormat, price)
                        val newPattern = pattern?.replace("%s".toRegex(), formattedPrice)
                        optionString = SpannableString(" (+$newPattern)")
                    }
                }
                optionString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                        )
                    ), 0, optionString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                eachCheckBoxTypeOptionValue.append(optionString)
                eachCheckBoxTypeOptionValue.setOnClickListener { updatePrice() }
                eachCheckBoxTypeOptionValue.textSize = 14f
                checkBoxCustomOptionLayout.addView(eachCheckBoxTypeOptionValue)
            }
            mContentViewBinding.productCustomOptionsContainer.addView(checkBoxCustomOptionLayout)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionDateType(customOptIndex: Int) {
        try {
            val myCalendar = Calendar.getInstance()
            val customOptionDateView = layoutInflater.inflate(
                R.layout.custom_option_date_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            customOptionDateView.tag = customOptIndex

            val dateEditText1 = customOptionDateView.findViewById<AppCompatTextView>(R.id.dateET)

            val resetBtn = customOptionDateView.findViewById<AppCompatButton>(R.id.resetBtn)
            resetBtn.tag = customOptIndex
            resetBtn.setOnClickListener {
                dateEditText1.text = ""
                updatePrice()
            }

            val date1 = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "MM/dd/yy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dateEditText1.text = sdf.format(myCalendar.time)
                updatePrice()
            }
            dateEditText1.setOnClickListener {
                DatePickerDialog(
                    this,
                    R.style.AlertDialogTheme,
                    date1,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            mContentViewBinding.productCustomOptionsContainer.addView(customOptionDateView)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionTimeType(customOptIndex: Int) {
        try {
            val customOptionTimeView = layoutInflater.inflate(
                R.layout.custom_option_time_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            customOptionTimeView.tag = customOptIndex
            val timeEditText1 = customOptionTimeView.findViewById<AppCompatTextView>(R.id.timeET)

            val resetBtn = customOptionTimeView.findViewById<AppCompatButton>(R.id.resetBtn)
            resetBtn.tag = customOptIndex
            resetBtn.setOnClickListener {
                timeEditText1.text = ""
                updatePrice()
            }

            timeEditText1.setOnClickListener {
                val currentTime = Calendar.getInstance()
                val hour = currentTime.get(Calendar.HOUR_OF_DAY)
                val minute = currentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                        var aMpM = "AM"
                        if (selectedHour > 11) {
                            aMpM = "PM"
                        }
                        //Make the 24 hour time format to 12 hour time format
                        val currentHour: Int = if (selectedHour > 11) {
                            selectedHour - 12
                        } else {
                            selectedHour
                        }

                        timeEditText1.text =
                            String.format(Locale.US, "%02d", currentHour) + ":" + String.format(
                                Locale.US,
                                "%02d",
                                selectedMinute
                            ) + " " + aMpM

                        updatePrice()
                    },
                    hour,
                    minute,
                    false
                )//true: 24 hour time

                mTimePicker.setTitle(getString(R.string.select_time))
                mTimePicker.show()
            }
            mContentViewBinding.productCustomOptionsContainer.addView(customOptionTimeView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomOptionDateTimeType(customOptIndex: Int) {
        try {
            val customOptionDateLayout = layoutInflater.inflate(
                R.layout.custom_option_date_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            customOptionDateLayout.tag = "date$customOptIndex"
            /*hiding one button so that there are only one button for this type of custom view*/
            customOptionDateLayout.findViewById<View>(R.id.resetBtn).visibility = View.GONE

            val myCalendar = Calendar.getInstance()

            val dateEditText1 = customOptionDateLayout.findViewById<AppCompatTextView>(R.id.dateET)

            mContentViewBinding.productCustomOptionsContainer.addView(customOptionDateLayout)

            val params = LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 5, 0, 0)

            val customOptionTimeView = layoutInflater.inflate(
                R.layout.custom_option_time_view,
                mContentViewBinding.productCustomOptionsContainer,
                false
            )
            customOptionTimeView.tag = "time$customOptIndex"
            customOptionTimeView.layoutParams = params
            val timeLayout = LinearLayoutCompat(this)

            timeLayout.orientation = LinearLayoutCompat.HORIZONTAL

            val timeEditText1 = customOptionTimeView.findViewById<AppCompatTextView>(R.id.timeET)

            val resetDateTimeBtn = customOptionTimeView.findViewById<AppCompatButton>(R.id.resetBtn)
            resetDateTimeBtn.tag = customOptIndex

            val date1 = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "MM/dd/yy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                dateEditText1.text = sdf.format(myCalendar.time)

                if (timeEditText1.text.toString() == "") {
                    val mCurrentTime = Calendar.getInstance()
                    val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                    val minute = mCurrentTime.get(Calendar.MINUTE)
                    val mTimePicker: TimePickerDialog
                    mTimePicker = TimePickerDialog(
                        this,
                        TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                            var aMpM = "AM"
                            if (selectedHour > 11) {
                                aMpM = "PM"
                            }
                            //Make the 24 hour time format to 12 hour time format
                            val currentHour: Int = if (selectedHour > 11) {
                                selectedHour - 12
                            } else {
                                selectedHour
                            }
                            timeEditText1.text =
                                String.format(Locale.US, "%02d", currentHour) + ":" + String.format(
                                    Locale.US,
                                    "%02d",
                                    selectedMinute
                                ) + " " + aMpM
                            updatePrice()
                        },
                        hour,
                        minute,
                        false
                    ) //Yes 24 hour time
                    mTimePicker.setTitle(R.string.select_time)
                    mTimePicker.show()
                }
                updatePrice()
            }
            dateEditText1.setOnClickListener {
                DatePickerDialog(
                    this,
                    R.style.AlertDialogTheme,
                    date1,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            resetDateTimeBtn.setOnClickListener {
                dateEditText1.text = ""
                dateEditText1.error = null
                timeEditText1.text = ""
                timeEditText1.error = null
                updatePrice()
            }

            timeEditText1.setOnClickListener {
                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mCurrentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                        var aMpM = "AM"
                        if (selectedHour > 11) {
                            aMpM = "PM"
                        }
                        //Make the 24 hour time format to 12 hour time format
                        val currentHour: Int = if (selectedHour > 11) {
                            selectedHour - 12
                        } else {
                            selectedHour
                        }
                        timeEditText1.text =
                            currentHour.toString() + ":" + selectedMinute + " " + aMpM

                        if (dateEditText1.text.toString() == "") {
                            DatePickerDialog(
                                this,
                                R.style.AlertDialogTheme,
                                date1,
                                myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            ).show()

                        }
                        updatePrice()
                    },
                    hour,
                    minute,
                    false
                ) //true: 24 hour time
                mTimePicker.setTitle(R.string.select_time)
                mTimePicker.show()
            }
            mContentViewBinding.productCustomOptionsContainer.addView(timeLayout)
            mContentViewBinding.productCustomOptionsContainer.addView(customOptionTimeView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBundledProductData() {
        try {
            if (mContentViewBinding.data!!.isAvailable) {
                if (mContentViewBinding.data!!.bundleOptions.size != 0) {
                    mContentViewBinding.otherProductOptionsContainer.setPadding(
                        resources.getDimension(
                            R.dimen.spacing_normal
                        ).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt() - 15
                    )
                    for (bundleOptIndex in 0 until mContentViewBinding.data!!.bundleOptions.size) {
                        val eachBundleItemNameTV = TextView(this)
                        eachBundleItemNameTV.typeface = TypefaceUtils.load(
                            assets,
                            ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD
                        )
                        eachBundleItemNameTV.textSize = 14f
                        eachBundleItemNameTV.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.text_color_secondary
                            )
                        )
                        if (bundleOptIndex == 0)
                            eachBundleItemNameTV.setPadding(0, 0, 0, 15)
                        else
                            eachBundleItemNameTV.setPadding(0, 30, 0, 15)
                        eachBundleItemNameTV.text =
                            mContentViewBinding.data!!.bundleOptions[bundleOptIndex].title
                        if (mContentViewBinding.data!!.bundleOptions[bundleOptIndex].required == 1) {
                            val requiredSign = SpannableString("*")
                            requiredSign.setSpan(
                                ForegroundColorSpan(Color.RED),
                                0,
                                requiredSign.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            eachBundleItemNameTV.append(requiredSign)
                        }

                        mContentViewBinding.otherProductOptionsContainer.addView(
                            eachBundleItemNameTV
                        )

                        when (mContentViewBinding.data!!.bundleOptions[bundleOptIndex].type) {
                            "select" -> loadDropDownBundleOption(bundleOptIndex)
                            "radio" -> loadRadioBundleOption(bundleOptIndex)
                            "checkbox", "multi" -> loadCheckAndMultiBundleOption(bundleOptIndex)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun loadDropDownBundleOption(bundleOptIndex: Int) {
        try {
            val bundleOptionTypeDropDownLayout = layoutInflater.inflate(
                R.layout.bundle_option_drop_down_layout,
                mContentViewBinding.otherProductOptionsContainer,
                false
            )
            bundleOptionTypeDropDownLayout.tag = "bundleoption$bundleOptIndex"

            val qtyET = bundleOptionTypeDropDownLayout.findViewById<AppCompatTextView>(R.id.qty_et)
            qtyET.tag = bundleOptIndex
            qtyET.addTextChangedListener(GenericTextWatcher())

            val incrementQty =
                bundleOptionTypeDropDownLayout.findViewById<AppCompatTextView>(R.id.increment_qty_iv)
            incrementQty.tag = bundleOptIndex
            incrementQty.setOnClickListener {
                val currentQty = Integer.parseInt(qtyET.text.toString()) + 1
                qtyET.text = currentQty.toInt().toString()
            }
            val decrementQty =
                bundleOptionTypeDropDownLayout.findViewById<AppCompatTextView>(R.id.decrement_qty_iv)
            decrementQty.tag = bundleOptIndex
            decrementQty.setOnClickListener {
                val currentQty = Integer.parseInt(qtyET.text.toString()) - 1
                if (currentQty < 1)
                    qtyET.text = "1"
                else
                    qtyET.text = currentQty.toInt().toString()
            }

            val spinnerOptions = ArrayList<String>()
            val spinnerOptionsKeys = ArrayList<String>()
            spinnerOptions.add(resources.getString(R.string._choose_a_product_))
            spinnerOptionsKeys.add("")

            val dropdownBundleOptionSpinner =
                bundleOptionTypeDropDownLayout.findViewById<AppCompatSpinner>(R.id.dropdown_bundle_option_spinner)

            var defaultSelected = 0
            for (noOfOptions in 0 until mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues.size) {
                val optionString =
                    mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].title
                spinnerOptions.add(optionString)
                spinnerOptionsKeys.add(mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].optionId)
                if (mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].isDefault == "1") {
                    defaultSelected = noOfOptions + 1
                }
            }

            val spinnerArrayAdapter = SpinnerWithTagAdapter(spinnerOptions, spinnerOptionsKeys)
            dropdownBundleOptionSpinner.adapter = spinnerArrayAdapter
            dropdownBundleOptionSpinner.tag = bundleOptIndex

            dropdownBundleOptionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        spinnerView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        try {
                            val bundleOptionPosition = spinnerView.tag as Int
                            val bundleOptionValueKey = selectedItemView?.tag.toString()

                            if (position == 0 || bundleOptionValueKey == "") {
                                qtyET.isEnabled = false
                                qtyET.text = "0"
                                incrementQty.isEnabled = false
                                decrementQty.isEnabled = false
                            } else {
                                val defaultQty =
                                    mContentViewBinding.data!!.bundleOptions[bundleOptionPosition].optionValues[Integer.parseInt(
                                        bundleOptionValueKey
                                    )].defaultQty.toInt()
                                if (defaultQty != 0) {
                                    qtyET.text = defaultQty.toInt().toString()
                                } else {
                                    qtyET.text = "1"
                                }
                                val userDefinedQty =
                                    mContentViewBinding.data!!.bundleOptions[bundleOptionPosition].optionValues[Integer.parseInt(
                                        bundleOptionValueKey
                                    )].isQtyUserDefined
                                if (userDefinedQty == 0) {
                                    incrementQty.isEnabled = false
                                    decrementQty.isEnabled = false
                                } else {
                                    incrementQty.isEnabled = true
                                    decrementQty.isEnabled = true
                                }
                                qtyET.isEnabled =
                                    mContentViewBinding.data!!.bundleOptions[bundleOptionPosition].optionValues[Integer.parseInt(
                                        bundleOptionValueKey
                                    )].isQtyUserDefined == 1
                            }
                            updatePrice()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        updatePrice()
                    }
                }

            dropdownBundleOptionSpinner.setSelection(defaultSelected)

            mContentViewBinding.otherProductOptionsContainer.addView(bundleOptionTypeDropDownLayout)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadRadioBundleOption(bundleOptIndex: Int) {
        try {

            val bundleOptionTypeRadioLayout = layoutInflater.inflate(
                R.layout.bundle_option_radio_layout,
                mContentViewBinding.otherProductOptionsContainer,
                false
            )
            bundleOptionTypeRadioLayout.tag = "bundleoption$bundleOptIndex"
            val qtyET = bundleOptionTypeRadioLayout.findViewById<AppCompatTextView>(R.id.qty_et)
            qtyET.tag = "bundleoption$bundleOptIndex"
            qtyET.addTextChangedListener(GenericTextWatcher())

            val bundleOptionRG =
                bundleOptionTypeRadioLayout.findViewById<RadioGroup>(R.id.bundle_option_rg)
            bundleOptionRG.tag = "bundleoption$bundleOptIndex"

            val incrementQty =
                bundleOptionTypeRadioLayout.findViewById<AppCompatTextView>(R.id.increment_qty_iv)
            incrementQty.tag = "bundleoption$bundleOptIndex"
            incrementQty.setOnClickListener { v ->
                val editText =
                    mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                        v.tag
                    ).findViewById<AppCompatTextView>(R.id.qty_et)
                var currentQty = 0
                currentQty = try {
                    Integer.parseInt(editText.text.toString()) + 1
                } catch (e: NumberFormatException) {
                    ((editText.text.toString()).toInt() + 1).toInt()
                }
                editText.text = currentQty.toInt().toString()
            }
            val decrementQty =
                bundleOptionTypeRadioLayout.findViewById<AppCompatTextView>(R.id.decrement_qty_iv)
            decrementQty.tag = "bundleoption$bundleOptIndex"
            decrementQty.setOnClickListener { v ->
                val editText =
                    mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                        v.tag
                    ).findViewById<AppCompatTextView>(R.id.qty_et)
                var currentQty = 0
                try {
                    currentQty = Integer.parseInt(editText.text.toString()) - 1
                } catch (e: NumberFormatException) {
                    currentQty = ((editText.text.toString()).toInt() - 1).toInt()
                }

                if (currentQty < 1)
                    editText.text = "1"
                else
                    editText.text = currentQty.toInt().toString()
            }

            if (mContentViewBinding.data!!.bundleOptions[bundleOptIndex].required == 0) {
                val noneRadioButton = RadioButton(this)
                noneRadioButton.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    noneRadioButton.buttonTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                }
                noneRadioButton.text = getString(R.string.none)
                noneRadioButton.textSize = 14f
                bundleOptionRG.addView(noneRadioButton)
            }

            for (noOfOptions in 0 until mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues.size) {
                val eachBundleOptionValueRB = RadioButton(this)
                eachBundleOptionValueRB.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    eachBundleOptionValueRB.buttonTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                }
                val optionString =
                    mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].title
                eachBundleOptionValueRB.text = optionString
                eachBundleOptionValueRB.textSize = 14f
                eachBundleOptionValueRB.tag = noOfOptions
                eachBundleOptionValueRB.id = noOfOptions
                bundleOptionRG.addView(eachBundleOptionValueRB)
                if (mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].isDefault == "1") {
                    bundleOptionRG.check(eachBundleOptionValueRB.id)
                    qtyET.text =
                        mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].defaultQty.toInt()
                            .toString()
                    incrementQty.isEnabled =
                        mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].isQtyUserDefined == 1
                    decrementQty.isEnabled =
                        mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].isQtyUserDefined == 1
                }
            }

            bundleOptionRG.setOnCheckedChangeListener { radioGroup, checkedId ->
                try {
                    if (checkedId != -1) {
                        val selectedRadioButton =
                            radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                        val bundleOptionValueKey = selectedRadioButton.tag.toString()
                        if (bundleOptionValueKey.isEmpty()) {
                            qtyET.isEnabled = false
                            qtyET.text = "0"
                        } else {
                            val defaultQty =
                                mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[Integer.parseInt(
                                    bundleOptionValueKey
                                )].defaultQty.toInt()
                            if (defaultQty != 0) {
                                qtyET.text = defaultQty.toInt().toString()
                            } else {
                                qtyET.text = "1"
                            }
                        }
                        incrementQty.isEnabled =
                            mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[Integer.parseInt(
                                bundleOptionValueKey
                            )].isQtyUserDefined == 1
                        decrementQty.isEnabled =
                            mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[Integer.parseInt(
                                bundleOptionValueKey
                            )].isQtyUserDefined == 1
                        updatePrice()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mContentViewBinding.otherProductOptionsContainer.addView(bundleOptionTypeRadioLayout)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCheckAndMultiBundleOption(bundleOptIndex: Int) {
        try {
            val bundleOptionTypeCheckMultiLayout = LinearLayoutCompat(this)
            bundleOptionTypeCheckMultiLayout.orientation = LinearLayoutCompat.VERTICAL
            bundleOptionTypeCheckMultiLayout.tag = "bundleoption$bundleOptIndex"
            bundleOptionTypeCheckMultiLayout.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.material_background
                )
            )

            for (noOfOptions in 0 until mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues.size) {
                val eachCheckTypeBundleOptionValue = CheckBox(this)
                eachCheckTypeBundleOptionValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_primary
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    eachCheckTypeBundleOptionValue.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            R.color.text_color_primary
                        )
                    )
                }
                eachCheckTypeBundleOptionValue.highlightColor =
                    resources.getColor(R.color.text_color_primary)

                val optionString =
                    mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].title
                eachCheckTypeBundleOptionValue.text = optionString
                eachCheckTypeBundleOptionValue.textSize = 14f
                eachCheckTypeBundleOptionValue.tag = noOfOptions
                eachCheckTypeBundleOptionValue.isChecked =
                    mContentViewBinding.data!!.bundleOptions[bundleOptIndex].optionValues[noOfOptions].isDefault == "1"

                eachCheckTypeBundleOptionValue.setOnClickListener { updatePrice() }
                bundleOptionTypeCheckMultiLayout.addView(eachCheckTypeBundleOptionValue)
            }
            mContentViewBinding.otherProductOptionsContainer.addView(
                bundleOptionTypeCheckMultiLayout
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadGroupedProductData() {
        try {
            if (mContentViewBinding.data!!.isAvailable) {
                if (mContentViewBinding.data!!.groupedData.size != 0) {
                    mContentViewBinding.qtyLayout.visibility = View.GONE
                    mContentViewBinding.otherProductOptionsContainer.setPadding(
                        resources.getDimension(
                            R.dimen.spacing_normal
                        ).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt(),
                        resources.getDimension(R.dimen.spacing_normal).toInt()
                    )
                    for (noOfGroupedData in 0 until mContentViewBinding.data!!.groupedData.size) {
                        val eachGroupProductItem = layoutInflater.inflate(
                            R.layout.item_grouped_product,
                            mContentViewBinding.otherProductOptionsContainer,
                            false
                        )
                        eachGroupProductItem.tag = noOfGroupedData


                        if (!mContentViewBinding.data!!.groupedData[noOfGroupedData].isAvailable) {
                            eachGroupProductItem.findViewById<LinearLayoutCompat>(R.id.quantity_container).visibility =
                                View.GONE
                            val editText =
                                eachGroupProductItem.findViewById<AppCompatTextView>(R.id.qty)
                            editText.text = 0.toString()
                            eachGroupProductItem.findViewById<AppCompatTextView>(R.id.outofstock).visibility =
                                View.VISIBLE
                        }

                        (eachGroupProductItem.findViewById(R.id.nameOfGroupedData) as AppCompatTextView).text =
                            mContentViewBinding.data!!.groupedData[noOfGroupedData].name

                        if (mContentViewBinding.data!!.groupedData[noOfGroupedData].isInRange && !mContentViewBinding.data!!.groupedData[noOfGroupedData].specialPrice.equals(
                                "$0.00",
                                true
                            )
                        ) {
                            (eachGroupProductItem.findViewById(R.id.priceOfGroupedProduct) as AppCompatTextView).text =
                                mContentViewBinding.data!!.groupedData[noOfGroupedData].specialPrice
                            (eachGroupProductItem.findViewById(R.id.specialpriceOfGroupedProduct) as AppCompatTextView).text =
                                mContentViewBinding.data!!.groupedData[noOfGroupedData].formattedPrice

                            (eachGroupProductItem.findViewById(R.id.specialpriceOfGroupedProduct) as AppCompatTextView).paintFlags =
                                (eachGroupProductItem.findViewById(R.id.priceOfGroupedProduct) as TextView).paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            eachGroupProductItem.findViewById<AppCompatTextView>(R.id.specialpriceOfGroupedProduct).visibility =
                                View.VISIBLE
                        } else {
                            (eachGroupProductItem.findViewById(R.id.priceOfGroupedProduct) as TextView).text =
                                mContentViewBinding.data!!.groupedData[noOfGroupedData].formattedPrice
                        }

                        val editText =
                            eachGroupProductItem.findViewById<AppCompatTextView>(R.id.qty)
                        editText.text =
                            mContentViewBinding.data!!.groupedData[noOfGroupedData].defaultQty

                        val incrementQty =
                            eachGroupProductItem.findViewById<AppCompatTextView>(R.id.increment_qty_iv)
                        incrementQty.tag = noOfGroupedData
                        incrementQty.setOnClickListener {
                            val currentQty = Integer.parseInt(editText.text.toString()) + 1
                            editText.text = currentQty.toString()
                        }
                        val decrementQty =
                            eachGroupProductItem.findViewById<AppCompatTextView>(R.id.decrement_qty_iv)
                        decrementQty.tag = noOfGroupedData
                        decrementQty.setOnClickListener {
                            val currentQty = Integer.parseInt(editText.text.toString()) - 1
                            if (currentQty < 0)
                                editText.text = "0"
                            else
                                editText.text = currentQty.toString()
                        }

                        val eachGroupedProductImage =
                            eachGroupProductItem.findViewById<AppCompatImageView>(R.id.grouped_product_image)
                        ImageHelper.load(
                            eachGroupedProductImage,
                            mContentViewBinding.data!!.groupedData[noOfGroupedData].thumbNail,
                            null
                        )
                        mContentViewBinding.otherProductOptionsContainer.addView(
                            eachGroupProductItem
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDownloadableProductData() {
        try {
            if (mContentViewBinding.data!!.isAvailable) {
                val linksTitle = TextView(this)
                linksTitle.typeface =
                    TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
                linksTitle.text = mContentViewBinding.data!!.links.title
                linksTitle.textSize = 14f
                linksTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))

                if (mContentViewBinding.data!!.links.linksPurchasedSeparately == 1) {
                    val requiredSign = SpannableString("*")
                    requiredSign.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        requiredSign.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    linksTitle.append(requiredSign)
                }

                mContentViewBinding.otherProductOptionsContainer.setPadding(
                    resources.getDimension(R.dimen.spacing_normal).toInt(),
                    resources.getDimension(R.dimen.spacing_normal).toInt(),
                    resources.getDimension(R.dimen.spacing_normal).toInt(),
                    resources.getDimension(R.dimen.spacing_normal).toInt()
                )
                mContentViewBinding.otherProductOptionsContainer.addView(linksTitle)

                for (linkIndex in 0 until mContentViewBinding.data!!.links.linkData.size) {
                    val eachLinkCheckBox = layoutInflater.inflate(
                        R.layout.item_links_downloadable_product,
                        mContentViewBinding.otherProductOptionsContainer,
                        false
                    ) as CheckBox
                    eachLinkCheckBox.text =
                        mContentViewBinding.data!!.links.linkData[linkIndex].linkTitle
                    eachLinkCheckBox.textSize = 14f
                    eachLinkCheckBox.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.text_color_primary
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        eachLinkCheckBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this,
                                R.color.text_color_primary
                            )
                        )
                    }
                    eachLinkCheckBox.highlightColor = resources.getColor(R.color.text_color_primary)
                    eachLinkCheckBox.setOnClickListener { updatePrice() }

                    if (mContentViewBinding.data!!.links.linksPurchasedSeparately == 0) {
                        eachLinkCheckBox.isEnabled = false
                        eachLinkCheckBox.setButtonDrawable(R.drawable.selector_checkbox)
                    } else if (mContentViewBinding.data!!.links.linkData[linkIndex].price != 0.0) {
                        eachLinkCheckBox.text =
                            "${eachLinkCheckBox.text} (+${mContentViewBinding.data!!.links.linkData[linkIndex].formattedPrice})"
                    }

                    mContentViewBinding.otherProductOptionsContainer.addView(eachLinkCheckBox)
                }
            }

            if (mContentViewBinding.data!!.samples.hasSample) {
                mContentViewBinding.downloadableProductSampleLl.removeAllViews()
                mContentViewBinding.downloadableProductSampleLl.visibility = View.VISIBLE

                val sampleTitle = TextView(this)
                sampleTitle.typeface =
                    TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
                sampleTitle.text = mContentViewBinding.data!!.samples.title
                sampleTitle.textSize = 14f
                sampleTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))
                mContentViewBinding.downloadableProductSampleLl.addView(sampleTitle)

                for (linkDataIndex in 0 until mContentViewBinding.data!!.samples.linkSampleData.size) {
                    val sampleLink = TextView(this)
                    sampleLink.setPadding(
                        resources.getDimension(R.dimen.spacing_generic).toInt() + 15,
                        15,
                        resources.getDimension(R.dimen.spacing_generic).toInt() + 15,
                        0
                    )
                    sampleLink.textSize = 16f
                    sampleLink.text =
                        mContentViewBinding.data!!.samples.linkSampleData[linkDataIndex].sampleTitle
                    sampleLink.setTextColor(ContextCompat.getColor(this, R.color.text_color_link))
                    sampleLink.tag = linkDataIndex
                    sampleLink.setOnClickListener { v ->
                        if (checkPermissions()) {
                            val sampleLinkData =
                                mContentViewBinding.data!!.samples.linkSampleData[v.tag as Int]
                            downloadFile(
                                this,
                                sampleLinkData.url,
                                sampleLinkData.fileName,
                                sampleLinkData.mimeType
                            )
                        }
                    }
                    mContentViewBinding.downloadableProductSampleLl.addView(sampleLink)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showToast(this, getString(R.string.storage_acccess_permission), Toast.LENGTH_LONG)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RC_WRITE_TO_EXTERNAL_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RC_WRITE_TO_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }

    private fun loadConfigurableProductData() {
        try {
            mContentViewBinding.otherProductOptionsContainer.background = null
            mContentViewBinding.data!!.configurableData.attributes?.forEachIndexed { index, element ->
                if (element.swatchType == "visual" || element.swatchType == "text") {
                    addVisualTypeSwatch(index, element)
                } else {
                    addDropDownTypeOptions(index, element)
                }
            }
            initializeConfigurableAttributeOption(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addVisualTypeSwatch(index: Int, element: Attribute) {
        val eachConfigurableAttributeLayout = layoutInflater.inflate(
            R.layout.item_configurable_product_attribute_swatch_layout,
            mContentViewBinding.otherProductOptionsContainer,
            false
        )
        eachConfigurableAttributeLayout.tag = "config$index"

        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).typeface =
            TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).text =
            "${element.label}*"
        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).isAllCaps =
            true

        val eachSwatchRecyclerView =
            eachConfigurableAttributeLayout.findViewById(R.id.configurable_item_rv) as RecyclerView
        eachSwatchRecyclerView.tag = "config$index"
        val attributeSwatchData = ArrayList<SwatchData>()
        try {
            val mainSwatchDataObject =
                JSONObject(mContentViewBinding.data!!.configurableData.swatchData)
            val eachOptionData = mainSwatchDataObject.getJSONObject(element.id)
            val keys = eachOptionData.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                if (eachOptionData.get(key) is JSONObject) {
                    val eachSwatchData = JSONObject(eachOptionData.get(key).toString())
                    val swatchData = SwatchData()
                    swatchData.id = key
                    swatchData.type = eachSwatchData.getString("type")
                    swatchData.value = eachSwatchData.getString("value")
                    cartData?.optionItems?.forEach {
                        if (it.valueIds?.contains(key) == true) {
                            swatchData.isSelected = true
                        }
                    }
                    attributeSwatchData.add(swatchData)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        eachSwatchRecyclerView.addItemDecoration(
            HorizontalMarginItemDecoration(
                resources.getDimension(
                    R.dimen.spacing_generic
                ).toInt()
            )
        )
        eachSwatchRecyclerView.adapter = ProductAttributesSwatchRvAdapter(
            this,
            index,
            element.updateProductPreviewImage,
            attributeSwatchData
        )
        mContentViewBinding.otherProductOptionsContainer.addView(eachConfigurableAttributeLayout)
    }

    private fun addDropDownTypeOptions(index: Int, element: Attribute) {
        val eachConfigurableAttributeLayout = layoutInflater.inflate(
            R.layout.item_configurable_product_attribute_dropdown_layout,
            mContentViewBinding.otherProductOptionsContainer,
            false
        )
        eachConfigurableAttributeLayout.tag = "config$index"

        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).typeface =
            TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).text =
            "${element.label}*"
        (eachConfigurableAttributeLayout.findViewById(R.id.configurable_attribute_label) as TextView).isAllCaps =
            true

        val eachConfigurableAttributeSpinner =
            eachConfigurableAttributeLayout.findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
        eachConfigurableAttributeSpinner.tag = index

        val spinnerOptions = ArrayList<String>()
        val spinnerTags = ArrayList<String>()
        mContentViewBinding.data!!.configurableData.chooseText?.let { spinnerOptions.add(it) }
        spinnerTags.add("")
        for (attributeOptionPosition in 0 until (element.options?.size ?: 0)) {
            element.options?.get(attributeOptionPosition)?.label?.let { spinnerOptions.add(it) }
            spinnerTags.add(attributeOptionPosition.toString())
        }

        eachConfigurableAttributeSpinner.adapter =
            SpinnerWithTagAdapter(spinnerOptions, spinnerTags)
        mContentViewBinding.otherProductOptionsContainer.addView(eachConfigurableAttributeLayout)
    }

    fun initializeConfigurableAttributeOption(position: Int) {
        try {
            if (mContentViewBinding.data!!.configurableData.attributes?.get(position)?.swatchType == "visual" || mContentViewBinding.data!!.configurableData.attributes?.get(
                    position
                )?.swatchType == "text"
            ) {
                val eachSwatchRecyclerView =
                    mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                        "config$position"
                    ).findViewById<RecyclerView>(R.id.configurable_item_rv)
                val recyclerViewOptions =
                    (eachSwatchRecyclerView.adapter as ProductAttributesSwatchRvAdapter).getSwatchValuesData()

                for (noOfAttribute in recyclerViewOptions.indices) {
                    if (canAddCurrentAttributeOption(
                            position,
                            noOfAttribute,
                            recyclerViewOptions[noOfAttribute].id
                        )
                    ) {
                        recyclerViewOptions[noOfAttribute].isEnabled = true
                    } else {
                        recyclerViewOptions[noOfAttribute].isEnabled = false
                        recyclerViewOptions[noOfAttribute].isSelected = false
                    }
                }
                eachSwatchRecyclerView.adapter!!.notifyDataSetChanged()
            } else {
                val currentConfigurableAttributeSpinner =
                    mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                        "config$position"
                    ).findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
                val spinnerOptions = ArrayList<String>()
                val spinnerTags = ArrayList<String>()
                mContentViewBinding.data!!.configurableData.chooseText?.let { spinnerOptions.add(it) }
                spinnerTags.add("")

                for (attributeOptionPosition in 0 until (mContentViewBinding.data!!.configurableData.attributes?.get(
                    position
                )?.options?.size ?: 0)) {

                    if (canAddCurrentAttributeOption(
                            position,
                            attributeOptionPosition,
                            mContentViewBinding.data!!.configurableData.attributes?.get(position)?.options?.get(
                                attributeOptionPosition
                            )?.id ?: ""
                        )
                    ) {
                        mContentViewBinding.data!!.configurableData.attributes?.get(position)?.options?.get(
                            attributeOptionPosition
                        )?.label?.let { spinnerOptions.add(it) }
                        spinnerTags.add(attributeOptionPosition.toString())
                    }
                }
                currentConfigurableAttributeSpinner.adapter =
                    SpinnerWithTagAdapter(spinnerOptions, spinnerTags)
                currentConfigurableAttributeSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            selectedItemView: View?,
                            selectedItemPosition: Int,
                            id: Long
                        ) {
                            try {
                                parent.let {
                                    val currentAttributePosition = parent.tag as Int
                                    if (currentAttributePosition + 1 < (mContentViewBinding.data!!.configurableData.attributes?.size
                                            ?: 0)
                                    ) {
                                        initializeConfigurableAttributeOption(
                                            currentAttributePosition + 1
                                        )
                                    }
                                    updatePrice()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>) {
                        }
                    }
            }
            initializeAllFollowingConfigurableOptionLayouts(position + 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun canAddCurrentAttributeOption(
        currentAttributePositionToUpdate: Int,
        attributeOptionPosition: Int,
        attributeId: String
    ): Boolean {
        try {
            /*add all attribute option value for the position 0*/
            if (currentAttributePositionToUpdate == 0) {
                if (attributeId.isNullOrEmpty()) {
                    return mContentViewBinding.data!!.configurableData.attributes?.get(
                        currentAttributePositionToUpdate
                    )?.options?.get(attributeOptionPosition)?.products?.size != 0

                } else {
                    if (mContentViewBinding.data!!.configurableData.attributes?.get(
                            currentAttributePositionToUpdate
                        )?.options?.get(attributeOptionPosition)?.id == attributeId
                    )
                        return mContentViewBinding.data!!.configurableData.attributes?.get(
                            currentAttributePositionToUpdate
                        )?.options?.get(attributeOptionPosition)?.products?.size != 0
                    else {
                        mContentViewBinding.data!!.configurableData.attributes?.get(
                            currentAttributePositionToUpdate
                        )?.options?.forEach {
                            if (it.id == attributeId && it.products?.size != 0) {
                                return true
                            }
                        }
                        return false
                    }
                }
            }
            val currentProductList = if (attributeId.isNullOrEmpty())
                mContentViewBinding.data!!.configurableData.attributes?.get(
                    currentAttributePositionToUpdate
                )?.options?.get(attributeOptionPosition)?.products
            else {
                var productList: ArrayList<String> = ArrayList()
                mContentViewBinding.data!!.configurableData.attributes?.get(
                    currentAttributePositionToUpdate
                )?.options?.forEach {
                    if (it.id == attributeId)
                        productList = it.products ?: ArrayList()
                }
                productList
            }
            for (noOfProductAssociatedWithCurrentAttributeOption in currentProductList?.indices!!) {

                var noOfMatches = 0

                for (noOfAttributeBeforeTheCurrentAttribute in 0 until currentAttributePositionToUpdate) {
                    if (mContentViewBinding.data!!.configurableData.attributes?.get(
                            noOfAttributeBeforeTheCurrentAttribute
                        )?.swatchType == "visual" || mContentViewBinding.data!!.configurableData.attributes?.get(
                            noOfAttributeBeforeTheCurrentAttribute
                        )?.swatchType == "text"
                    ) {
                        val eachSwatchRecyclerView =
                            mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                "config$noOfAttributeBeforeTheCurrentAttribute"
                            ).findViewById<RecyclerView>(R.id.configurable_item_rv)
                        val eachRecyclerViewData =
                            (eachSwatchRecyclerView.adapter as ProductAttributesSwatchRvAdapter).getSwatchValuesData()
                        var actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute =
                            ""
                        for (noOfData in eachRecyclerViewData.indices) {
                            if (eachRecyclerViewData[noOfData].isSelected) {
                                actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute =
                                    noOfData.toString()
                            }
                        }
                        if (actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute.isEmpty()) {
                            return mContentViewBinding.data!!.configurableData.attributes?.get(
                                currentAttributePositionToUpdate
                            )?.swatchType == "visual" || this.mContentViewBinding.data!!.configurableData.attributes?.get(
                                currentAttributePositionToUpdate
                            )?.swatchType == "text"
                        }
                        val eachProductArrayOfAttributeBeforeTheCurrentAttribute =
                            mContentViewBinding.data!!.configurableData.attributes?.get(
                                noOfAttributeBeforeTheCurrentAttribute
                            )?.options?.get(
                                Integer.parseInt(
                                    actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute
                                )
                            )?.products
                        for (i in eachProductArrayOfAttributeBeforeTheCurrentAttribute?.indices!!) {
                            if (eachProductArrayOfAttributeBeforeTheCurrentAttribute.get(i) == currentProductList.get(
                                    noOfProductAssociatedWithCurrentAttributeOption
                                )
                            ) {
                                noOfMatches++
                            }
                        }
                    } else {
                        val eachConfigurableProductAttributeSpinnerBeforeCurrentAttribute =
                            mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                "config$noOfAttributeBeforeTheCurrentAttribute"
                            ).findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
                        val actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute =
                            eachConfigurableProductAttributeSpinnerBeforeCurrentAttribute.selectedItem as String
                        if (actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute.isEmpty()) {
                            return mContentViewBinding.data!!.configurableData.attributes?.get(
                                currentAttributePositionToUpdate
                            )?.swatchType == "visual" || mContentViewBinding.data!!.configurableData.attributes?.get(
                                currentAttributePositionToUpdate
                            )?.swatchType == "text"
                        }
                        val eachProductArrayOfAttributeBeforeTheCurrentAttribute =
                            mContentViewBinding.data!!.configurableData.attributes?.get(
                                noOfAttributeBeforeTheCurrentAttribute
                            )?.options?.get(
                                Integer.parseInt(
                                    actualPositionOfEachConfigurableProductAttributeSpinnerBeforeCurrentAttribute
                                )
                            )?.products
                        for (i in eachProductArrayOfAttributeBeforeTheCurrentAttribute?.indices!!) {
                            if (eachProductArrayOfAttributeBeforeTheCurrentAttribute.get(i) == currentProductList.get(
                                    index = noOfProductAssociatedWithCurrentAttributeOption
                                )
                            ) {
                                noOfMatches++
                            }
                        }
                    }
                }
                if (noOfMatches == currentAttributePositionToUpdate) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun initializeAllFollowingConfigurableOptionLayouts(position: Int) {
        try {
            for (attributeOptionPosition in position until (mContentViewBinding.data!!.configurableData.attributes?.size
                ?: 0)) {
                if (mContentViewBinding.data!!.configurableData.attributes?.get(position)?.swatchType == "visual" || mContentViewBinding.data!!.configurableData.attributes?.get(
                        position
                    )?.swatchType == "text"
                ) {

                } else {
                    val eachConfigurableAttributeSpinner =
                        mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                            attributeOptionPosition
                        ).findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
                    val spinnerOpions = ArrayList<String>()
                    mContentViewBinding.data!!.configurableData.chooseText?.let {
                        spinnerOpions.add(
                            it
                        )
                    }
                    val spinnerArrayAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        spinnerOpions
                    )
                    eachConfigurableAttributeSpinner.adapter = spinnerArrayAdapter
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updatePrice() = try {
        var noOfSelectedOptions = 0
        var productId = 0
        var price = mContentViewBinding.data!!.price
        val finalPrice = mContentViewBinding.data!!.finalPrice
        var updatedFinalPrice = finalPrice

        //            For Configurable
        if (mContentViewBinding.data!!.configurableData.attributes?.size ?: 0 > 0) {
            val configOptionsId = ArrayList<String>()
            val selectedConfigOptionsId = ArrayList<String>()

            for (noOfConfigData in 0 until (mContentViewBinding.data!!.configurableData.attributes?.size
                ?: 0)) {
                mContentViewBinding.data!!.configurableData.attributes?.get(noOfConfigData)?.id?.let {
                    configOptionsId.add(
                        it
                    )
                }
                if (mContentViewBinding.data!!.configurableData.attributes?.get(noOfConfigData)?.swatchType == "visual" || mContentViewBinding.data!!.configurableData.attributes?.get(
                        noOfConfigData
                    )?.swatchType == "text"
                ) {
                    val eachSwatchRecyclerView =
                        mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                            "config$noOfConfigData"
                        ).findViewById<RecyclerView>(R.id.configurable_item_rv)
                    val recyclerViewOptions =
                        (eachSwatchRecyclerView.adapter as ProductAttributesSwatchRvAdapter).getSwatchValuesData()

                    var isOptionSelected = false
                    for (noOfAttribute in recyclerViewOptions.indices) {
                        if (recyclerViewOptions[noOfAttribute].isSelected) {
                            isOptionSelected = true
                            noOfSelectedOptions++
                            selectedConfigOptionsId.add(
                                noOfConfigData,
                                recyclerViewOptions[noOfAttribute].id
                            )
                        }
                    }
                    if (!isOptionSelected) {
                        selectedConfigOptionsId.add(noOfConfigData, recyclerViewOptions[0].id)
                    }
                } else {
                    val eachConfigurableAttributeSpinner =
                        mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                            "config$noOfConfigData"
                        ).findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
                    val selectedItemOfEachConfigurableProductAttributeSpinner =
                        eachConfigurableAttributeSpinner.selectedItemPosition
                    val actualPositionOfEachConfigurableProductAttributeSpinner =
                        eachConfigurableAttributeSpinner.getItemAtPosition(
                            selectedItemOfEachConfigurableProductAttributeSpinner
                        ) as String

                    if (actualPositionOfEachConfigurableProductAttributeSpinner.isEmpty()) {
                        mContentViewBinding.data!!.configurableData.attributes?.get(noOfConfigData)?.options?.get(
                            0
                        )?.id?.let { selectedConfigOptionsId.add(noOfConfigData, it) }
                    } else {
                        noOfSelectedOptions++
                        mContentViewBinding.data!!.configurableData.attributes?.get(noOfConfigData)?.options?.get(
                            Integer.parseInt(actualPositionOfEachConfigurableProductAttributeSpinner)
                        )?.id?.let { selectedConfigOptionsId.add(noOfConfigData, it) }
                    }
                }
            }

            val indexArray = JSONArray(mContentViewBinding.data!!.configurableData.index)
            var priceFound = false
            for (noOfCombinationData in 0 until indexArray.length()) {
                for (noOfIndexItems in configOptionsId.indices) {
                    if (indexArray.getJSONObject(noOfCombinationData)
                            .getString(configOptionsId[noOfIndexItems]) == selectedConfigOptionsId[noOfIndexItems]
                    ) {
                        priceFound = true
                    } else {
                        priceFound = false
                        break
                    }
                }
                if (priceFound) {
                    updatedFinalPrice =
                        mContentViewBinding.data!!.configurableData.optionPrices?.get(
                            noOfCombinationData
                        )?.finalPrice?.amount
                            ?: 0.0
                    productId = mContentViewBinding.data!!.configurableData.optionPrices?.get(
                        noOfCombinationData
                    )?.product
                        ?: 0
                    break
                }
            }
            if (noOfSelectedOptions > 0)
                updateImages(productId)
        }

        //            For customoptions
        if (mContentViewBinding.data!!.customOptions.size > 0) {
            for (noOfCustomOpt in 0 until mContentViewBinding.data!!.customOptions.size) {
                when (mContentViewBinding.data!!.customOptions[noOfCustomOpt].type) {
                    "field", "area" -> {
                        val customOptionFieldView =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<AppCompatEditText>(
                                noOfCustomOpt
                            )
                        if (!(customOptionFieldView as AppCompatEditText).text.toString()
                                .matches("".toRegex())
                        ) {
                            updatedFinalPrice += (if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].price_type!! == "fixed") {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!!
                            } else {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!! * finalPrice / 100
                            })
                        }
                    }

                    "file" -> if ((mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                            noOfCustomOpt
                        )
                            .findViewById<AppCompatTextView>(R.id.fileSelectedTV) as TextView).text != resources.getString(
                            R.string.no_file_selected
                        )
                    ) {
                        updatedFinalPrice += (if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].price_type == "fixed") {
                            mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price
                        } else {
                            mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!! * finalPrice / 100
                        })!!
                    }

                    "drop_down" -> {
                        val spinnerItem =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<AppCompatSpinner>(
                                noOfCustomOpt
                            )
                        val positionOfSpinnerItem = spinnerItem.selectedItemPosition
                        if (positionOfSpinnerItem != 0) {
                            updatedFinalPrice += if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!!.get(
                                    positionOfSpinnerItem - 1
                                ).priceType == "fixed"
                            ) {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues?.get(
                                    positionOfSpinnerItem - 1
                                )!!.defaultPrice
                            } else {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfSpinnerItem - 1].defaultPrice * finalPrice / 100
                            }
                        }
                    }

                    "radio" -> {
                        val radioGroup =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<RadioGroup>(
                                noOfCustomOpt
                            )
                        if (radioGroup.checkedRadioButtonId != -1) {
                            val selectedRadioBtn =
                                mContentViewBinding.root.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                            val positionOfRadioItem = radioGroup.indexOfChild(selectedRadioBtn)

                            if (positionOfRadioItem != 0 || mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require != 0) {
                                updatedFinalPrice += if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 0) {
                                    if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem].priceType == "fixed") {
                                        mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem - 1].defaultPrice
                                    } else {
                                        mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem - 1].defaultPrice * finalPrice / 100
                                    }
                                } else {
                                    if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem].priceType == "fixed") {
                                        mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem].defaultPrice
                                    } else {
                                        mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfRadioItem].defaultPrice * finalPrice / 100
                                    }
                                }
                            }
                        }
                    }

                    "checkbox", "multiple" -> {
                        val checkBoxContainerLL =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                noOfCustomOpt
                            )

                        for (i in 0 until mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!!.size) {
                            val tempChkBox = checkBoxContainerLL.getChildAt(i) as CheckBox

                            if (tempChkBox.isChecked) {
                                updatedFinalPrice += if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![i].defaultPriceType == "fixed") {
                                    mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![i].defaultPrice
                                } else {
                                    mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![i].defaultPrice * finalPrice / 100
                                }
                            }
                        }
                    }

                    "date" -> {
                        val dateET =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                noOfCustomOpt
                            ).findViewById<AppCompatTextView>(R.id.dateET)
                        if (!dateET.text.toString().matches("".toRegex())) {
                            updatedFinalPrice += (if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].price_type == "fixed") {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price
                            } else {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!! * finalPrice / 100
                            })!!
                        }
                    }

                    "time" -> {
                        val timeET =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                noOfCustomOpt
                            ).findViewById<AppCompatTextView>(R.id.timeET)
                        if (!timeET.text.toString().isEmpty()) {
                            updatedFinalPrice += (if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].price_type!! == "fixed") {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!!
                            } else {
                                mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!! * finalPrice / 100
                            })
                        }
                    }


                    "date_time" -> {
                        val v =
                            mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                "date$noOfCustomOpt"
                            ).findViewById<AppCompatTextView>(R.id.dateET)
                        if (!v.text.toString().matches("".toRegex())) {
                            updatedFinalPrice += mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!!
                        }
                        if (v.text.toString().matches("".toRegex())) {
                            val asscociatedET =
                                mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                    "time$noOfCustomOpt"
                                ).findViewById<AppCompatTextView>(R.id.timeET)
                            if (!asscociatedET.text.toString().matches("".toRegex())) {
                                updatedFinalPrice += (if (mContentViewBinding.data!!.customOptions[noOfCustomOpt].price_type == "fixed") {
                                    mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price
                                } else {
                                    mContentViewBinding.data!!.customOptions[noOfCustomOpt].default_price!! * finalPrice / 100
                                })!!
                            }
                        }
                    }
                }
            }
        }

        //            For Downloadable product links
        if (mContentViewBinding.data!!.links.linkData.size > 0) {
            for (i in 0 until mContentViewBinding.data!!.links.linkData.size) {
                /* i+1 as it contain a extra text box for label*/
                if ((mContentViewBinding.otherProductOptionsContainer.getChildAt(i + 1) as CheckBox).isChecked) {
                    updatedFinalPrice += mContentViewBinding.data!!.links.linkData[i].price
                }
            }
        }

        price += updatedFinalPrice - finalPrice

        val pattern = AppSharedPref.getPattern(this)
        val precision = AppSharedPref.getPrecision(this)
        val precisionFormat = "%." + precision + "f"
        val formattedFinalPrice = String.format(Locale.US, precisionFormat, updatedFinalPrice)
        val formattedPrice = String.format(Locale.US, precisionFormat, price)
        val newFormattedFinalPrice = pattern.replace("%s", formattedFinalPrice)
        val newFormattedPrice = pattern.replace("%s", formattedPrice)
        val specialPriceText = "As low as $newFormattedPrice"
        if (mContentViewBinding.data!!.typeId != "configurable" || noOfSelectedOptions == mContentViewBinding.data!!.configurableData.attributes?.size) {
            mContentViewBinding.productPriceTv.text = newFormattedFinalPrice
            mContentViewBinding.productSpecialPriceTv.setText(newFormattedPrice)

        } else {

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    private fun updateImages(productId: Int) {
        val imageGalleryDataArray = ArrayList<ImageGalleryData>()
        for (noOfImages in 0 until mContentViewBinding.data!!.imageGallery.size) {
            val imageGalleryDataObject = ImageGalleryData()
            imageGalleryDataObject.smallImage =
                mContentViewBinding.data!!.imageGallery[noOfImages].smallImage
            imageGalleryDataObject.largeImage =
                mContentViewBinding.data!!.imageGallery[noOfImages].largeImage
            imageGalleryDataArray.add(imageGalleryDataObject)
        }

        if (productId != 0) {
            try {
                val imagesJSON = JSONObject(mContentViewBinding.data!!.configurableData.images)
                val imageGalleryArray = imagesJSON.getJSONArray(productId.toString())

                var insertedImagePosition = 0

                if (imageGalleryArray != null) {
                    for (noOfImages in 0 until imageGalleryArray.length()) {
                        val imageGalleryDataObject = ImageGalleryData()
                        imageGalleryDataObject.smallImage =
                            imageGalleryArray.getJSONObject(noOfImages).getString("thumb")
                        imageGalleryDataObject.largeImage =
                            imageGalleryArray.getJSONObject(noOfImages).getString("full")
                        imageGalleryDataArray.add(insertedImagePosition++, imageGalleryDataObject)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        mContentViewBinding.productSliderViewPager.adapter =
            ProductPageSliderAdapter(this, mContentViewBinding.data!!.name, imageGalleryDataArray)
        mContentViewBinding.productSliderDotsTabLayout.setupWithViewPager(
            mContentViewBinding.productSliderViewPager,
            true
        )
    }

    private fun setupMoreInfoRv() {
        mContentViewBinding.moreInfoRv.adapter =
            MoreInfoRvAdapter(this, mContentViewBinding.data!!.additionalInformation)
    }

    private fun setupReviewsLayoutAndRv() {
        mContentViewBinding.reviewPieChart.isClickable = false
        mContentViewBinding.reviewPieChart.legend.isEnabled = false
        mContentViewBinding.reviewPieChart.isRotationEnabled = true
        mContentViewBinding.reviewPieChart.holeRadius = 85f
        mContentViewBinding.reviewPieChart.setTransparentCircleAlpha(0)
        mContentViewBinding.reviewPieChart.setDrawEntryLabels(false)
        mContentViewBinding.reviewPieChart.description.isEnabled = false

        mContentViewBinding.reviewPieChart.isFocusable = false

        addPieChartDataSet()
        mContentViewBinding.reviewPieChart.isEnabled = false

        if (mContentViewBinding.reviewsRv.adapter == null) {
            mContentViewBinding.reviewsRv.addItemDecoration(
                DividerItemDecoration(
                    this,
                    LinearLayout.VERTICAL
                )
            )
        }
        mContentViewBinding.reviewsRv.adapter =
            ReviewsRvAdapter(this, mContentViewBinding.data!!.reviewList)
    }

    private fun addPieChartDataSet() {
        val yEntrys = ArrayList<PieEntry>()

        yEntrys.add(PieEntry(mContentViewBinding.data!!.ratingArray.one))
        yEntrys.add(PieEntry(mContentViewBinding.data!!.ratingArray.two))
        yEntrys.add(PieEntry(mContentViewBinding.data!!.ratingArray.three))
        yEntrys.add(PieEntry(mContentViewBinding.data!!.ratingArray.four))
        yEntrys.add(PieEntry(mContentViewBinding.data!!.ratingArray.five))

        //create the data set
        val pieDataSet = PieDataSet(yEntrys, "Ratings")
        pieDataSet.setDrawValues(false)
        pieDataSet.sliceSpace = 0f

        //add colors to data set
        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(this, R.color.single_star_color))
        colors.add(ContextCompat.getColor(this, R.color.two_star_color))
        colors.add(ContextCompat.getColor(this, R.color.three_star_color))
        colors.add(ContextCompat.getColor(this, R.color.four_star_color))
        colors.add(ContextCompat.getColor(this, R.color.five_star_color))
        pieDataSet.colors = colors

        //create pie data object
        val pieData = PieData(pieDataSet)
        mContentViewBinding.reviewPieChart.data = pieData

        mContentViewBinding.reviewPieChart.invalidate()
    }

    private fun setupRelatedProductsRv() {
        if (mContentViewBinding.relatedProductsRv.adapter == null) {
            mContentViewBinding.relatedProductsRv.addItemDecoration(
                HorizontalMarginItemDecoration(
                    resources.getDimension(R.dimen.spacing_tiny).toInt()
                )
            )
            mContentViewBinding.relatedProductsRv.isNestedScrollingEnabled = false
        }
        mContentViewBinding.relatedProductsRv.adapter =
            RelatedProductsRvAdapter(this, mContentViewBinding.data!!.relatedProductList)
    }

    private fun setupUpsellProductsRv() {
        if (mContentViewBinding.upsellProductsRv.adapter == null) {
            mContentViewBinding.upsellProductsRv.addItemDecoration(
                HorizontalMarginItemDecoration(
                    resources.getDimension(R.dimen.spacing_tiny).toInt()
                )
            )
            mContentViewBinding.upsellProductsRv.isNestedScrollingEnabled = false
        }
        mContentViewBinding.upsellProductsRv.adapter =
            ProductCarouselHorizontalRvAdapter(this, mContentViewBinding.data!!.upsellProductList)
    }

    private fun setupFloatingBuyLayoutHiding() {
        val rect = Rect()
        mContentViewBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (!mContentViewBinding.data!!.isAvailable || (mContentViewBinding.staticBuyLayout.getGlobalVisibleRect(
                    rect
                ) && mContentViewBinding.staticBuyLayout.height == rect.height() &&
                        mContentViewBinding.staticBuyLayout.width == rect.width()) || scrollY > mContentViewBinding.staticBuyLayout.y
            ) {
                mContentViewBinding.floatingBuyLayout.visibility = View.GONE
            } else {
                mContentViewBinding.floatingBuyLayout.visibility = View.VISIBLE
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            if (!mContentViewBinding.data!!.isAvailable || (mContentViewBinding.staticBuyLayout.getGlobalVisibleRect(
                    rect
                ) && mContentViewBinding.staticBuyLayout.height == rect.height() &&
                        mContentViewBinding.staticBuyLayout.width == rect.width())
            ) {
                mContentViewBinding.floatingBuyLayout.visibility = View.GONE
            } else {
                mContentViewBinding.floatingBuyLayout.visibility = View.VISIBLE
            }
        }, 500)
    }

    open fun onFailureResponse(productDetailsPageModel: ProductDetailsPageModel) {
        if (productDetailsPageModel.message == "disabled") {
            ToastHelper.showToast(this, getString(R.string.product_disabled))
            mDataBaseHandler.deleteRecentlyViewedProduct(mProductId)
            finish()
        } else {
            AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                productDetailsPageModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                },
                getString(R.string.dismiss),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    if (mContentViewBinding.data == null)
                        finish()
                })
        }
    }

    fun onErrorResponse(error: Throwable) {
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
                    callApi()
                },
                getString(R.string.dismiss),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    if (mContentViewBinding.data == null)
                        finish()
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                RC_PICK_SINGLE_FILE -> {
                    CropImage.activity(data.data)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(Uri.fromFile(File(externalCacheDir, "image${System.currentTimeMillis()}.jpg")))
                        .start(this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    try {
                        val fileUri: Uri? = CropImage.getActivityResult(data)?.uri
                        fileUri?.let {
                            (mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(
                                mSeletedCustomOption
                            ).findViewById(R.id.fileSelectedTV) as TextView).text =
                                it.lastPathSegment
                            mSelectedImageUri[mSeletedCustomOption] = it
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    updatePrice()
                }
                RC_AR_MEASURE -> {
                    mSelectedEditText?.setText(data.getStringExtra(BundleKeysHelper.BUNDLE_KEY_AR_MEASURED))
                }
                RC_WISHLIST -> {
                    callApi()
                }
                RC_LOGIN->{
                    if(mContentViewBinding.handler?.addToCartResponseModel!=null)
                    mContentViewBinding.handler?.checkout(mContentViewBinding.handler?.addToCartResponseModel!!)
                }
            }
        }
    }

    internal inner class GenericTextWatcher : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updatePrice()
        }
    }

    inner class SpinnerWithTagAdapter internal constructor(
        private val mSpinnerOptions: ArrayList<String>,
        private val spinnerOptionsKeys: ArrayList<String>
    ) : BaseAdapter() {

        override fun getCount(): Int {
            return mSpinnerOptions.size
        }

        override fun getItem(position: Int): Any {
            return spinnerOptionsKeys[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val spinnerItem = layoutInflater.inflate(
                android.R.layout.simple_spinner_dropdown_item,
                null
            ) as CheckedTextView
            spinnerItem.setPadding(5, 5, 5, 5)
            spinnerItem.minHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f,
                resources.displayMetrics
            ).toInt()
            spinnerItem.text = mSpinnerOptions[position]
            spinnerItem.tag = position - 1
            return spinnerItem
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allPermissionsGranted = true
        for (eachGrantResult in grantResults) {
            if (eachGrantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
            }
        }
        if (allPermissionsGranted) {
            if (requestCode == ConstantsHelper.RC_AR) {
                mContentViewBinding.handler!!.startArActivity()
            }
        }
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

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT in 21..25 && (resources.configuration.uiMode == applicationContext.resources.configuration.uiMode)) {
            return
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    fun setVisibilityBasedOnAvailability(view: View, availability: String?) {
        view.visibility = if (availability != null && availability == "In Stock") {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}