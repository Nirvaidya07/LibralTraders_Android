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

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityDashboardBinding
import com.webkul.mobikul.fragments.DashboardAddressesFragment
import com.webkul.mobikul.fragments.DashboardRecentOrdersFragment
import com.webkul.mobikul.fragments.DashboardReviewsFragment
import com.webkul.mobikul.fragments.SettingsBottomSheetFragment
import com.webkul.mobikul.handlers.DashboardActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ConstantsHelper.RC_LOGIN
import com.webkul.mobikul.models.ImageUploadResponseData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class DashboardActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityDashboardBinding
    var mIsUpdatingProfilePic: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        startInitialization()
    }

    private fun startInitialization() {
        initSupportActionBar()
        setupTitleWithScroll()

        updateProfilePic()
        updateBannerImage()
        updateNameAndEmail()

        setupViewPager()

        mContentViewBinding.handler = DashboardActivityHandler(this)
    }

    private fun setupViewPager() {
        mContentViewBinding.tabs.setupWithViewPager(mContentViewBinding.viewPager)
        val adapter = DashboardVpAdapter(supportFragmentManager)
        adapter.addFragment(DashboardRecentOrdersFragment(), getString(R.string.recent_orders))
        adapter.addFragment(DashboardAddressesFragment(), getString(R.string.address))
        adapter.addFragment(DashboardReviewsFragment(), getString(R.string.reviews))
        mContentViewBinding.viewPager.adapter = adapter
        mContentViewBinding.viewPager.offscreenPageLimit = 2
    }

    override fun initSupportActionBar() {
        setSupportActionBar(mContentViewBinding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        Utils.setMenuItemIconColor(menu, this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                SettingsBottomSheetFragment().show(supportFragmentManager, SettingsBottomSheetFragment::class.java.simpleName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateProfilePic() {
        try {
            if (AppSharedPref.getCustomerImageUrl(this).isNotBlank()) {
                Glide.with(this).load(AppSharedPref.getCustomerImageUrl(this)).apply(RequestOptions.circleCropTransform()).into(mContentViewBinding.profileImage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateBannerImage() {
        try {
            if (AppSharedPref.getCustomerBannerUrl(this).isNotBlank()) {
                ImageHelper.load(mContentViewBinding.bannerImage, AppSharedPref.getCustomerBannerUrl(this), AppSharedPref.getCustomerBannerDominantColor(this))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateNameAndEmail() {
        mContentViewBinding.customerNameTv.text = AppSharedPref.getCustomerName(this)
        mContentViewBinding.customerEmailTv.text = AppSharedPref.getCustomerEmail(this)
    }

    private fun setupTitleWithScroll() {
        mContentViewBinding.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    mContentViewBinding.collapsingToolbar.title = resources.getString(R.string.dashboard)
                    isShow = true
                } else if (isShow) {
                    mContentViewBinding.collapsingToolbar.title = " " //carefull there should be a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })
    }

    private inner class DashboardVpAdapter internal constructor(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                    CropImage.activity(CropImage.getPickImageResultUri(this, data))
                            .setOutputUri(Uri.fromFile(File(externalCacheDir, "image${System.currentTimeMillis()}.jpg")))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(if (mIsUpdatingProfilePic) 1 else 3, if (mIsUpdatingProfilePic) 1 else 2)
                            .start(this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    CropImage.getActivityResult(data)?.let { uploadPic(it.uri) }
                }
                RC_LOGIN -> {
                    mContentViewBinding.executePendingBindings()
                }
            }
        }
    }

    private fun uploadPic(data: Uri) {
        try {
            val bitmap = BitmapFactory.decodeFile(data.path)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)

            val fileBody =  bos.toByteArray().toRequestBody("image/*".toMediaTypeOrNull())
            val multipartFileBody = MultipartBody.Part.createFormData("files", File(data.path).name, fileBody)

            if (mIsUpdatingProfilePic) {
                uploadProfilePic(multipartFileBody)
            } else {
                uploadBanner(multipartFileBody)
            }
        } catch (e: Exception) {
            ToastHelper.showToast(this, getString(R.string.something_went_wrong))
            e.printStackTrace()
        }
    }

    private fun uploadProfilePic(multipartFileBody: MultipartBody.Part) {
        mContentViewBinding.loadingProfile = true
        ApiConnection.uploadProfileImage(this, multipartFileBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ImageUploadResponseData>(this, true) {
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

    private fun uploadBanner(multipartFileBody: MultipartBody.Part) {
        mContentViewBinding.loadingBanner = true
        ApiConnection.uploadBannerImage(this, multipartFileBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ImageUploadResponseData>(this, true) {
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

    override fun onResume() {
        super.onResume()
        updateNameAndEmail()
    }
}