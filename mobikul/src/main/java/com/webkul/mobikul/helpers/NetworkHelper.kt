package com.webkul.mobikul.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.libraltraders.android.R
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

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

class NetworkHelper {

    companion object {

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected==true
        }

        fun isNetworkConnected(context: Context) : Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val capabilities =  connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                capabilities.also {
                    if (it != null){
                        if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                            return true
                        else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                            return true
                        }
                    }
                }
            } else {
          //      TODO("VERSION.SDK_INT < M")
            }

            return false
        }

        fun getErrorMessage(context: Context?, error: Throwable): String? {
            if (context != null) {
                if (!isNetworkAvailable(context))
                    return context.getString(R.string.error_internet)
                else if (error is HttpException && error.code() >= 500 && error.code() < 600)
                    return context.getString(R.string.error_server)
                else if (error is SocketTimeoutException || error is SocketException)
                    return context.getString(R.string.error_timeout)
                else
                    return context.getString(R.string.error_request)
            }
            return null
        }
    }
}