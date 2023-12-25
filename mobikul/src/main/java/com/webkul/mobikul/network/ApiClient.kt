package com.webkul.mobikul.network

import android.util.Log
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.ApplicationConstants.API_AUTH_TYPE
import com.webkul.mobikul.helpers.ApplicationConstants.BASE_URL
import com.webkul.mobikul.helpers.AuthKeyHelper
import com.webkul.mobikul.helpers.Utils.Companion.getMd5String
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.UnsupportedEncodingException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URLDecoder
import java.nio.charset.Charset
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ApiClient {

    companion object {

        private const val TAG = "ApiClient"
        private const val CONNECT_TIMEOUT_MULTIPLIER = 1
        private const val DEFAULT_CONNECT_TIMEOUT_IN_SEC = 60 * CONNECT_TIMEOUT_MULTIPLIER
        private const val DEFAULT_WRITE_TIMEOUT_IN_SEC = 60 * CONNECT_TIMEOUT_MULTIPLIER
        private const val DEFAULT_READ_TIMEOUT_IN_SEC = 60 * CONNECT_TIMEOUT_MULTIPLIER

        private const val NO_OF_LOG_CHAR = 1000

        private var sRetrofitClient: Retrofit? = null
        private val sDispatcher: Dispatcher? = null

        fun getClient(): Retrofit? {
            if (sRetrofitClient == null) {
                sRetrofitClient = Retrofit.Builder()
                        .baseUrl("$BASE_URL/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(JacksonConverterFactory.create())
                        .client(getOkHttpClientBuilder().build())
//                        .client(getUnsafeOkHttpClientBuilder().build())
                        .build()
            }
            return sRetrofitClient
        }
        fun getClient1(): Retrofit? {
            if (sRetrofitClient == null) {
                sRetrofitClient = Retrofit.Builder()
                    .baseUrl("$BASE_URL/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(getOkHttpClientBuilder1().build())
//                        .client(getUnsafeOkHttpClientBuilder().build())
                    .build()
            }
            return sRetrofitClient
        }

        private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            /*OkHttp client builder*/
            val oktHttpClientBuilder = OkHttpClient.Builder()
                    .connectTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_CONNECT_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .writeTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_WRITE_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .readTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_READ_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .cookieJar(JavaNetCookieJar(getCookieManager())) /* Using okhttp3 cookie instead of java net cookie*/
            oktHttpClientBuilder.dispatcher(getDispatcher())

            oktHttpClientBuilder.addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                        .addHeader("Content-Type", "text/html")
                        .addHeader("authKey", AuthKeyHelper.getInstance().authKey)
                        .addHeader("apiAuthType", API_AUTH_TYPE.toLowerCase())
                        .addHeader("token", AuthKeyHelper.getInstance().token.toString())
                chain.proceed(builder.build())
            }

            oktHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor())
            oktHttpClientBuilder.addInterceptor { chain ->
                var request = chain.request()

                printPostmanFormattedLog(request)

                var response = chain.proceed(request)
                Log.d(TAG, "intercept: " + response.code)
                val token = response.header("token")
                if (response.code == 401 && token != null && token.isNotEmpty()) {
                    val usernamePasswordMd5 = getMd5String(ApplicationConstants.API_USER_NAME + ":" + ApplicationConstants.API_PASSWORD)
                    AuthKeyHelper.getInstance().authKey = getMd5String("$usernamePasswordMd5:$token")
                    Log.d(TAG, "token: $token")
                    Log.d(TAG, "authKey: " + AuthKeyHelper.getInstance().authKey)
                    response.close()
                    request = request.newBuilder().header("authKey", AuthKeyHelper.getInstance().authKey).build()
                    response = chain.proceed(request)
                }
                response
            }
            return oktHttpClientBuilder
        }
        private fun getOkHttpClientBuilder1(): OkHttpClient.Builder {
            /*OkHttp client builder*/
            val oktHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_CONNECT_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                .writeTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_WRITE_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                .readTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_READ_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                .cookieJar(JavaNetCookieJar(getCookieManager())) /* Using okhttp3 cookie instead of java net cookie*/
            oktHttpClientBuilder.dispatcher(getDispatcher())

            oktHttpClientBuilder.addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("Content-Type", "text/html")
                    .addHeader("authKey", AuthKeyHelper.getInstance().authKey)
                    .addHeader("apiAuthType", API_AUTH_TYPE.toLowerCase())
                    .addHeader("token", AuthKeyHelper.getInstance().token.toString())
                chain.proceed(builder.build())
            }

            oktHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor())
            oktHttpClientBuilder.addInterceptor { chain ->
                var request = chain.request()

                printPostmanFormattedLog(request)

                var response = chain.proceed(request)
                Log.d(TAG, "intercept: " + response.code)
                val token = response.header("token")
                if (response.code == 401 && token != null && token.isNotEmpty()) {
                    val usernamePasswordMd5 = getMd5String(ApplicationConstants.API_USER_NAME + ":" + ApplicationConstants.API_PASSWORD)
                    AuthKeyHelper.getInstance().authKey = getMd5String("$usernamePasswordMd5:$token")
                    Log.d(TAG, "token: $token")
                    Log.d(TAG, "authKey: " + AuthKeyHelper.getInstance().authKey)
                    response.close()
                    request = request.newBuilder().header("authKey", AuthKeyHelper.getInstance().authKey).build()
                    response = chain.proceed(request)
                }
                response
            }
            return oktHttpClientBuilder
        }
        private fun getUnsafeOkHttpClientBuilder(): OkHttpClient.Builder {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate?> {
                    return arrayOfNulls(0)
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val oktHttpClientBuilder = OkHttpClient.Builder()
                    .connectTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_CONNECT_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .writeTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_WRITE_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .readTimeout((CONNECT_TIMEOUT_MULTIPLIER * DEFAULT_READ_TIMEOUT_IN_SEC).toLong(), TimeUnit.SECONDS)
                    .cookieJar(JavaNetCookieJar(getCookieManager())) /* Using okhttp3 cookie instead of java net cookie*/
            oktHttpClientBuilder.dispatcher(getDispatcher())

            oktHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)
            oktHttpClientBuilder.hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String, session: SSLSession): Boolean {
                    return true
                }
            })

            oktHttpClientBuilder.addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                        .addHeader("Content-Type", "text/json")
                        .addHeader("Authorization","Bearer "+ AuthKeyHelper.getInstance().token.toString())
                chain.proceed(builder.build())
            }

            oktHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor())
