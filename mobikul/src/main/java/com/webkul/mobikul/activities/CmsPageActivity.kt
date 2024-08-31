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

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.View
import android.webkit.*
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityCmsPageBinding
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CMS_PAGE_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CMS_PAGE_TITLE
import com.webkul.mobikul.models.extra.CMSPageDataModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class CmsPageActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityCmsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_cms_page)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = intent.getStringExtra(BUNDLE_KEY_CMS_PAGE_TITLE)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mHashIdentifier = Utils.getMd5String("getSearchTermsList" + AppSharedPref.getStoreId(this))
        ApiConnection.getCMSPageData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), intent.getStringExtra(BUNDLE_KEY_CMS_PAGE_ID))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CMSPageDataModel>(this, true) {
                    override fun onNext(cmsPageDataModel: CMSPageDataModel) {
                        super.onNext(cmsPageDataModel)
                        if (cmsPageDataModel.success) {
                            onSuccessfulResponse(cmsPageDataModel)
                        } else {
                            onFailureResponse(cmsPageDataModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(cmsPageDataModel: CMSPageDataModel) {
        mContentViewBinding.cmsPageInfoWebView.visibility = View.GONE
        mContentViewBinding.data = cmsPageDataModel
        mContentViewBinding.cmsPageInfoWebView.settings.defaultFontSize = 14
        mContentViewBinding.cmsPageInfoWebView.settings.javaScriptEnabled = true
        mContentViewBinding.cmsPageInfoWebView.isFocusableInTouchMode = false
        mContentViewBinding.cmsPageInfoWebView.isFocusable = false
        mContentViewBinding.cmsPageInfoWebView.isClickable = false
        mContentViewBinding.cmsPageInfoWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("goo.gl")) {
                    val gmmIntentUri = Uri.parse(url)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    }
                    return true
                } else {
                    CustomTabsHelper.openTab(this@CmsPageActivity, url)
                    return true
                }

            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.toString().contains("goo.gl")) {
                    val gmmIntentUri = Uri.parse(request.url.toString())
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    }
                    return true
                } else {
                    CustomTabsHelper.openTab(this@CmsPageActivity, request.url.toString())
                    return true
                }

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mContentViewBinding.cmsPageInfoWebView.visibility = View.VISIBLE
            }

        }

        mContentViewBinding.cmsPageInfoWebView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription(getString(R.string.download))
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(this@CmsPageActivity, Environment.DIRECTORY_DOWNLOADS, ".png")
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            ToastHelper.showToast(this, getString(R.string.download_started))
        }

        mContentViewBinding.cmsPageInfoWebView.webChromeClient = WebChromeClient()
    }

    private fun onFailureResponse(cmsPageDataModel: CMSPageDataModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                cmsPageDataModel.message,
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

    private fun onErrorResponse(error: Throwable) {

        if ((!NetworkHelper.isNetworkAvailable(this@CmsPageActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@CmsPageActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@CmsPageActivity, error),
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

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, CMSPageDataModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@CmsPageActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@CmsPageActivity, error),
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
}
