package com.webkul.mobikul.fragments


import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Rational
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.FragmentTrackDeliveryBoyBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADMIN_ADDRESS
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADMIN_LATITUDE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADMIN_LONGITUDE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CUSTOMER_ADDRESS
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_DELIVERY_BOY_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_PICKED
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.user.GetDeliveryBoyLocationResponseData
import com.webkul.mobikul.network.ApiClient
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeUnit

class TrackDeliveryBoyFragment : BaseFragment() {

    private val LOCATION_UPDATE_INTERVAL = 10000 // 10 seconds by default, can be changed later

    companion object {
        fun newInstance(deliveryBoyId: String, isPicked: Boolean, adminAddress: String, adminLat: String, adminLng: String, customerAddress: String): TrackDeliveryBoyFragment {
            val trackDeliveryBoyFragment = TrackDeliveryBoyFragment()
            val args = Bundle()
            args.putString(BUNDLE_KEY_DELIVERY_BOY_ID, deliveryBoyId)
            args.putBoolean(BUNDLE_KEY_IS_PICKED, isPicked)
            args.putString(BUNDLE_KEY_ADMIN_ADDRESS, adminAddress)
            args.putString(BUNDLE_KEY_ADMIN_LATITUDE, adminLat)
            args.putString(BUNDLE_KEY_ADMIN_LONGITUDE, adminLng)
            args.putString(BUNDLE_KEY_CUSTOMER_ADDRESS, customerAddress)
            trackDeliveryBoyFragment.arguments = args
            return trackDeliveryBoyFragment
        }
    }

    lateinit var mContentViewBinding: FragmentTrackDeliveryBoyBinding
    private var mDeliveryBoyId: String = ""
    private var mAdminAddress: String = ""
    private var mCustomerAddress: String = ""
    private var mIsPicked: Boolean = false

    private var mAdminLatLng: LatLng? = null
    private var mDeliveryBoyLatLng: LatLng? = null
    private var mCustomerBoyLatLng: LatLng? = null
    private var mPolyline: Polyline? = null