//            oktHttpClientBuilder.addInterceptor { chain ->
//                var request = chain.request()
//
//                printPostmanFormattedLog(request)
//
//                var response = chain.proceed(request)
//                Log.d(TAG, "intercept: " + response.code)
//                val token = response.header("token")
//                if (response.code == 401 && token != null && token.isNotEmpty()) {
//                    val usernamePasswordMd5 = getMd5String(ApplicationConstants.API_USER_NAME + ":" + ApplicationConstants.API_PASSWORD)
//                    AuthKeyHelper.getInstance().authKey = getMd5String("$usernamePasswordMd5:$token")
//                    Log.d(TAG, "token: $token")
//                    Log.d(TAG, "authKey: " + AuthKeyHelper.getInstance().authKey)
//                    response.close()
//                    request = request.newBuilder().header("authKey", AuthKeyHelper.getInstance().authKey).build()
//                    response = chain.proceed(request)
//                }
//                response
//            }

            return oktHttpClientBuilder
        }

        private fun printPostmanFormattedLog(request: Request) {
            try {
                val allParams: String
                allParams = if (request.method == "GET" || request.method == "DELETE") {
                    request.url.toString().substring(request.url.toString().indexOf("?") + 1, request.url.toString().length)
                } else {
                    val buffer = Buffer()
                    request.body!!.writeTo(buffer)
                    buffer.readString(Charset.forName("UTF-8"))
                }
                val params = allParams.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val paramsString = StringBuilder("\n")
                for (param in params) {
                    paramsString.append(decode(param.replace("=", ":")))
                    paramsString.append("\n")
                }
                Log.d(TAG, paramsString.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        private fun getHttpLoggingInterceptor(): Interceptor {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                if (message.length > NO_OF_LOG_CHAR) {
                    for (noOfLogs in 0..message.length / NO_OF_LOG_CHAR) {
                        if (noOfLogs * NO_OF_LOG_CHAR + NO_OF_LOG_CHAR < message.length) {
                            Log.d(TAG, message.substring(noOfLogs * NO_OF_LOG_CHAR, noOfLogs * NO_OF_LOG_CHAR + NO_OF_LOG_CHAR))
                        } else {
                            Log.d(TAG, message.substring(noOfLogs * NO_OF_LOG_CHAR, message.length))
                        }
                    }
                } else {
                    Log.d(TAG, message)
                }
            })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return loggingInterceptor
        }

        private fun getCookieManager(): CookieManager {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            return cookieManager
        }

        fun getDispatcher(): Dispatcher {
            return sDispatcher ?: Dispatcher()
        }

        private fun decode(url: String): String {
            return try {
                var prevURL = ""
                var decodeURL = url
                while (prevURL != decodeURL) {
                    prevURL = decodeURL
                    decodeURL = URLDecoder.decode(decodeURL, "UTF-8")
                }
                decodeURL
            } catch (e: UnsupportedEncodingException) {
                "Issue while decoding" + e.message
            }
        }
    }
}
