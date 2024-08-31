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

package com.webkul.mobikul.handlers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.SplashScreenActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.homepage.WebsiteData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WebsitesRvHandler {

    fun onClickWebsiteItem(view: View, website: WebsiteData) {
        if (AppSharedPref.getWebsiteId(view.context) != website.id) {
            if (AppSharedPref.isLoggedIn(view.context)) {
                onLogout(view, website)
            } else {
                setWebsiteData(view, website)
            }
        }
    }

    private fun setWebsiteData(view: View, website: WebsiteData) {
        website.id?.let { AppSharedPref.setWebsiteId(view.context, it) }
        website.name?.let { AppSharedPref.setWebsiteLabel(view.context, it) }
        AppSharedPref.setStoreId(view.context, "0")

        AppSharedPref.setShowSplash(view.context, false)

        val intent = Intent(view.context, SplashScreenActivity::class.java)
        view.context.startActivity(intent)
    }

    fun onLogout(view: View, website: WebsiteData) {
        Handler(Looper.getMainLooper()).postDelayed({
            val context = view.context
            val websiteLabel = AppSharedPref.getWebsiteLabel(view.context)
            AlertDialogHelper.showNewCustomDialog(
                context as BaseActivity,
                context.getString(R.string.warning),
                context.getString(R.string.website_logout_warning, websiteLabel),
                false,
                context.getString(R.string.ok),
                { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                    deleteTokenFromCloud(context)
                    ApiConnection.logout(context)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : ApiCustomCallback<BaseModel>(context, true) {
                            override fun onNext(responseModel: BaseModel) {
                            }

                            override fun onError(e: Throwable) {
                            }
                        })
                    setWebsiteData(view, website)
                }, context.getString(R.string.cancel), { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                })
        }, 300)
    }

    private fun deleteTokenFromCloud(context: Context?) {
        ApiConnection.deleteTokenFromCloud(context!!, "seller")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCustomCallback<BaseModel>(context, true) {
                override fun onNext(responseModel: BaseModel) {
                }

                override fun onError(e: Throwable) {
                }
            })
        ApiConnection.deleteTokenFromCloud(context, "customer")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCustomCallback<BaseModel>(context, true) {
                override fun onNext(responseModel: BaseModel) {
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}