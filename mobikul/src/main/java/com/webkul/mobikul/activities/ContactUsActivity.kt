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

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityContactUsBinding
import com.webkul.mobikul.handlers.ContactUsActivityHandler
import com.webkul.mobikul.helpers.AppSharedPref
import java.io.IOException


class ContactUsActivity : BaseActivity(), OnMapReadyCallback {

    lateinit var mContentViewBinding: ActivityContactUsBinding
    private var mMap: GoogleMap? = null
    private val address = "Libral Traders Pvt. Ltd, B-84/1, Pocket X, Okhla Phase II, Okhla Industrial Estate, New Delhi, Delhi 110020, India"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        setupAutoFillData()

        mContentViewBinding.handler = ContactUsActivityHandler(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_contact_us)
        super.initSupportActionBar()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocationOnMap(address);
    }

    private fun setupAutoFillData() {

        if (AppSharedPref.isLoggedIn(this)) {
            mContentViewBinding.name.setText(AppSharedPref.getCustomerName(this))
            mContentViewBinding.email.setText(AppSharedPref.getCustomerEmail(this))
            mContentViewBinding.msg.requestFocus()
        }
    }

    private fun showLocationOnMap(address: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location: Address = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)

                // Log full details for debugging
                Log.d("Geocoder", "Address found: ${location.getAddressLine(0)}")
                Log.d("Geocoder", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")

                // Add a marker and move the camera
                mMap!!.addMarker(MarkerOptions().position(latLng).title("Libral Traders Pvt. Ltd."))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            } else {
                // Handle case where address is not found
                Log.e("Geocoder", "Address not found")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IOException
        }
    }


}