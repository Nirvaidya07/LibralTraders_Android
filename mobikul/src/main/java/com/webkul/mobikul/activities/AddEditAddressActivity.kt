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
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.mobikul.locationpicker.AddressPickerActivity
import com.mobikul.locationpicker.AddressPickerActivity.Companion.RESULT_ADDRESS
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityAddEditAddressBinding
import com.webkul.mobikul.handlers.AddEditAddressActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADDRESS_COUNT
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADDRESS_DATA
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADDRESS_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_IS_GUEST_USER
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_LOGIN_EMAIL_ID
import com.webkul.mobikul.helpers.ConstantsHelper.RC_CHECK_LOCATION_SETTINGS
import com.webkul.mobikul.helpers.ConstantsHelper.RC_LOCATION_PERMISSION
import com.webkul.mobikul.helpers.ConstantsHelper.RC_LOGIN
import com.webkul.mobikul.helpers.ConstantsHelper.RC_PLACE_PICKER
import com.webkul.mobikul.models.user.*
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*

class AddEditAddressActivity : BaseActivity(), LocationListener {

    lateinit var mContentViewBinding: ActivityAddEditAddressBinding
    var mAddressId: String = ""
    var mNewAddressForCheckout: AddressDetailsData? = null
    var mLocationManager: LocationManager? = null
    var firstCall = true
    var isGuestUser = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_address)
        startInitialization()
    }

    private fun startInitialization() {
        if (intent.hasExtra(BUNDLE_KEY_ADDRESS_DATA)) {
            mNewAddressForCheckout = intent.getParcelableExtra(BUNDLE_KEY_ADDRESS_DATA)
            mContentViewBinding.showSaveInAddressBookSwitch = true
        } else {
            mAddressId = intent.getStringExtra(BUNDLE_KEY_ADDRESS_ID) ?: ""
        }
        if (intent.hasExtra(BUNDLE_KEY_IS_GUEST_USER)) {
            isGuestUser = intent.getBooleanExtra(BUNDLE_KEY_IS_GUEST_USER, false)
            mContentViewBinding.isGuestUser = isGuestUser
        }
        initSupportActionBar()
        callApi()
    }



    override fun onBackPressed() {
        if (isGuestUser) {
            val backResult = Intent()
            backResult.putExtra(BUNDLE_KEY_IS_GUEST_USER, true)

            setResult(RESULT_OK, backResult)
        }

        super.onBackPressed()
    }


    override fun initSupportActionBar() {
        if (mAddressId.isBlank() && (mNewAddressForCheckout == null || mNewAddressForCheckout!!.firstname.isNullOrEmpty()))
        /*   if (isGuestUser)
               supportActionBar?.title = getString(R.string.add_shipping_address)
           else*/
            supportActionBar?.title = getString(R.string.activity_title_add_address)
        else
            supportActionBar?.title = getString(R.string.activity_title_edit_address)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getAddressFormData" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + mAddressId)
        ApiConnection.getAddressFormData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mAddressId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddressFormResponseModel>(this, true) {
                    override fun onNext(addressFormResponseModel: AddressFormResponseModel) {
                        super.onNext(addressFormResponseModel)
                        mContentViewBinding.loading = false
                        if (addressFormResponseModel.success) {
                            onSuccessfulResponse(addressFormResponseModel)
                        } else {
                            onFailureResponse(addressFormResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(addressFormResponseModel: AddressFormResponseModel) {
        if (mNewAddressForCheckout != null) {
            if (mNewAddressForCheckout!!.firstname.isNullOrEmpty()) {
                mNewAddressForCheckout!!.prefix = addressFormResponseModel.prefixValue ?: ""
                mNewAddressForCheckout!!.firstname = addressFormResponseModel.firstName ?: ""
                mNewAddressForCheckout!!.lastname = addressFormResponseModel.lastName ?: ""
                mNewAddressForCheckout!!.suffix = addressFormResponseModel.suffixValue ?: ""
            }
            addressFormResponseModel.addressData = mNewAddressForCheckout as AddressDetailsData
        } else if (mAddressId.isEmpty() || addressFormResponseModel.addressData.firstname.isNullOrEmpty()) {
            addressFormResponseModel.addressData.prefix = addressFormResponseModel.prefixValue
            addressFormResponseModel.addressData.firstname = addressFormResponseModel.firstName
                    ?: ""
            addressFormResponseModel.addressData.lastname = addressFormResponseModel.lastName ?: ""
            addressFormResponseModel.addressData.suffix = addressFormResponseModel.suffixValue
            if (intent.hasExtra(BUNDLE_KEY_ADDRESS_COUNT) && intent.getIntExtra(BUNDLE_KEY_ADDRESS_COUNT, 0) == 0) {
                addressFormResponseModel.addressData.isDefaultBilling = true
                addressFormResponseModel.addressData.isDefaultShipping = true
            }
        }

        mContentViewBinding.data = addressFormResponseModel
        setUpPrefix()
        setUpSuffix()
        setUpCountry()
        if (!AppSharedPref.isLoggedIn(this)) {
            setupEmailChangeListener()
        }
        mContentViewBinding.handler = AddEditAddressActivityHandler(this)
    }

    private fun setUpPrefix() {
        if (mContentViewBinding.data!!.isPrefixVisible && mContentViewBinding.data!!.prefixHasOptions) {
            val prefixSpinnerData = ArrayList<String>()
            if (!mContentViewBinding.data!!.isPrefixRequired)
                prefixSpinnerData.add("")
            for (prefixIterator in 0 until mContentViewBinding.data!!.prefixOptions.size) {
                prefixSpinnerData.add(mContentViewBinding.data!!.prefixOptions[prefixIterator])
            }

            mContentViewBinding.prefixSp.adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_item, prefixSpinnerData)
            mContentViewBinding.prefixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    parent.selectedItem?.let {
                        mContentViewBinding.data?.addressData?.prefix = it.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            mContentViewBinding.prefixSp.setSelection(mContentViewBinding.data!!.getSelectedPrefixPosition())
        }
    }

    private fun setUpSuffix() {
        if (mContentViewBinding.data!!.isSuffixVisible && mContentViewBinding.data!!.suffixHasOptions) {
            val suffixSpinnerData = ArrayList<String>()
            if (!mContentViewBinding.data!!.isSuffixRequired)
                suffixSpinnerData.add("")
            for (prefixIterator in 0 until mContentViewBinding.data!!.suffixOptions.size) {
                suffixSpinnerData.add(mContentViewBinding.data!!.suffixOptions[prefixIterator])
            }

            mContentViewBinding.suffixSp.adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_item, suffixSpinnerData)
            mContentViewBinding.suffixSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    mContentViewBinding.data!!.addressData.suffix = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            mContentViewBinding.suffixSp.setSelection(mContentViewBinding.data!!.getSelectedSuffixPosition())
        }
    }

    private fun setUpCountry() {
        val countrySpinnerData = ArrayList<String>()
        for (countryIterator in 0 until mContentViewBinding.data!!.countryData.size) {
            countrySpinnerData.add(mContentViewBinding.data!!.countryData[countryIterator].name)
        }


        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_item,
                countrySpinnerData)

        mContentViewBinding.countrySp.setAdapter(adapter)

        mContentViewBinding.countrySp.onItemClickListener = AdapterView.OnItemClickListener { parent, arg1, position, id ->
            setSelectedCountry(position)
        }


        val countryPosition = mContentViewBinding.data!!.getSelectedCountryPosition()
        if (countryPosition != -1) {
            mContentViewBinding.countrySp.setText(mContentViewBinding.data!!.countryData[countryPosition].name, false)
            setSelectedCountry(countryPosition)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentViewBinding.countrySp.showSoftInputOnFocus = false
        } else {
            mContentViewBinding.countrySp.setTextIsSelectable(true);
        }
    }

    private fun setSelectedCountry(position: Int) {

        mContentViewBinding.data!!.addressData.country_id = mContentViewBinding.data!!.countryData[position].country_id
        mContentViewBinding.data!!.addressData.countryName = mContentViewBinding.data!!.countryData[position].name
        mContentViewBinding.data!!.stateRequired = mContentViewBinding.data!!.allowToChooseState || mContentViewBinding.data!!.countryData[position].isStateRequired
        if (mContentViewBinding.stateSp.adapter != null)
            mContentViewBinding.data!!.addressData.region_id = 0
        if (mContentViewBinding.data!!.countryData[position].states.size > 0) {
            mContentViewBinding.data!!.hasStateData = true
            setUpStates(mContentViewBinding.data!!.countryData[position].states)
        } else {
            if (!firstCall) {
                mContentViewBinding.data!!.addressData.region = ""
            }
            firstCall = false
            mContentViewBinding.data!!.addressData.selectedRegion = ""
            mContentViewBinding.data!!.addressData.region_id = 0
            mContentViewBinding.data!!.hasStateData = false
        }
    }

    private fun setUpStates(stateSpinnerData: ArrayList<State>) {
        val stateSpinnerLabelData = ArrayList<String>()
        stateSpinnerLabelData.add(getString(R.string.select_state_province))
        for (stateIterator in 0 until stateSpinnerData.size) {
            stateSpinnerLabelData.add(stateSpinnerData[stateIterator].name)
        }


        val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                R.layout.custom_spinner_item,
                stateSpinnerLabelData)

        mContentViewBinding.stateSp.setAdapter(adapter)

        mContentViewBinding.stateSp.onItemClickListener = AdapterView.OnItemClickListener { parent, arg1, position, id ->
            if (position == 0) {
                mContentViewBinding.data!!.addressData.region_id = 0
                mContentViewBinding.data!!.addressData.selectedRegion = ""
                mContentViewBinding.data!!.addressData.region = ""
            } else {
                setSelectedState(position - 1, stateSpinnerData)
            }
        }
        val position = mContentViewBinding.data!!.getSelectedStatePosition()
        when {
            position == 0 && mAddressId.isNullOrEmpty() -> {
                mContentViewBinding.stateSp.setSelection(position)
                mContentViewBinding.stateSp.setText(stateSpinnerLabelData[position], false)
            }
            position == 0 && !mAddressId.isNullOrEmpty() -> {
                setSelectedState(position, stateSpinnerData)
                mContentViewBinding.stateSp.setText(stateSpinnerData[position].name, false)
            }
            else -> {
                setSelectedState(position - 1, stateSpinnerData)
                mContentViewBinding.stateSp.setText(stateSpinnerData[position - 1].name, false)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentViewBinding.stateSp.showSoftInputOnFocus = false
        } else {
            mContentViewBinding.stateSp.setTextIsSelectable(true)
        }

    }

    private fun setSelectedState(position: Int, stateSpinnerData: ArrayList<State>) {
        mContentViewBinding.data!!.addressData.region_id = stateSpinnerData[position].region_id
        mContentViewBinding.data!!.addressData.selectedRegion = stateSpinnerData[position].name
        mContentViewBinding.data!!.addressData.region = ""
    }

    private fun setupEmailChangeListener() {
        mContentViewBinding.emailTiet.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if ((v as TextInputEditText).text!!.toString().trim { it <= ' ' }.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(v.text!!.toString().trim { it <= ' ' }).matches()) {
                    mContentViewBinding.loading = true
                    ApiConnection.checkCustomerByEmail(this, v.text!!.toString())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : ApiCustomCallback<CheckCustomerByEmailResponseData>(this, true) {
                                override fun onNext(responseModel: CheckCustomerByEmailResponseData) {
                                    super.onNext(responseModel)
                                    mContentViewBinding.loading = false
                                    if (responseModel.success) {
                                        if (responseModel.isCustomerExist) {
                                            showLoginAlertDialog( v.text!!.toString())
                                        }
                                    }
                                }

                                override fun onError(t: Throwable) {
                                    super.onError(t)
                                    mContentViewBinding.loading = false
                                }
                            })
                }
            }
        }
    }

    private fun showLoginAlertDialog(toString: String) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.sign_in),
                getString(R.string.customer_exist),
                false,
                getString(R.string.sign_in),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    val intent =(application as MobikulApplication).getLoginAndSignUpActivity(this)
                    intent?.putExtra(
                        BundleKeysHelper.BUNDLE_KEY_LOGIN_OR_REGISTER_PAGE,
                        ConstantsHelper.OPEN_LOGIN_PAGE
                    )
                    intent?.putExtra(BUNDLE_KEY_LOGIN_EMAIL_ID,toString)
                    startActivityForResult(intent, RC_LOGIN)
            //        startActivityForResult((application as MobikulApplication).getLoginAndSignUpActivity(this), RC_LOGIN)
                }, getString(R.string.continue_text), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    private fun onFailureResponse(addressFormResponseModel: AddressFormResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                addressFormResponseModel.message,
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

        if ((!NetworkHelper.isNetworkAvailable(this@AddEditAddressActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@AddEditAddressActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@AddEditAddressActivity, error),
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContentViewBinding.handler?.onClickGetCurrentLocation()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_LOGIN) {
                setResult(RESULT_OK, Intent())
                finish()
            } else if (requestCode == RC_PLACE_PICKER) {
                val address: Address? = data?.getParcelableExtra(RESULT_ADDRESS) as? Address
                onNewLocation(address?.latitude.toString(), address?.longitude.toString())
            } else if (requestCode == RC_CHECK_LOCATION_SETTINGS) {
                mContentViewBinding.loading = true
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                if (getString(R.string.maps_api_key).isNullOrEmpty()) {
                    mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
                } else {
                    val intent = Intent(this@AddEditAddressActivity, AddressPickerActivity::class.java)
                    intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL, 16.0f)
                    startActivityForResult(intent, RC_PLACE_PICKER)
                }
            }
        } else if (requestCode == RC_PLACE_PICKER) {
            mContentViewBinding.loading = false
        }
    }

    override fun onLocationChanged(location: Location) {

        val latitude = location.latitude
        val longitude = location.longitude
        onNewLocation(latitude.toString(), longitude.toString())
        mLocationManager?.removeUpdates(this)
    }

     fun onNewLocation(latitude: String, longitude: String) {
        mContentViewBinding.loading = true
        try {
            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)

            if (addresses != null) {
                mContentViewBinding.data!!.addressData.street = setStreetAddress(addresses)
                mContentViewBinding.data!!.addressData.city = addresses[0].locality
                mContentViewBinding.data!!.addressData.selectedRegion = addresses[0].adminArea
                mContentViewBinding.data!!.addressData.postcode = addresses[0].postalCode ?: ""

                if (mContentViewBinding.data!!.countryData.isNotEmpty()) {
                    mContentViewBinding.data!!.addressData.country_id = addresses[0].countryCode
                    mContentViewBinding.data!!.addressData.countryName = addresses[0].countryName
                    mContentViewBinding.data = mContentViewBinding.data

                    val newCountryPosition = mContentViewBinding.data!!.getSelectedCountryPosition()
                    if (newCountryPosition == -1) {
                        mContentViewBinding.data!!.addressData.country_id = mContentViewBinding.data!!.countryData[0].country_id
                        mContentViewBinding.data!!.addressData.countryName = mContentViewBinding.data!!.countryData[0].name
                        mContentViewBinding.data = mContentViewBinding.data
                        mContentViewBinding.countrySp.setText(mContentViewBinding.data!!.countryData[0].name, false)
                        setSelectedCountry(0)
                    } else {
                        mContentViewBinding.countrySp.setText(mContentViewBinding.data!!.countryData[newCountryPosition].name, false)
                        setSelectedCountry(newCountryPosition)
                    }
                    if (mContentViewBinding.data!!.countryData.size == 1 && mContentViewBinding.data!!.hasStateData) {
                        mContentViewBinding.data!!.addressData.region_id = 0
                        setSelectedState(mContentViewBinding.data!!.getSelectedStatePosition() + 1, mContentViewBinding.data!!.countryData[if (newCountryPosition == -1) 0 else newCountryPosition].states)
                        mContentViewBinding.data!!.addressData.region_id = mContentViewBinding.data!!.selectedRegionId
                    }
                } else {
                    mContentViewBinding.data = mContentViewBinding.data
                }
            } else {
                ToastHelper.showToast(this, getString(R.string.unable_to_get_exact_address))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastHelper.showToast(this, getString(R.string.unable_to_get_exact_address))
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ToastHelper.showToast(this, getString(R.string.access_needed_to_fill_address))
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION_PERMISSION)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION_PERMISSION)
            }
            return
        }
        mContentViewBinding.loading = false
    }

    private fun setStreetAddress(addresses: MutableList<Address>): ArrayList<String> {
        val street = ArrayList<String>()
        val addressLine = addresses[0].getAddressLine(0).split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        when (mContentViewBinding.data!!.streetLineCount) {
            1 -> {
                when (addressLine.size) {
                    0 -> {
                    }
                    1 -> street.add(addressLine[0])
                    2 -> street.add(addressLine[0] + ", " + addressLine[1])
                    else -> {
                        street.add(addressLine[0] + ", " + addressLine[1] + ", " + addressLine[2])
                    }
                }
            }
            2 -> {
                when (addressLine.size) {
                    0 -> {
                    }
                    1 -> {
                        street.add(addressLine[0])
                        street.add("")
                    }
                    2 -> {
                        street.add(addressLine[0])
                        street.add(addressLine[1])
                    }
                    else -> {
                        street.add(addressLine[0] + ", " + addressLine[1])
                        street.add(addressLine[2])
                    }
                }
            }
            3 -> {
                when (addressLine.size) {
                    0 -> {
                    }
                    1 -> {
                        street.add(addressLine[0])
                        street.add("")
                    }
                    2 -> {
                        street.add(addressLine[0])
                        street.add(addressLine[1])
                    }
                    else -> {
                        street.add(addressLine[0] + ", " + addressLine[1])
                        street.add(addressLine[2])
                    }
                }
                street.add("")
            }
            4 -> {
                when (addressLine.size) {
                    0 -> {
                    }
                    1 -> {
                        street.add(addressLine[0])
                        street.add("")
                    }
                    2 -> {
                        street.add(addressLine[0])
                        street.add(addressLine[1])
                    }
                    else -> {
                        street.add(addressLine[0] + ", " + addressLine[1])
                        street.add(addressLine[2])
                    }
                }
                street.add("")
                street.add("")
            }
        }
        return street
    }


    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String) {

    }

    override fun onProviderDisabled(p0: String) {

    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, AddressFormResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@AddEditAddressActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@AddEditAddressActivity, error),
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