package com.webkul.mobikul.handlers

import android.accounts.AccountManager.KEY_ACCOUNT_TYPE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.DeliveryChatActivity
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.fragments.*
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_USER_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_SELLER_ID
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.ToastHelper
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.ReOrderModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup

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

class ItemOrderedFragmentHandler(val mFragmentContext: ItemOrderedFragment, val mIncrementId: String?) {

    fun onClickReorder() {
        mFragmentContext.mContentViewBinding.loading = true
        ApiConnection.reorder(mFragmentContext.requireContext(), mIncrementId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ReOrderModel>(mFragmentContext.requireContext(), true) {
                    override fun onNext(reorderResponseModel: ReOrderModel) {
                        super.onNext(reorderResponseModel)
                        mFragmentContext.mContentViewBinding.loading = false
                        if (reorderResponseModel.success) {
                            (mFragmentContext.activity as BaseActivity).updateCartCount(reorderResponseModel.cartCount)
                            if (reorderResponseModel.quoteId != 0) {
                                mFragmentContext.context?.let { AppSharedPref.setQuoteId(it, reorderResponseModel.quoteId) }
                            }
                            onSuccessfulResponse(reorderResponseModel)
                        } else {
                            onFailureResponse(reorderResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mFragmentContext.mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(reorderResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.reorder),
                reorderResponseModel.message,
                false,
                mFragmentContext.getString(R.string.go_to_cart),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                val cartBottomSheetFragment = CartBottomSheetFragment()
                mFragmentContext.activity?.supportFragmentManager?.let { cartBottomSheetFragment.show(it, CartBottomSheetFragment::class.java.simpleName) }

            }
                , mFragmentContext.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    private fun onFailureResponse(reorderResponseModel: BaseModel) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                reorderResponseModel.message,
                false,
                mFragmentContext.getString(R.string.try_again),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                onClickReorder()
            }
                , mFragmentContext.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                mFragmentContext.context as BaseActivity,
                mFragmentContext.getString(R.string.error),
                NetworkHelper.getErrorMessage(mFragmentContext.requireContext(), error),
                false,
                mFragmentContext.getString(R.string.try_again),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                onClickReorder()
            }
                , mFragmentContext.getString(R.string.dismiss)
                , { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        })
    }

    fun onClickDeliveryBoyImage(imageUrl: String) {
        FullImageDialogFragment.newInstance(imageUrl).show((mFragmentContext.context as BaseActivity).supportFragmentManager, FullImageDialogFragment::class.java.simpleName)
    }

    fun onClickCallBtn(contactNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$contactNumber")
        mFragmentContext.startActivity(intent)
    }

    fun onClickTrackBtn(deliveryBoyId: String, isPicked: Boolean,  deliveryBoyLat: String?, deliveryBoyLong: String?) {
        if (!mFragmentContext.mContentViewBinding.data?.adminAddress.isNullOrEmpty() && !deliveryBoyLat.isNullOrEmpty() && !deliveryBoyLong.isNullOrEmpty() && !mFragmentContext.mContentViewBinding.data?.shippingAddress.isNullOrEmpty()) {
            val fragmentTransaction = (mFragmentContext.context as OrderDetailsActivity).supportFragmentManager.beginTransaction()
            fragmentTransaction.add(android.R.id.content, TrackDeliveryBoyFragment.newInstance(deliveryBoyId, isPicked, Jsoup.parse(mFragmentContext.mContentViewBinding.data!!.adminAddress).text(), deliveryBoyLat, deliveryBoyLong, Jsoup.parse(mFragmentContext.mContentViewBinding.data!!.shippingAddress).text()), TrackDeliveryBoyFragment::class.java.simpleName)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            fragmentTransaction.addToBackStack(TrackDeliveryBoyFragment::class.java.simpleName)
            fragmentTransaction.commit()
        } else {
            ToastHelper.showToast(mFragmentContext.requireContext(), mFragmentContext.getString(R.string.unable_to_track_delivery_boy))
        }
    }


    fun onClickHelpBtn(sellerId:String?) {
        val intent = Intent(mFragmentContext.requireContext(), DeliveryChatActivity::class.java)
        intent.putExtra(KEY_ACCOUNT_TYPE, "customer")
        intent.putExtra(BUNDLE_SELLER_ID, sellerId)
        intent.putExtra(BUNDLE_KEY_USER_ID, AppSharedPref.getCustomerId(mFragmentContext.requireContext()))
        mFragmentContext.requireContext().startActivity(intent)
    }

    fun deliveryBoyMakeReview(deliveryBoyId: String, customerId: String) {
        val deliveryBoyMakeReviewFragment = DeliveryboyMakeReviewFragment.newInstance(deliveryBoyId, customerId)
        mFragmentContext.activity?.supportFragmentManager?.let { deliveryBoyMakeReviewFragment.show(it, DeliveryboyMakeReviewFragment::class.java.simpleName) }
    }
}