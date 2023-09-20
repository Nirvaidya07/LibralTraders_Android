//package com.mobikul.locationpicker
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.location.Address
//import android.location.Geocoder
//import android.location.Location
//import io.reactivex.Observable
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import org.json.JSONArray
//import java.util.*
//
//class GeocoderHelper {
//    private val running = false
//    @SuppressLint("CheckResult")
//    fun fetchCityName(contex: Context?, location: Location,onAddressResult:  (Address) -> Unit) {
//        if (running) return
//        Observable.fromCallable {
//            var address: Address? = null
//            if (Geocoder.isPresent()) {
//                try {
//                    val geocoder = Geocoder(contex, Locale.getDefault())
//                    val addresses =
//                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                    if (addresses.size > 0) {
//                        address = addresses[0]
//                    }
//                } catch (ignored: Exception) {
//                    // after a while, Geocoder start to trhow "Service not availalbe" exception.
//                    // really weird since it was working before (same device, same Android version etc..
//                }
//            }
//            if (address != null) // i.e., Geocoder succeed
//            {
//                return@fromCallable address
//            } else  // i.e., Geocoder failed
//            {
//                return@fromCallable fetchAddressUsingGoogleMap()
//            }
//            false
//        }.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { result: Any? ->
//                onAddressResult(result as Address)
//            }
//
//    }
//
////     Our B Plan : Google Map
//private fun fetchAddressUsingGoogleMap(): Address {
//    val googleMapUrl =
//        ("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude()
//            .toString() + ","
//                + location.getLongitude().toString() + "&sensor=false&language=fr")
//    try {
//        val googleMapResponse = JSONObject(
//            ANDROID_HTTP_CLIENT.execute(
//                HttpGet(googleMapUrl),
//                BasicResponseHandler()
//            )
//        )
//
//        // many nested loops.. not great -> use expression instead
//        // loop among all results
//        val results = googleMapResponse.get("results") as JSONArray
//        for (i in 0 until results.length()) {
//            // loop among all addresses within this result
//            val result = results.getJSONObject(i)
//            if (result.has("address_components")) {
//                val addressComponents = result.getJSONArray("address_components")
//                // loop among all address component to find a 'locality' or 'sublocality'
//                for (j in 0 until addressComponents.length()) {
//                    val addressComponent = addressComponents.getJSONObject(j)
//                    if (result.has("types")) {
//                        val types = addressComponent.getJSONArray("types")
//
//                        // search for locality and sublocality
//                        var cityName: String? = null
//                        for (k in 0 until types.length()) {
//                            if ("locality" == types.getString(k) && cityName == null) {
//                                if (addressComponent.has("long_name")) {
//                                    cityName = addressComponent.getString("long_name")
//                                } else if (addressComponent.has("short_name")) {
//                                    cityName = addressComponent.getString("short_name")
//                                }
//                            }
//                            if ("sublocality" == types.getString(k)) {
//                                if (addressComponent.has("long_name")) {
//                                    cityName = addressComponent.getString("long_name")
//                                } else if (addressComponent.has("short_name")) {
//                                    cityName = addressComponent.getString("short_name")
//                                }
//                            }
//                        }
//                        if (cityName != null) {
//                            return cityName
//                        }
//                    }
//                }
//            }
//        }
//    } catch (ignored: Exception) {
//        ignored.printStackTrace()
//    }
//    return null
//}
//
//}