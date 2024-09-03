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

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.MyOrdersActivity
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.adapters.ProductCreateReviewRatingDataRvAdapter
import com.libraltraders.android.databinding.FragmentProductCreateReviewBottomSheetBinding
import com.webkul.mobikul.activities.ProductDetailsActivity
import com.webkul.mobikul.handlers.ProductCreateReviewBottomSheetFragmentHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_KEYBOARD_OBSERVER
import com.webkul.mobikul.models.product.CreateReviewData
import com.webkul.mobikul.models.product.ProductRatingFormDataModel
import com.webkul.mobikul.models.product.RatingFormData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ProductCreateReviewBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    companion object {
        fun newInstance(id: String?, image: String?, name: String?): ProductCreateReviewBottomSheetFragment {
            val productCreateReviewBottomSheetFragment = ProductCreateReviewBottomSheetFragment()
            val args = Bundle()
            args.putString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID, id)
            args.putString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE, image)
            args.putString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME, name)
            productCreateReviewBottomSheetFragment.arguments = args
            return productCreateReviewBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentProductCreateReviewBottomSheetBinding
    var mRatingFormData: ArrayList<RatingFormData> = ArrayList()
    private var mProductId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_create_review_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        callApi()

        mContentViewBinding.commentEt.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (mContentViewBinding.commentEt.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun getArgumentData() {
        mProductId = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID)?:""
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = Utils.getMd5String("getRatingForData" + AppSharedPref.getStoreId(requireContext()))
        ApiConnection.getRatingForData(requireContext(), BaseActivity.mDataBaseHandler.getETagFromDatabase((context as BaseActivity).mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ProductRatingFormDataModel>(requireContext(), true) {
                    override fun onNext(ratingFormDataResponseModel: ProductRatingFormDataModel) {
                        super.onNext(ratingFormDataResponseModel)
                        mContentViewBinding.loading = false
                        if (ratingFormDataResponseModel.success) {
                            onSuccessfulResponse(ratingFormDataResponseModel)
                        } else {
                            onFailureResponse(ratingFormDataResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(ratingFormDataResponseModel: ProductRatingFormDataModel) {
        mRatingFormData = ratingFormDataResponseModel.ratingFormData
        setupViewData()
        setupRatingRv()
    }

    private fun setupViewData() {
        mContentViewBinding.productId = mProductId

        val createReviewData = CreateReviewData()
        createReviewData.productImage = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME)?:""
        createReviewData.productName = requireArguments().getString(BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE)?:""
        createReviewData.nickName = AppSharedPref.getCustomerName(requireContext())
        mContentViewBinding.data = createReviewData
        mContentViewBinding.handler = ProductCreateReviewBottomSheetFragmentHandler(this)

        if (ENABLE_KEYBOARD_OBSERVER)
            attachKeyboardListeners()
    }

    private fun setupRatingRv() {
        mContentViewBinding.ratingDataRv.adapter = ProductCreateReviewRatingDataRvAdapter(requireContext(), mRatingFormData)
        if (mRatingFormData.isEmpty()) {
            mContentViewBinding.ratingDataRv.visibility = View.GONE
        }
    }

    private fun onFailureResponse(ratingFormDataResponseModel: ProductRatingFormDataModel) {
        AlertDialogHelper.showNewCustomDialog(
                (context as BaseActivity),
                getString(R.string.error),
                ratingFormDataResponseModel.message,
                false,
                getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }
                , getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            dismiss()
        })
    }

    private fun onErrorResponse(error: Throwable) {
        AlertDialogHelper.showNewCustomDialog(
                (context as BaseActivity),
                getString(R.string.error),
                NetworkHelper.getErrorMessage((context as BaseActivity), error),
                false,
                getString(R.string.try_again),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }
                , getString(R.string.dismiss)
                , DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            dismiss()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.hideKeyboard(mContentViewBinding.cretaeReviewBottomSheet)

        if (keyboardListenersAttached) {
            mContentViewBinding.cretaeReviewBottomSheet.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        var heightDiff: Int = 0
        if (context is ProductDetailsActivity)
            heightDiff = Utils.screenHeight - (context as ProductDetailsActivity).mContentViewBinding.root.height
        else if (context is OrderDetailsActivity)
            heightDiff = Utils.screenHeight - (context as OrderDetailsActivity).mContentViewBinding.root.height
        else if (context is MyOrdersActivity)
            heightDiff = Utils.screenHeight - (context as MyOrdersActivity).mContentViewBinding.root.height
        if (heightDiff < 200) {
            onHideKeyboard()
        } else {
            onShowKeyboard(heightDiff)
        }
    }

    private var keyboardListenersAttached = false

    protected fun onShowKeyboard(keyboardHeight: Int) {
        val layoutParams = mContentViewBinding.keyboardHeightLayout.layoutParams
        layoutParams.height = keyboardHeight
        mContentViewBinding.keyboardHeightLayout.layoutParams = layoutParams
    }

    protected fun onHideKeyboard() {
        val layoutParams = mContentViewBinding.keyboardHeightLayout.layoutParams
        layoutParams.height = 0
        mContentViewBinding.keyboardHeightLayout.layoutParams = layoutParams
    }

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        mContentViewBinding.cretaeReviewBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }
}