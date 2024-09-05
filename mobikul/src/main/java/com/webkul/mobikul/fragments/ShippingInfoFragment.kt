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

package com.webkul.mobikul.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.activities.CheckoutActivity
import com.libraltraders.android.databinding.FragmentShippingInfoBinding
import com.libraltraders.android.databinding.ItemCheckoutShippingMethodBinding
import com.webkul.mobikul.handlers.ShippingInfoFragmentHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ADDRESS_DATA
import com.webkul.mobikul.helpers.ConstantsHelper.RC_ADD_EDIT_ADDRESS
import com.webkul.mobikul.models.checkout.BillingShippingAddress
import com.webkul.mobikul.models.checkout.CheckoutAddressInfoResponseModel
import com.webkul.mobikul.models.checkout.ShippingMethodsModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.github.inflationx.calligraphy3.TypefaceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ShippingInfoFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentShippingInfoBinding
    private var mShippingMethodsList: ArrayList<View> = ArrayList()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shipping_info, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        context?.let { context ->
        initSupportActionBar()
        mContentViewBinding.handler = ShippingInfoFragmentHandler(this)

        if (!AppSharedPref.isLoggedIn(context)) {
            if (AppSharedPref.getCustomerCachedNewAddress(context).firstname.isNullOrEmpty()) {
                mContentViewBinding.handler?.onClickAddEditNewAddress()
            }
        }
        callApi()
        }
    }

    fun initSupportActionBar() {
        (context as CheckoutActivity).supportActionBar?.title = getString(R.string.shipping)
        (context as CheckoutActivity).initSupportActionBar()
        (context as CheckoutActivity).mContentViewBinding.paymentPageIndicator.setBackgroundColor(ContextCompat.getColor(context as CheckoutActivity, R.color.divider_color))
    }

    fun callApi() {
        (context as CheckoutActivity).mContentViewBinding.loading = true
        ApiConnection.getCheckoutAddressInfo(context as CheckoutActivity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CheckoutAddressInfoResponseModel>(context as CheckoutActivity, true) {
                    override fun onNext(checkoutAddressInfoResponseModel: CheckoutAddressInfoResponseModel) {
                        super.onNext(checkoutAddressInfoResponseModel)
                        (context as CheckoutActivity).mContentViewBinding.loading = false
                        if (checkoutAddressInfoResponseModel.success) {
                            onSuccessfulResponse(checkoutAddressInfoResponseModel)
                        } else {
                            onFailureResponse(checkoutAddressInfoResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        (context as CheckoutActivity).mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(checkoutAddressInfoResponseModel: CheckoutAddressInfoResponseModel) {
        mContentViewBinding.data = checkoutAddressInfoResponseModel

        setNewAddressModel()
        setupSelectedCheckoutAddress()
        setupScrollView()
    }

    private fun setNewAddressModel() {
        mContentViewBinding.data!!.newAddressData = AppSharedPref.getCustomerCachedNewAddress(requireContext())
        mContentViewBinding.data!!.newAddressData?.isDefaultBilling = true
        mContentViewBinding.data!!.newAddressData?.isDefaultShipping = true
        if (!mContentViewBinding.data!!.newAddressData?.firstname.isNullOrEmpty()) {
            mContentViewBinding.data!!.hasNewAddress = true

            val newBillingShippingAddress = BillingShippingAddress()
            newBillingShippingAddress.id = "new"
            newBillingShippingAddress.value = mContentViewBinding.data!!.newAddressData?.getFormattedAddress()
            mContentViewBinding.data!!.addressData?.add(newBillingShippingAddress)
        }
    }

    private fun setupSelectedCheckoutAddress() {
        if (!mContentViewBinding.data!!.addressData.isNullOrEmpty()) {
            mContentViewBinding.data!!.addressData?.get(0)?.let { setSelectedAddressData(it) }
        } else {

            //      getShippingMethods()
        }
    }

    private fun setupScrollView() {
        mContentViewBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (scrollY - oldScrollY < 0 || scrollY > mContentViewBinding.scrollView.getChildAt(0).height - mContentViewBinding.scrollView.height - 100) {
                mContentViewBinding.nextBtn.animate().alpha(1.0f).translationY(0f).interpolator = DecelerateInterpolator(1.4f)
            } else {
                mContentViewBinding.nextBtn.animate().alpha(0f).translationY(mContentViewBinding.nextBtn.height.toFloat()).interpolator = AccelerateInterpolator(1.4f)
            }
        })
    }

    private fun onFailureResponse(checkoutAddressInfoResponseModel: CheckoutAddressInfoResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
                (context as CheckoutActivity),
                getString(R.string.error),
                checkoutAddressInfoResponseModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    when (checkoutAddressInfoResponseModel.otherError) {
                        ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                            Utils.logoutAndGoToHome(requireContext())
                        }
                        else -> callApi()
                    }
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            (context as CheckoutActivity).finish()
        })
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                (context as CheckoutActivity),
                getString(R.string.error),
                NetworkHelper.getErrorMessage((context as CheckoutActivity), error),
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            (context as CheckoutActivity).finish()
        })
    }

    private fun getShippingMethods() {
        (context as CheckoutActivity).mContentViewBinding.loading = true
        ApiConnection.getShippingMethods(context as CheckoutActivity, mContentViewBinding.data!!.getNewAddressData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ShippingMethodsModel>(context as CheckoutActivity, true) {
                    override fun onNext(shippingMethodsModel: ShippingMethodsModel) {
                        super.onNext(shippingMethodsModel)
                        (context as CheckoutActivity).mContentViewBinding.loading = false
                        if (shippingMethodsModel.success) {
                            onGetShippingMethodsSuccessfulResponse(shippingMethodsModel)
                        } else {
                            onGetShippingMethodsFailureResponse(shippingMethodsModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        (context as CheckoutActivity).mContentViewBinding.loading = false
                        onGetShippingMethodsErrorResponse(e)
                    }
                })
    }

    private fun onGetShippingMethodsSuccessfulResponse(shippingMethodsModel: ShippingMethodsModel) {
        mContentViewBinding.shippingData = shippingMethodsModel
        setupShippingMethods()
    }

    private fun setupShippingMethods() {
        mContentViewBinding.shippingMethodRg.removeAllViews()
        mShippingMethodsList.clear()
        var hasSelectedRb = false
        mContentViewBinding.shippingData!!.shippingMethods.forEach { eachShippingMethod ->
            val titleTv = TextView(context)
            titleTv.text = eachShippingMethod.title
            titleTv.setPadding(0, 20, 0, 0)
            titleTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_400))
            titleTv.typeface = TypefaceUtils.load(requireActivity().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
            titleTv.textSize = 14f
            titleTv.textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            llp.setMargins(15, 40, 0, 0)
            titleTv.layoutParams = llp

            mContentViewBinding.shippingMethodRg.addView(titleTv)
            for (methodOfDifferentShippingMethodIdx in 0 until eachShippingMethod.method.size) {

                val methodBinding = DataBindingUtil.inflate<ItemCheckoutShippingMethodBinding>(LayoutInflater.from(context), R.layout.item_checkout_shipping_method, mContentViewBinding.shippingMethodRg, false)
                methodBinding.data = eachShippingMethod.method[methodOfDifferentShippingMethodIdx]
                methodBinding.root.tag = eachShippingMethod.method[methodOfDifferentShippingMethodIdx].code
                methodBinding.shippingMethodRb.tag = eachShippingMethod.method[methodOfDifferentShippingMethodIdx].code
                methodBinding.root.setOnClickListener(shippingMethodClickListener)
                methodBinding.shippingMethodRb.setOnClickListener(shippingMethodClickListener)

                mShippingMethodsList.add(methodBinding.root)
                mContentViewBinding.shippingMethodRg.addView(methodBinding.root)

                if (!eachShippingMethod.method[methodOfDifferentShippingMethodIdx].error.isEmpty()) {
                    methodBinding.mainLayout.isEnabled = false
                    methodBinding.shippingMethodRb.isEnabled = false
                    val errorTv = TextView(context)
                    errorTv.text = eachShippingMethod.method[methodOfDifferentShippingMethodIdx].error
                    errorTv.textSize = 12f
                    errorTv.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    mContentViewBinding.shippingMethodRg.addView(errorTv)
                }

                if (!mContentViewBinding.data!!.selectedShippingMethod.isNullOrEmpty() && methodBinding.shippingMethodRb.tag == mContentViewBinding.data!!.selectedShippingMethod) {
                    hasSelectedRb = true
                    methodBinding.shippingMethodRb.isChecked = true
                }
            }
        }

        if (!hasSelectedRb) {
            mContentViewBinding.data!!.selectedShippingMethod = ""
        }
    }

    private var shippingMethodClickListener: View.OnClickListener = View.OnClickListener { v ->
        for (view in mShippingMethodsList) {
            if (view.tag == v.tag) {
                (view.findViewById(R.id.shipping_method_rb) as RadioButton).isChecked = true
                mContentViewBinding.data!!.selectedShippingMethod = v.tag.toString()
            } else {
                (view.findViewById(R.id.shipping_method_rb) as RadioButton).isChecked = false
            }
        }
    }

    private fun onGetShippingMethodsFailureResponse(shippingMethodsModel: ShippingMethodsModel) {
        AlertDialogHelper.showNewCustomDialog(
                (context as CheckoutActivity),
                getString(R.string.error),
                shippingMethodsModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    getShippingMethods()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            if (mContentViewBinding.shippingData != null) {
                mContentViewBinding.shippingData!!.shippingMethods = ArrayList()
                mContentViewBinding.shippingData = mContentViewBinding.shippingData
                setupShippingMethods()
            }
        })
    }

    private fun onGetShippingMethodsErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                (context as CheckoutActivity),
                getString(R.string.error),
                NetworkHelper.getErrorMessage((context as CheckoutActivity), error),
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    getShippingMethods()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            (context as CheckoutActivity).finish()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            if (requestCode == RC_ADD_EDIT_ADDRESS) {
                if (data.hasExtra(BUNDLE_KEY_ADDRESS_DATA)) {

                    mContentViewBinding.data!!.newAddressData = data.getParcelableExtra(BUNDLE_KEY_ADDRESS_DATA)!!

                    mContentViewBinding.data!!.newAddressData?.let { AppSharedPref.setCustomerCachedNewAddress(requireContext(), it) }

                    if (!mContentViewBinding.data!!.addressData.isNullOrEmpty() && mContentViewBinding.data!!.addressData?.get((mContentViewBinding.data!!.addressData?.size
                                    ?: 0) - 1)?.id == "new") {
                        mContentViewBinding.data!!.addressData?.get(mContentViewBinding.data!!.addressData!!.size - 1)?.value = mContentViewBinding.data!!.newAddressData?.getFormattedAddress()
                    } else {
                        val newBillingShippingAddress = BillingShippingAddress()
                        newBillingShippingAddress.id = "new"
                        newBillingShippingAddress.value = mContentViewBinding.data!!.newAddressData?.getFormattedAddress()
                        mContentViewBinding.data!!.addressData?.add(newBillingShippingAddress)

                        mContentViewBinding.data!!.hasNewAddress = true
                    }

                    mContentViewBinding.data!!.addressData?.get(mContentViewBinding.data!!.addressData!!.size - 1)?.let { setSelectedAddressData(it) }
                } else if (data.hasExtra(BundleKeysHelper.BUNDLE_KEY_IS_GUEST_USER)) {
                    (activity as CheckoutActivity).finish()
                } else {
                    callApi()
                }
            }
        }
    }

    fun setSelectedAddressData(billingAddress: BillingShippingAddress) {
        mContentViewBinding.data!!.selectedAddressData = billingAddress
        mContentViewBinding.data = mContentViewBinding.data!!
        getShippingMethods()
    }
}