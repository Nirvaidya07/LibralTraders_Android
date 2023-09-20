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

package com.webkul.mobikul.helpers

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup


class VersionChecker(private val context: Context, val onLatestVersionResponse: (String?)-> Unit){

    @SuppressLint("CheckResult")
    fun getUpdatedResponse(){

        onVersionChecker(context)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.isNotBlank()) {
                    onLatestVersionResponse(it)
                }
                }
            }
    }

    fun onVersionChecker( context: Context): Observable<String> {
        return Observable.fromCallable {
            onLatestVersionResponse(context)
        }
    }

    fun onLatestVersionResponse(context: Context):String{
        var newVersion = "1.00"
        try {
            val document = Jsoup.connect("https://play.google.com/store/apps/details?id=${context.packageName}")
                .timeout(10000)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.google.com")
                .get()
            if (document != null) {
                val element = document.getElementsContainingOwnText("Current Version")
                for (ele in element) {
                    if (ele.siblingElements() != null) {
                        val sibElements = ele.siblingElements()
                        for (sibElement in sibElements) {
                            newVersion = sibElement.text()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newVersion
    }