    private lateinit var mMapView: MapView
    private lateinit var mGoogleMap: GoogleMap
    private var mDeliveryBoyMarker: Marker? = null
    private var mLocationHandler: Handler? = null
    private val mLocationUpdater = object : Runnable {
        override fun run() {
            try {
                if (mLocationHandler != null) {
                    getLocation()
                    mLocationHandler!!.postDelayed(this, LOCATION_UPDATE_INTERVAL.toLong())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_delivery_boy, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            mDeliveryBoyId = requireArguments().getString(BUNDLE_KEY_DELIVERY_BOY_ID)?:""
            mIsPicked = requireArguments().getBoolean(BUNDLE_KEY_IS_PICKED)
            mAdminLatLng = LatLng(requireArguments().getString(BUNDLE_KEY_ADMIN_LATITUDE)?.toDouble()?:0.0, requireArguments().getString(BUNDLE_KEY_ADMIN_LONGITUDE)?.toDouble()?:0.0)
            mAdminAddress = requireArguments().getString(BUNDLE_KEY_ADMIN_ADDRESS)?:""
            mCustomerAddress = requireArguments().getString(BUNDLE_KEY_CUSTOMER_ADDRESS)?:""
        }

        try {
            mMapView = mContentViewBinding.map
            mMapView.onCreate(savedInstanceState)
            mMapView.onResume()
            MapsInitializer.initialize(requireActivity().applicationContext)
            addGoogleMapFragment()
            mLocationHandler = Handler()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addGoogleMapFragment() {
        mMapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            addAdminCustomerLocation()
            startLocationUpdates()
        }
    }

    private fun getLocation() {
        ApiConnection.getDeliveryBoyLocation(requireContext(), mDeliveryBoyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<GetDeliveryBoyLocationResponseData>(requireContext(), false) {
                    override fun onNext(getDeliveryBoyLocationResponseData: GetDeliveryBoyLocationResponseData) {
                        super.onNext(getDeliveryBoyLocationResponseData)
                        if (getDeliveryBoyLocationResponseData.success) {
                            if (getDeliveryBoyLocationResponseData.latitude != null && getDeliveryBoyLocationResponseData.longitude != null)
                                mDeliveryBoyLatLng = LatLng(getDeliveryBoyLocationResponseData.latitude.toDouble(), getDeliveryBoyLocationResponseData.longitude.toDouble())
                            updateDeliveryBoyLocation()
                        } else {
                            ToastHelper.showToast(context, getDeliveryBoyLocationResponseData.message)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        ToastHelper.showToast(context, NetworkHelper.getErrorMessage(context, e)!!)
                    }
                })
    }

    private fun updateDeliveryBoyLocation() {
        if (mDeliveryBoyMarker == null) {
            val deliveryBoyMarker = MarkerOptions()
                    .position(mDeliveryBoyLatLng!!)
                    .icon(getBitmapDescriptorForMaps("deliveryboy"))
            mDeliveryBoyMarker = mGoogleMap.addMarker(deliveryBoyMarker)
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(mDeliveryBoyLatLng!!)
                    .tilt(45f)
                    .zoom(13f)
                    .build()))
        } else {
            mDeliveryBoyMarker?.position = mDeliveryBoyLatLng!!
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mDeliveryBoyLatLng!!))
        }

        try {
            if (mIsPicked && mDeliveryBoyLatLng != null && mCustomerBoyLatLng != null) {
                DrawPolyLines(this).execute()
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    internal class DrawPolyLines(context: TrackDeliveryBoyFragment) {
        private val mFragmentContext: TrackDeliveryBoyFragment = context

        @SuppressLint("CheckResult")
        fun execute() {
            io.reactivex.Observable.fromCallable {
                return@fromCallable try {
                    val req = DirectionsApi.getDirections(mFragmentContext.getGeoContext(), "${mFragmentContext.mDeliveryBoyLatLng!!.latitude},${mFragmentContext.mDeliveryBoyLatLng!!.longitude}", "${mFragmentContext.mCustomerBoyLatLng!!.latitude},${mFragmentContext.mCustomerBoyLatLng!!.longitude}")
                    req.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
                .subscribeOn(Schedulers.io()) // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread()) // execute this RxJava
                .subscribe { res ->
                    if (res != null)
                        mFragmentContext.addPolyline(res)
                }
        }
    }

    private fun addAdminCustomerLocation() {
        // Setting Admin Marker
        val adminLatLng = LatLng(mAdminLatLng!!.latitude, mAdminLatLng!!.longitude)
        val adminMarker = MarkerOptions()
                .position(adminLatLng)
                .icon(getBitmapDescriptorForMaps("admin"))
        mGoogleMap.addMarker(adminMarker)

        // Setting Customer Marker
        mCustomerBoyLatLng = getLocationFromAddress(mCustomerAddress)
        if (mCustomerBoyLatLng != null) {
            val customerMarker = MarkerOptions()
                    .position(mCustomerBoyLatLng!!)
                    .icon(getBitmapDescriptorForMaps("customer"))
            mGoogleMap.addMarker(customerMarker)
        }

        // Zoom between admin and customer
        if (mCustomerBoyLatLng != null) {
            try {
                val adminCustomerBound = LatLngBounds(adminLatLng, mCustomerBoyLatLng!!)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(adminCustomerBound, 200))
            } catch (e: Exception) {
                e.printStackTrace()
                val adminCustomerBound = LatLngBounds(mCustomerBoyLatLng!!, adminLatLng)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(adminCustomerBound, 200))
            } finally {

            }
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {
        val coder = context?.let { Geocoder(it) }
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            address = coder?.getFromLocationName(strAddress, 1)
            if (address != null && address.isNotEmpty()) {
                val location = address[0]
                p1 = LatLng(location.latitude, location.longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return p1
    }

    private fun getBitmapDescriptorForMaps(type: String): BitmapDescriptor {
        var customMarkerView: View? = null
        try {
            when (type) {
                "admin" -> customMarkerView = (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.marker_admin, null)
                "deliveryboy" -> customMarkerView = (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.marker_delivery_boy, null)
                "customer" -> customMarkerView = (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.marker_customer, null)
                else -> customMarkerView = (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.marker_customer, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        customMarkerView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(returnedBitmap)
    }

    private fun getGeoContext(): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey(getString(R.string.maps_api_key))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun addPolyline(results: DirectionsResult) {
        try {
            if (results.routes.isNotEmpty()) {
                if (mPolyline != null) {
                    mPolyline?.remove()
                }
                val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
                mPolyline = mGoogleMap.addPolyline(PolylineOptions().color(ContextCompat.getColor(requireContext(), R.color.colorAccent)).addAll(decodedPath))

                if (results.routes[0].legs.isNotEmpty()) {
                    mContentViewBinding.estimateLayout.visibility = View.VISIBLE
                    mContentViewBinding.time = results.routes[0].legs[0].duration.humanReadable
                } else {
                    mContentViewBinding.estimateLayout.visibility = View.GONE
                }
            } else {
                mContentViewBinding.estimateLayout.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLocationUpdates() {
        mLocationUpdater.run()
    }

    override fun onPause() {
        super.onPause()
        if (!isRemoving && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(300, 400)
            activity?.enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(aspectRatio).build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
        (context as BaseActivity).mCompositeDisposable.clear()
        stopLocationUpdates()
        ApiClient.getDispatcher().cancelAll()
        Utils.enableUserInteraction(requireContext())
    }

    private fun stopLocationUpdates() {
        mLocationHandler?.removeCallbacks(mLocationUpdater)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        if (isInPictureInPictureMode) {
            mContentViewBinding.estimateTimeText.visibility = View.GONE
            mContentViewBinding.colon.visibility = View.GONE
            mContentViewBinding.timeTv.gravity = Gravity.CENTER
        } else {
            mContentViewBinding.estimateTimeText.visibility = View.VISIBLE
            mContentViewBinding.colon.visibility = View.VISIBLE
            mContentViewBinding.timeTv.gravity = Gravity.END
        }
    }
}