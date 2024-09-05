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

import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.razorpay.Checkout
import com.libraltraders.android.R
import com.webkul.mobikul.activities.AddEditAddressActivity
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.activities.OrderPlacedActivity
import com.webkul.mobikul.activities.WebPaymentActivity
import com.webkul.mobikul.fragments.AddressListFragment
import com.webkul.mobikul.fragments.PaymentInfoFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CANCEL_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_FAILURE_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_SAVE_ORDER_RESPONSE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_START_PAYMENT_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_SUCCESS_URL
import com.webkul.mobikul.helpers.ConstantsHelper.RC_PAYMENT
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.SaveOrderResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


class PaymentInfoFragmentHandler(val mFragmentContext: PaymentInfoFragment) {

    private var mSaveOrderResponseModel: SaveOrderResponseModel = SaveOrderResponseModel()

    fun onClickChangeAddress() {
        val fragmentManager = mFragmentContext.requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        fragmentTransaction.add(android.R.id.content, AddressListFragment.newInstance(mFragmentContext.mContentViewBinding.checkoutAddressData!!))
        fragmentTransaction.addToBackStack(AddressListFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickAddEditNewAddress() {
        val intent = Intent(mFragmentContext.context, AddEditAddressActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_ADDRESS_DATA, mFragmentContext.mContentViewBinding.checkoutAddressData!!.newAddressData)
        mFragmentContext.startActivityForResult(intent, ConstantsHelper.RC_ADD_EDIT_ADDRESS)
    }

    fun onClickPlaceOrderBtn() {
        if (!mFragmentContext.mContentViewBinding.checkoutAddressData!!.sameAsShipping && mFragmentContext.mContentViewBinding.checkoutAddressData!!.selectedAddressData?.id == "0") {
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.payment_address_select_error))
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.addEditAddBtn)
            mFragmentContext.mContentViewBinding.mainScroller.smoothScrollTo(0, 0)
        } else if (mFragmentContext.mContentViewBinding.data!!.selectedPaymentMethod.isBlank()) {
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.payment_method_select_error))
            Utils.showShakeError(mFragmentContext.requireContext(), mFragmentContext.mContentViewBinding.paymentMethodRg)
            val scrollTo = mFragmentContext.mContentViewBinding.mainScroller.top + mFragmentContext.mContentViewBinding.paymentMethodRg.top - 100
            mFragmentContext.mContentViewBinding.mainScroller.smoothScrollTo(0, scrollTo)

        } else if(mFragmentContext.mContentViewBinding.data!!.selectedPaymentMethod== ApplicationConstants.PAYMENT_CODE_RAZORPAY){
            processPayment()
        } else {
            placeOrder()
        }
    }

    var transactionId:String?=null
    var status:Int?=null

    fun placeOrder(){
        (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = true
        ApiConnection.placeOrder(mFragmentContext.context as CheckoutActivity,
                mFragmentContext.mContentViewBinding.data!!.selectedPaymentMethod,
                mFragmentContext.mContentViewBinding.checkoutAddressData!!.getNewAddressData(),
                transactionId,status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<SaveOrderResponseModel>(mFragmentContext.context as CheckoutActivity, true) {
                    override fun onNext(saveOrderResponseModel: SaveOrderResponseModel) {
                        super.onNext(saveOrderResponseModel)
                        if (saveOrderResponseModel.success) {
                            onSuccessfulResponse(saveOrderResponseModel)
                        } else {
                            (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = false
                            onFailureResponse(saveOrderResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        (context as CheckoutActivity).mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(saveOrderResponseModel: SaveOrderResponseModel) {
        mSaveOrderResponseModel = saveOrderResponseModel
        FirebaseAnalyticsHelper.logECommercePurchaseEvent(saveOrderResponseModel.incrementId!!,saveOrderResponseModel.orderId!!)
        AppSharedPref.setQuoteId(mFragmentContext.requireContext(), 0)
        AppSharedPref.setCartCount(mFragmentContext.requireContext(), 0)
        if (mSaveOrderResponseModel.webview) {
            val intent = Intent(mFragmentContext.context, WebPaymentActivity::class.java)
            intent.putExtra(BUNDLE_KEY_START_PAYMENT_URL, mSaveOrderResponseModel.redirectUrl)
            intent.putExtra(BUNDLE_KEY_SUCCESS_URL, mSaveOrderResponseModel.successUrl)
            intent.putExtra(BUNDLE_KEY_CANCEL_URL, mSaveOrderResponseModel.cancelUrl)
            intent.putExtra(BUNDLE_KEY_FAILURE_URL, mSaveOrderResponseModel.failureUrl)
            mFragmentContext.startActivityForResult(intent, RC_PAYMENT)
        } else {
            when (mFragmentContext.mContentViewBinding.data!!.selectedPaymentMethod) {

                else -> onPaymentResponse()
            }
        }
    }

    private fun processPayment() {
        (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = true

        val checkout = Checkout()
        checkout.setKeyID(mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].razorpay_keyId)

        try {
            val options = JSONObject()
            options.put("name", mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].razorpay_merchantName)
//            options.put("description", "Order#" + saveOrderResponseModel.incrementId)
            options.put("order_id", mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].razorpay_order_id)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", mFragmentContext.mContentViewBinding.data?.currencyCode)
            options.put("amount", mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].unformattedAmount?.let { Integer.valueOf(it) })
            options.put("prefill.name", mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].customer_name+" "+mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].customer_lastname)
            options.put("prefill.email", mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].customer_email)
            options.put("prefill.contact",mFragmentContext.mContentViewBinding.data?.razorpay_data!![0].customer_phone_number)
            checkout.open(mFragmentContext.activity as CheckoutActivity, options)
            checkout.setImage(R.mipmap.ic_launcher)

        } catch (e: Exception) {
            (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = false
            e.message?.let { ToastHelper.showToast(mFragmentContext.context!!, it) }
            Log.e("PaymentMehtodHandler", "Error in starting Razorpay Checkout", e)
        }
    }

    fun onPaymentResponse() {
        val intent = Intent(mFragmentContext.context, OrderPlacedActivity::class.java)
        intent.putExtra(BUNDLE_KEY_SAVE_ORDER_RESPONSE, mSaveOrderResponseModel)
        mFragmentContext.startActivity(intent)
    }

    private fun onFailureResponse(saveOrderResponseModel: SaveOrderResponseModel) {
        if (saveOrderResponseModel.cartCount == 0) {
            AlertDialogHelper.showNewCustomDialog(
                    (mFragmentContext.context as CheckoutActivity),
                    mFragmentContext.getString(R.string.error),
                    saveOrderResponseModel.message,
                    false,
                    mFragmentContext.getString(R.string.ok),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        (mFragmentContext.context as CheckoutActivity).finish()
                    })
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    (mFragmentContext.context as CheckoutActivity),
                    mFragmentContext.getString(R.string.error),
                    saveOrderResponseModel.message,
                    false,
                    mFragmentContext.getString(R.string.try_again),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        onClickPlaceOrderBtn()
                    }
                    , mFragmentContext.getString(R.string.dismiss)
                    , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
        }
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                (mFragmentContext.context as CheckoutActivity),
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage((mFragmentContext.context as CheckoutActivity), error),
                false,
                mFragmentContext.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    onClickPlaceOrderBtn()
                }
                , mFragmentContext.getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }


    fun onClickDiscountCodeLabel() {
        if (mFragmentContext.mContentViewBinding.discountCode.visibility == View.VISIBLE) {
            mFragmentContext.mContentViewBinding.discountCode.visibility = View.GONE
            mFragmentContext.mContentViewBinding.discountCodeHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mFragmentContext.requireContext(), R.drawable.ic_down_arrow_grey_wrapper), null)
        } else {
            mFragmentContext.mContentViewBinding.discountCode.visibility = View.VISIBLE
            mFragmentContext.mContentViewBinding.discountCodeHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mFragmentContext.requireContext(), R.drawable.ic_up_arrow_grey_wrapper), null)
           Handler(Looper.getMainLooper()).postDelayed({
                val scrollTo = mFragmentContext.mContentViewBinding.mainScroller.top + mFragmentContext.mContentViewBinding.discountCodeHeading.top
                mFragmentContext.mContentViewBinding.mainScroller.smoothScrollTo(0, scrollTo)
            }, 200)
        }
    }


    fun onClickApplyOrRemoveCouponBtn(couponCode: String, isRemoveCoupon: Boolean) {
        if (!couponCode.isBlank()) {
            Utils.hideKeyboard(mFragmentContext.mContentViewBinding.discountCode)
            (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = true
            ApiConnection.applyOrRemoveCoupon(mFragmentContext.requireContext(), couponCode, isRemoveCoupon)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mFragmentContext.requireContext(), true) {
                        override fun onNext(applyOrRemoveCouponResponse: BaseModel) {
                            super.onNext(applyOrRemoveCouponResponse)
                            (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = false
                            ToastHelper.showToast(mFragmentContext.requireContext(), applyOrRemoveCouponResponse.message)
                            if (applyOrRemoveCouponResponse.success) {
                                mFragmentContext.callApi()
                                mFragmentContext.appliedCoupon = true
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            (mFragmentContext.context as CheckoutActivity).mContentViewBinding.loading = false
                            onErrorResponse(e)
                        }
                    })
        } else {
            Utils.hideKeyboard(mFragmentContext.mContentViewBinding.discountCode)
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.please_enter_coupon_code))
        }
    }

}