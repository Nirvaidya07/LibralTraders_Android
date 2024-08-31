package com.webkul.mobikul.handlers

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.razorpay.Checkout
import com.theartofdev.edmodo.cropper.CropImage
import com.libraltraders.android.R
import com.webkul.mobikul.activities.*
import com.webkul.mobikul.fragments.NavDrawerStartFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.AddressDetailsData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

class NavDrawerStartFragmentHandler(private val mFragmentContext: NavDrawerStartFragment) {

    fun onClickBannerImage() {
        mFragmentContext. context?.let { context ->

            if (mFragmentContext.isAdded && mFragmentContext.isVisible) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (AppSharedPref.isLoggedIn(context)) {
                        mFragmentContext.activity?.startActivityForResult(
                            Intent(
                                context,
                                DashboardActivity::class.java
                            ), ConstantsHelper.RC_LOGIN
                        )

                    } else {
                        mFragmentContext.activity?.startActivityForResult(
                            ((context as BaseActivity).application as MobikulApplication).getLoginAndSignUpActivity(
                                mFragmentContext.requireContext()
                            ), ConstantsHelper.RC_LOGIN
                        )
                    }
                }, 300)
                (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
            }

        }
    }

    fun onClickProfileImage() {
        mFragmentContext.context?.let { context ->

            if (mFragmentContext.isAdded) {
                if (AppSharedPref.isLoggedIn(context)) {
                    mFragmentContext.mIsUpdatingProfilePic = true
                    selectImage()
                }
            }
        }
    }

    fun selectImage() {
        mFragmentContext.context?.let { context ->

            if (mFragmentContext.isAdded) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    CropImage.startPickImageActivity(
                        context,
                        mFragmentContext
                    )
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val permissions = arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        )
                        mFragmentContext.requestPermissions(
                            permissions,
                            ConstantsHelper.RC_PICK_IMAGE
                        )
                    }
                }
            }
        }
    }

    fun onClickCompareProducts() {
        mFragmentContext.context?.let { context ->


            if (mFragmentContext.isAdded) {
                Handler(Looper.getMainLooper()).postDelayed({
                    context.startActivity(
                        Intent(
                            context,
                            CompareProductsActivity::class.java
                        )
                    )
                }, 300)
                (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
            }
        }
    }

    fun onClickContactUs() {
        mFragmentContext.context?.let { context ->

            if (mFragmentContext.isAdded) {
                Handler(Looper.getMainLooper()).postDelayed({
                    context?.startActivity(
                        Intent(
                            context,
                            ContactUsActivity::class.java
                        )
                    )
                }, 300)
                (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
            }
        }
    }

    fun onClickOrdersAndReturns() {
        mFragmentContext.context?.let { context ->

        if (mFragmentContext.isAdded) {
            Handler(Looper.getMainLooper()).postDelayed({
                context.startActivity(
                    Intent(
                        context,
                        OrdersAndReturnsActivity::class.java
                    )
                )
            }, 300)
            (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
        }
        }
    }

    fun onClickViewCategory() {
        mFragmentContext.context?.let { context ->

            if (mFragmentContext.isAdded) {
                Handler(Looper.getMainLooper()).postDelayed({
                    context?.startActivity(
                        Intent(
                            context,
                            CategoryActivity::class.java
                        ).putParcelableArrayListExtra(
                            BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA,
                            (context as HomeActivity).mContentViewBinding.data!!.categories
                        )
                    )
                }, 250)
                (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
            }
        }
    }

    fun onClickLogout() {
        mFragmentContext.context?.let { context ->

            if (mFragmentContext.isAdded) {
                (context as HomeActivity).mContentViewBinding.drawerLayout.closeDrawers()
                Handler(Looper.getMainLooper()).postDelayed({
                 //   val context = mFragmentContext.requireActivity()

                    AlertDialogHelper.showNewCustomDialog(
                        context as BaseActivity,
                        mFragmentContext.getString(R.string.log_out),
                        mFragmentContext.getString(R.string.logout_warning),
                        false,
                        mFragmentContext.getString(R.string.log_out),
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            deleteTokenFromCloud(context as BaseActivity)

                            ApiConnection.logout(context)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(object : ApiCustomCallback<BaseModel>(context, true) {
                                    override fun onNext(logoutResponse: BaseModel) {
                                    }

                                    override fun onError(e: Throwable) {
                                    }
                                })

                            if(BaseActivity.mDataBaseHandler!=null)
                                BaseActivity.mDataBaseHandler.clearRecentlyViewedProductsTableData()
                            val customerSharedPrefEditor = AppSharedPref.getSharedPreferenceEditor(
                                context,
                                AppSharedPref.CUSTOMER_PREF
                            )
                            customerSharedPrefEditor.clear()
                            customerSharedPrefEditor.apply()
                            Checkout.clearUserData(mFragmentContext.context)
                            AppSharedPref.setCustomerCachedNewAddress(context, AddressDetailsData())
                            ToastHelper.showToast(
                                context as BaseActivity,
                                context.getString(R.string.logout_message)
                            )
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        },
                        mFragmentContext.getString(R.string.cancel),
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                        })
                }, 300)
            }
        }
    }

    private fun deleteTokenFromCloud(context: FragmentActivity?) {
        context?.let {
           if (mFragmentContext.isAdded) {
                ApiConnection.deleteTokenFromCloud(it, "seller")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(it, true) {
                        override fun onNext(baseModel: BaseModel) {
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
                ApiConnection.deleteTokenFromCloud(it, "customer")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(it, true) {
                        override fun onNext(baseModel: BaseModel) {
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
            }
        }
    }
}