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
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.LayerDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.webkul.mlkit.activities.CameraSearchActivity
import com.webkul.mobikul.R
import com.webkul.mobikul.broadcast_receivers.NetworkStateReceiver
import com.webkul.mobikul.customviews.MaterialSearchView
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.fragments.NotificationBottomSheetFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.CAMERA_SEARCH_HELPER
import com.webkul.mobikul.helpers.ConstantsHelper.RC_CAMERA_SEARCH
import com.webkul.mobikul.helpers.ConstantsHelper.RC_VOICE
import com.webkul.mobikul.helpers.Utils.Companion.setActionBarThemeColor
import com.webkul.mobikul.helpers.Utils.Companion.setMenuItemIconColor
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.network.ApiClient
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.disposables.CompositeDisposable


open class BaseActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    val activityLauncher: BetterActivityResult<Intent, ActivityResult> by lazy { BetterActivityResult.registerActivityForResult(this) }

    companion object {

        val mObjectMapper: ObjectMapper by lazy {
            ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
        lateinit var mDataBaseHandler: DatabaseHelper
        fun isDbInitialized() = ::mDataBaseHandler.isInitialized

    }

    var mHashIdentifier = ""
    lateinit var mItemCart: MenuItem
    var mCustomDialog: AlertDialog? = null
    var mToast: Toast? = null
    var mCompositeDisposable = CompositeDisposable()
    lateinit var mMaterialSearchView: MaterialSearchView
    var mNetworkStateReceiver: NetworkStateReceiver = NetworkStateReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarUpView()
        mDataBaseHandler = DatabaseHelper(this)

        mNetworkStateReceiver = NetworkStateReceiver()
        mNetworkStateReceiver.addListener(this)
        registerReceiver(mNetworkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    open fun setToolbarUpView() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMaterialSearchView = MaterialSearchView(this)
    }

    open fun initSupportActionBar(){
        setActionBarThemeColor(supportActionBar,this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        mItemCart = menu.findItem(R.id.menu_item_cart)
        setMenuItemIconColor(menu,this)
        updateCartBadge()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.menu_item_search -> {
                openMaterialSearchView()
            }
            R.id.menu_item_notification -> {
                NotificationBottomSheetFragment().show(supportFragmentManager, NotificationBottomSheetFragment::class.java.simpleName)
            }
            R.id.menu_item_cart -> {
                CartBottomSheetFragment().show(supportFragmentManager, CartBottomSheetFragment::class.java.simpleName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateCartCount(cartCount: Int) {
        AppSharedPref.setCartCount(this, cartCount)
        updateCartBadge()
    }

    fun updateCartBadge() {
        if (this::mItemCart.isInitialized) {
            Utils.setBadgeCount(this, (mItemCart.icon as LayerDrawable), AppSharedPref.getCartCount(this))
        }
    }


    fun openMaterialSearchView() {
        val contentLayout = findViewById<FrameLayout>(android.R.id.content)
        if (this::mMaterialSearchView.isInitialized && mMaterialSearchView.parent != null)
            (mMaterialSearchView.parent as ViewGroup).removeView(mMaterialSearchView)

        if (this::mMaterialSearchView.isInitialized) {
            contentLayout.addView(mMaterialSearchView)
            mMaterialSearchView.openSearch()
        }
        supportActionBar?.hide()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
                .also {
                    MyContextWrapper.wrap(newBase, AppSharedPref.getStoreCode(this))
                }
    }

    override fun onResume() {
        super.onResume()
        Utils.hideKeyboard(this)
        LocaleUtils.updateConfig(this)
        updateCartBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        ApiClient.getDispatcher().cancelAll()
        Utils.hideKeyboard(this)
        Utils.enableUserInteraction(this)
        mNetworkStateReceiver.removeListener(this)
        unregisterReceiver(mNetworkStateReceiver)
    }

    override fun onBackPressed() {
        if (this::mMaterialSearchView.isInitialized && mMaterialSearchView.isOpen()) {
            mMaterialSearchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_VOICE) {
                val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                mMaterialSearchView.setQuery(result?.get(0))
            } else if (requestCode == RC_CAMERA_SEARCH) {
                mMaterialSearchView.setQuery(data!!.getStringExtra(CameraSearchActivity.CAMERA_SEARCH_HELPER))
            }
        }
    }

    open fun onFailureResponse(response: Any) {
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                AlertDialogHelper.showNewCustomDialog(
                        this,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            Utils.logoutAndGoToHome(this)
                        }, "", null)
            }
        }
    }

    override fun networkAvailable() {
        if (mDataBaseHandler.getCartTableRowCount() > 0) {

            AlertDialogHelper.showNewCustomDialog(
                    this,
                    getString(R.string.offline_cart),
                    getString(R.string.sync_cart_with_server_body_message),
                    false,
                    getString(R.string.sync_now),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    ToastHelper.showToast(this, getString(R.string.syncing_in_bg))
                    val syncCartDbWithServer = SyncCartDbWithServer(this@BaseActivity)
                    syncCartDbWithServer.execute()
                },
                    getString(R.string.clear_all),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mDataBaseHandler.clearCartTableData()
                })
        }

        if (mDataBaseHandler.getWishListTableRowCount() > 0) {

            AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.offline_wish_list_to_cart),
                getString(R.string.sync_wish_list_to_cart_with_server_body_message),
                false,
                getString(R.string.sync_now),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    ToastHelper.showToast(this, getString(R.string.syncing_in_bg))
                    val syncCartDbWithServer = SyncCartDbWithServer(this@BaseActivity)
                    syncCartDbWithServer.executeWishListToCart()
                },
                getString(R.string.clear_all),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mDataBaseHandler.clearCartTableData()
                })
        }

        if (mDataBaseHandler.getWishListAllTableRowCount() > 0) {

            AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.offline_wish_list_all_to_cart),
                getString(R.string.sync_wish_list_all_to_cart_with_server_body_message),
                false,
                getString(R.string.sync_now),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    ToastHelper.showToast(this, getString(R.string.syncing_in_bg))
                    val syncCartDbWithServer = SyncCartDbWithServer(this@BaseActivity)
                    syncCartDbWithServer.executeWishListAllToCart()
                },
                getString(R.string.clear_all),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mDataBaseHandler.clearWishListAllCartTableData()
                })
        }


    }

    override fun networkUnavailable() {

    }

    fun openScanner(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_MAIN);
            intent.setClassName(baseContext, "com.google.firebase.ml.mobikul.kotlin.LiveBarcodeScanningActivity");
            startActivityForResult(intent, requestCode)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, ConstantsHelper.RC_OPEN_SCANNER)
            }
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
            if (requestCode == ConstantsHelper.RC_OPEN_SCANNER) {
                openScanner(ConstantsHelper.RC_QR_LOGIN)
            }
        }
    }
}