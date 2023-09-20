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

package com.webkul.mobikul.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.adapters.NavDrawerAccountRvAdapter
import com.webkul.mobikul.adapters.NavDrawerCategoriesRvAdapter
import com.webkul.mobikul.adapters.NavDrawerCmsRvAdapter
import com.webkul.mobikul.adapters.NavDrawerPreferencesRvAdapter
import com.webkul.mobikul.databinding.FragmentNavDrawerStartBinding
import com.webkul.mobikul.handlers.NavDrawerStartFragmentHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.ImageUploadResponseData
import com.webkul.mobikul.models.user.AccountRvModel
import com.webkul.mobikul.models.user.PreferencesRvModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

open class NavDrawerStartFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentNavDrawerStartBinding
    var mIsUpdatingProfilePic: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_drawer_start, container, false)
        startInitialization()
        return mContentViewBinding.root
    }

    private fun startInitialization() {
        context?.let { context->

        mContentViewBinding.data = (context as HomeActivity).mHomePageDataModel

        setupCategoriesRv()
        setupAccountRv()
        setupPreferencesRv()
        setupCMSPagesRv()

        mContentViewBinding.handler = NavDrawerStartFragmentHandler(this)
    }
    }

    private fun updateProfilePic() {
        context?.let { context ->

            try {
                if (context != null && !AppSharedPref.getCustomerImageUrl(context).isBlank()
                ) {
                    Glide.with(this).load(AppSharedPref.getCustomerImageUrl(context))
                        .apply(RequestOptions.circleCropTransform())
                        .into(mContentViewBinding.profileImage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateBannerImage() {
        context?.let { context ->

            try {
                if (context != null && !AppSharedPref.getCustomerBannerUrl(context).isBlank()) {
                    ImageHelper.load(
                        mContentViewBinding.banner,
                        AppSharedPref.getCustomerBannerUrl(context),
                        AppSharedPref.getCustomerBannerDominantColor(context)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupCategoriesRv() {
        context?.let { context ->

            if ((context as HomeActivity).mHomePageDataModel.categories?.size ?: 0 > 0) {
                mContentViewBinding.navDrawerCategoryRv.adapter =
                    (context as HomeActivity).mHomePageDataModel.categories?.let {
                        NavDrawerCategoriesRvAdapter(this, it)
                    }
                mContentViewBinding.navDrawerCategoryRv.isNestedScrollingEnabled = false
            }
        }
    }

    open fun setupAccountRv() {
        context?.let { context ->

        if (AppSharedPref.isLoggedIn(context)) {
            val accountRvData: ArrayList<AccountRvModel> = ArrayList()
            accountRvData.add(AccountRvModel(AccountRvModel.DASHBOARD, getString(R.string.dashboard), R.drawable.ic_user_dashboard))
            if (ApplicationConstants.ENABLE_WISHLIST && AppSharedPref.isWishlistEnabled(context))
                accountRvData.add(AccountRvModel(AccountRvModel.WISH_LIST, getString(R.string.wishlist), R.drawable.ic_user_wish_list))
            accountRvData.add(AccountRvModel(AccountRvModel.ORDERS, getString(R.string.orders), R.drawable.ic_user_orders))
//            accountRvData.add(AccountRvModel(AccountRvModel.DOWNLOADABLE_PRODUCTS, getString(R.string.downloadable_products), R.drawable.ic_user_downloadable_products))
            accountRvData.add(AccountRvModel(AccountRvModel.PRODUCT_REVIEWS, getString(R.string.product_reviews), R.drawable.ic_user_product_reviews))
            accountRvData.add(AccountRvModel(AccountRvModel.ADDRESS_BOOK, getString(R.string.address_book), R.drawable.ic_user_address_book))
            accountRvData.add(AccountRvModel(AccountRvModel.ACCOUNT_INFORMATION, getString(R.string.account_information), R.drawable.ic_user_account_info))
            accountRvData.add(AccountRvModel(AccountRvModel.ZOPIM_CHAT, getString(R.string.zopim_chat), R.drawable.ic_user_chat))
//            accountRvData.add(AccountRvModel(AccountRvModel.QR_CODE_LOGIN, getString(R.string.web_login), R.drawable.ic_vector_qr_code))
            mContentViewBinding.navDrawerAccountRv.adapter = NavDrawerAccountRvAdapter(this, accountRvData)
            mContentViewBinding.navDrawerAccountRv.isNestedScrollingEnabled = false
        }
        }
    }

    private fun setupPreferencesRv() {
        context?.let { context ->

            val preferencesRvModel: ArrayList<PreferencesRvModel> = ArrayList()
            if (ApplicationConstants.ENABLE_WEBSITE && !(context as HomeActivity).mHomePageDataModel.websiteData.isNullOrEmpty()
                && (context as HomeActivity).mHomePageDataModel.websiteData?.size ?: 0 > 1)
                preferencesRvModel.add(PreferencesRvModel(PreferencesRvModel.WEBSITE, String.format(getString(R.string.website_x), AppSharedPref.getWebsiteLabel(context))))
            if (ApplicationConstants.ENABLE_STORES && !(context as HomeActivity).mHomePageDataModel.storeData.isNullOrEmpty()
                && !((context as HomeActivity).mHomePageDataModel.storeData?.size == 1 && (context as HomeActivity).mHomePageDataModel.storeData?.get(0)?.stores?.size == 1))
                preferencesRvModel.add(PreferencesRvModel(PreferencesRvModel.LANGUAGE, String.format(getString(R.string.language_x), AppSharedPref.getStoreLabel(context))))
            if (ApplicationConstants.ENABLE_CURRENCIES && !(context as HomeActivity).mHomePageDataModel.allowedCurrencies.isNullOrEmpty() && (context as HomeActivity).mHomePageDataModel.allowedCurrencies?.size ?: 0 > 1)
                preferencesRvModel.add(PreferencesRvModel(PreferencesRvModel.CURRENCY, String.format(getString(R.string.currency_x), AppSharedPref.getCurrencyLabel(context))))
            preferencesRvModel.add(PreferencesRvModel(PreferencesRvModel.SETTINGS, getString(R.string.settings)))
            mContentViewBinding.navDrawerPreferencesRv.adapter = NavDrawerPreferencesRvAdapter(this, preferencesRvModel)
            mContentViewBinding.navDrawerPreferencesRv.isNestedScrollingEnabled = false
        }
    }

    open fun setupCMSPagesRv() {
        if (ApplicationConstants.ENABLE_CMS_PAGES && (context as HomeActivity).mHomePageDataModel.cmsData?.size?:0 > 0) {
            mContentViewBinding.navDrawerOthersRv.adapter = (context as HomeActivity).mHomePageDataModel.cmsData?.let { NavDrawerCmsRvAdapter(this, it) }
            mContentViewBinding.navDrawerOthersRv.isNestedScrollingEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfilePic()
        updateBannerImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        context?.let { context ->


        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
                CropImage.activity(CropImage.getPickImageResultUri(context, data))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(Uri.fromFile(File(context.externalCacheDir, "image${System.currentTimeMillis()}.jpg")))
                        .setAspectRatio(if (mIsUpdatingProfilePic) 1 else 3, if (mIsUpdatingProfilePic) 1 else 2)
                        .start(context, this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.getActivityResult(data)?.let { uploadPic(it.uri) }
            }
        }
        }
    }

    private fun uploadPic(data: Uri) {
        context?.let { context ->

            try {

                val bitmap = BitmapFactory.decodeFile(data.path)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)

                val fileBody = bos.toByteArray().toRequestBody("image/*".toMediaTypeOrNull())
                val multipartFileBody =
                    MultipartBody.Part.createFormData("files", File(data.path).name, fileBody)

                if (mIsUpdatingProfilePic) {
                    uploadProfilePic(multipartFileBody)
                } else {
                    uploadBanner(multipartFileBody)
                }
            } catch (e: Exception) {
                ToastHelper.showToast(context, getString(R.string.something_went_wrong))
                e.printStackTrace()
            }
        }
    }

    private fun uploadProfilePic(multipartFileBody: MultipartBody.Part) {
        context?.let { context ->

            mContentViewBinding.loadingProfile = true
            ApiConnection.uploadProfileImage(context, multipartFileBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :
                    ApiCustomCallback<ImageUploadResponseData>(context, true) {
                    override fun onNext(imageUploadResponseData: ImageUploadResponseData) {
                        super.onNext(imageUploadResponseData)
                        mContentViewBinding.loadingProfile = false
                        if (imageUploadResponseData.success) {
                            AppSharedPref.setCustomerImageUrl(context, imageUploadResponseData.url)
                            updateProfilePic()
                        } else {
                            ToastHelper.showToast(context, imageUploadResponseData.message)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loadingProfile = false
                        ToastHelper.showToast(context, getString(R.string.something_went_wrong))
                    }
                })
        }
    }

    private fun uploadBanner(multipartFileBody: MultipartBody.Part) {
        context?.let { context ->

            mContentViewBinding.loadingBanner = true
            ApiConnection.uploadBannerImage(context, multipartFileBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :
                    ApiCustomCallback<ImageUploadResponseData>(context, true) {
                    override fun onNext(imageUploadResponseData: ImageUploadResponseData) {
                        super.onNext(imageUploadResponseData)
                        mContentViewBinding.loadingBanner = false
                        if (imageUploadResponseData.success) {
                            AppSharedPref.setCustomerBannerUrl(context, imageUploadResponseData.url)
                            updateBannerImage()
                        } else {
                            ToastHelper.showToast(context, imageUploadResponseData.message)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loadingBanner = false
                        ToastHelper.showToast(context, getString(R.string.something_went_wrong))
                    }
                })
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
            if (requestCode == ConstantsHelper.RC_PICK_IMAGE) {
                mContentViewBinding.handler!!.selectImage()
            }
        }
    }
}