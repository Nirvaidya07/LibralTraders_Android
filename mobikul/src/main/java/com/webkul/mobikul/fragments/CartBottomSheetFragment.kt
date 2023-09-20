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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.adapters.CartItemsRvAdapter
import com.webkul.mobikul.adapters.PriceDetailsRvAdapter
import com.webkul.mobikul.adapters.ProductCarouselHorizontalRvAdapter
import com.webkul.mobikul.databinding.FragmentCartBottomSheetBinding
import com.webkul.mobikul.handlers.CartBottomSheetHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.catalog.CartDetailsResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class CartBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    lateinit var mContentViewBinding: FragmentCartBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

    fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getCartDetails" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(requireContext()) + AppSharedPref.getQuoteId(requireContext()) + AppSharedPref.getCurrencyCode(requireContext()))
        ApiConnection.getCartDetails(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CartDetailsResponseModel>(requireContext(), true) {
                    override fun onNext(cartDetailsResponseModel: CartDetailsResponseModel) {
                        super.onNext(cartDetailsResponseModel)
                        mContentViewBinding.loading = false
                        if (cartDetailsResponseModel.success) {
                            onSuccessfulResponse(cartDetailsResponseModel)
                        } else {
                            onFailureResponse(cartDetailsResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
        mContentViewBinding.handler = CartBottomSheetHandler(this)
    }

    private fun onSuccessfulResponse(cartDetailsResponseModel: CartDetailsResponseModel) {
        if (context != null) {
            mContentViewBinding.data = cartDetailsResponseModel

            (context as BaseActivity).updateCartCount(mContentViewBinding.data!!.cartCount)

            if (mContentViewBinding.data!!.items.isEmpty()) {
                val fragmentTransaction = childFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.cart_heading, EmptyFragment.newInstance("empty_cart.json", getString(R.string.empty_cart), getString(R.string.add_item_to_your_cart_now), false), EmptyFragment::class.java.simpleName)
                fragmentTransaction.commitAllowingStateLoss()
            } else {
                checkForErrors()
                setupCartItems()
                setupCrossSellProductsRv()
                setupPriceDetailsItems()
                setupProceedBtnHideShow()
            }
        }
    }

    private fun checkForErrors() {
        if (mContentViewBinding.data!!.descriptionMessage.isNullOrBlank()) {
            if (mContentViewBinding.data!!.minimumAmount > 0)
                mContentViewBinding.data!!.error = getString(R.string.min_order_amt_error) + " " + mContentViewBinding.data!!.minimumFormattedAmount
        } else {
            mContentViewBinding.data!!.error = mContentViewBinding.data!!.descriptionMessage!!
        }
    }

    private fun setupCartItems() {
        mContentViewBinding.cartItemsRv.isNestedScrollingEnabled = false
        mContentViewBinding.cartItemsRv.adapter = CartItemsRvAdapter(this, mContentViewBinding.data!!.items, mContentViewBinding.data!!.showThreshold)
    }

    private fun setupCrossSellProductsRv() {
        if (mContentViewBinding.crossSellProductsRv.adapter == null) {
            mContentViewBinding.crossSellProductsRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
            mContentViewBinding.crossSellProductsRv.isNestedScrollingEnabled = false
        }
        mContentViewBinding.crossSellProductsRv.adapter = ProductCarouselHorizontalRvAdapter(requireContext(), mContentViewBinding.data!!.crossSellList)
    }

    private fun setupPriceDetailsItems() {
        mContentViewBinding.priceDetailsRv.isNestedScrollingEnabled = false
        mContentViewBinding.priceDetailsRv.adapter = PriceDetailsRvAdapter(requireContext(), mContentViewBinding.data!!.totalsData)
    }

    private fun setupProceedBtnHideShow() {
        mContentViewBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY - oldScrollY < 0 || scrollY > mContentViewBinding.scrollView.getChildAt(0).height - mContentViewBinding.scrollView.height - 100) {
                if (mContentViewBinding.data!!.isCheckoutAllowed)
                    mContentViewBinding.proceedToCheckoutBtn.animate().alpha(1.0f).translationY(0f).interpolator = DecelerateInterpolator(1.4f)
            } else {
                mContentViewBinding.proceedToCheckoutBtn.animate().alpha(0f).translationY(mContentViewBinding.proceedToCheckoutBtn.height.toFloat()).interpolator = AccelerateInterpolator(1.4f)
            }
        })
    }

    fun onFailureResponse(response: Any) {
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                AlertDialogHelper.showNewCustomDialog(
                        context as BaseActivity,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            Utils.logoutAndGoToHome(requireContext())
                        }, "", null)
            }
            else -> {
                AlertDialogHelper.showNewCustomDialog(
                        context as BaseActivity,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            dismiss()
                        }, "", null)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {

        if ((!NetworkHelper.isNetworkAvailable(requireContext()) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    context as BaseActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(requireContext(), error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                dismiss()
            })
        }
    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread((context as BaseActivity).mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(BaseActivity.mObjectMapper.readValue(response, CartDetailsResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    context as BaseActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(context as BaseActivity, error),
                                    false,
                                    getString(R.string.try_again),
                                    { dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                        callApi()
                                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                                dismiss()
                            })
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        (context as BaseActivity).mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == ConstantsHelper.RC_LOGIN || requestCode == ConstantsHelper.RC_SIGN_UP) {
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                intent.putExtra(BundleKeysHelper.BUNDLE_KEY_IS_VIRTUAL_CART, mContentViewBinding.data!!.isVirtual)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.hideKeyboard(mContentViewBinding.scrollView)
    }
}