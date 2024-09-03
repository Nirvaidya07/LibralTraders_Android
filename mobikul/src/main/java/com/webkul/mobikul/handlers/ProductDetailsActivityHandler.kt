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

import android.Manifest
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.webkul.arcore.activities.ArActivity
import com.webkul.arcore.activities.CameraWithImageActivity
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.CheckoutActivity
import com.webkul.mobikul.adapters.ProductAttributesSwatchRvAdapter
import com.libraltraders.android.databinding.WishListPopupMenuBinding
import com.webkul.mobikul.activities.ProductDetailsActivity
import com.webkul.mobikul.fragments.ProceedCheckoutBottomSheetFragment
import com.webkul.mobikul.fragments.ProductCreateReviewBottomSheetFragment
import com.webkul.mobikul.fragments.ProductReviewListFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.AlertDialogHelper.Companion.showNewCustomDialog
import com.webkul.mobikul.helpers.ToastHelper.Companion.showToast
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.AddToCartResponseModel
import com.webkul.mobikul.models.user.AddToWishListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File


class ProductDetailsActivityHandler(private val mContext: ProductDetailsActivity) {

    private var mIsProcessing: Boolean = false

    private var mProductParamsJSON: JSONObject = JSONObject()
    private val mFiles = ArrayList<MultipartBody.Part>()
    private var mRelatedProductsJSONArray: JSONArray = JSONArray()

    private var mRefreshRate = 200
    private var mProductQty: Int = 0
    private val repeatUpdateHandler = Handler()
    private var mIsIncrement: Boolean = false
    var addToCartResponseModel: AddToCartResponseModel?=null
    private val mAutoChangeQty = object : Runnable {
        override fun run() {
            if (mIsIncrement) {
                mContext.mContentViewBinding.data!!.qty = mProductQty++.toString()
            } else {
                mContext.mContentViewBinding.data!!.qty = mProductQty--.toString()
            }
            repeatUpdateHandler.postDelayed(this, mRefreshRate.toLong())
            if (mRefreshRate > 80)
                mRefreshRate -= 5
        }
    }

    fun onClickReviewCountBtn() {
        mContext.mContentViewBinding.reviews.visibility = View.VISIBLE
        mContext.mContentViewBinding.reviewsHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_up_arrow_grey_wrapper), null)
       Handler(Looper.getMainLooper()).postDelayed({
            val scrollTo = mContext.mContentViewBinding.scrollView.top + mContext.mContentViewBinding.reviewsHeading.top
            mContext.mContentViewBinding.scrollView.smoothScrollTo(0, scrollTo)
        }, 200)
    }

    fun onClickAddYourReviewBtn() {
        if (mContext.mContentViewBinding.data!!.guestCanReview || AppSharedPref.isLoggedIn(mContext)) {
            val productCreateReviewBottomSheetFragment = ProductCreateReviewBottomSheetFragment.newInstance(mContext.mContentViewBinding.data!!.id, mContext.mContentViewBinding.data!!.name, mContext.mContentViewBinding.data!!.thumbNail)
            productCreateReviewBottomSheetFragment.show(mContext.supportFragmentManager, ProductCreateReviewBottomSheetFragment::class.java.simpleName)
        } else {
            showLoginAlertDialog(mContext.getString(R.string.please_login_to_write_reviews))
        }
    }

    fun showLoginAlertDialog(content: String?) {
        showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.login_required),
                content,
                true,
                mContext.getString(R.string.login),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mContext.startActivity((mContext.application as MobikulApplication).getLoginAndSignUpActivity(mContext))
                },
                mContext.getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    fun onClickNotifyPriceDropBtn() {
        if (AppSharedPref.isLoggedIn(mContext)) {
            mContext.mContentViewBinding.loading = true
            ApiConnection.addPriceAlert(mContext, mContext.mContentViewBinding.data!!.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                        override fun onNext(addPriceAlertResponse: BaseModel) {
                            super.onNext(addPriceAlertResponse)
                            mContext.mContentViewBinding.loading = false
                            showToast(mContext, addPriceAlertResponse.message)
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContext.mContentViewBinding.loading = false
                            showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                        }
                    })
        } else {
            showLoginAlertDialog(mContext.getString(R.string.login_to_subscribe))
        }
    }

    fun onClickNotifyProductInStockBtn() {
        if (AppSharedPref.isLoggedIn(mContext)) {
            mContext.mContentViewBinding.loading = true
            ApiConnection.addStockAlert(mContext, mContext.mContentViewBinding.data!!.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                        override fun onNext(addStockAlertResponse: BaseModel) {
                            super.onNext(addStockAlertResponse)
                            mContext.mContentViewBinding.loading = false
                            showToast(mContext, addStockAlertResponse.message)
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContext.mContentViewBinding.loading = false
                            showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                        }
                    })
        } else {
            showLoginAlertDialog(mContext.getString(R.string.login_to_subscribe))
        }
    }

    fun onClickAddToWishListBtn(view: View) {
        if (AppSharedPref.isLoggedIn(mContext)) {
            if (!mIsProcessing) {
                if (mContext.mContentViewBinding.data!!.isInWishList) {
                    val layout = mContext.layoutInflater.inflate(R.layout.wish_list_popup_menu, null)
                    layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    val mWishListPopupWindow = PopupWindow(layout, 350, layout.measuredHeight + 10, true)
                    mWishListPopupWindow.isOutsideTouchable = true
                    mWishListPopupWindow.isFocusable = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mWishListPopupWindow.elevation = 10f
                    }
                    mWishListPopupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                    mWishListPopupWindow.showAsDropDown(view, 0, 5)

                    val mBinding: WishListPopupMenuBinding? = DataBindingUtil.bind(layout)
                    mBinding?.add?.setOnClickListener {
                        addItemToWishList()
                        mWishListPopupWindow.dismiss()
                    }
                    mBinding?.remove?.setOnClickListener {
                        removeItemFromWishList()
                        mWishListPopupWindow.dismiss()
                    }
                } else {
                    addItemToWishList()
                }
            }
        } else {
            showLoginAlertDialog(mContext.getString(R.string.login_first))
        }
    }

    private fun removeItemFromWishList() {
        showNewCustomDialog(
                mContext as BaseActivity,
                mContext.getString(R.string.remove_item),
                mContext.getString(R.string.remove_wish_list_item_msg),
                false,
                mContext.getString(R.string.yes_remove_it),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mIsProcessing = true
                    mContext.mContentViewBinding.wishlistAnimationView.speed = -1f
                    mContext.mContentViewBinding.wishlistAnimationView.playAnimation()
                    ApiConnection.removeFromWishList(mContext, mContext.mContentViewBinding.data!!.wishListItemId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : ApiCustomCallback<BaseModel>(mContext, false) {
                                override fun onNext(removeFromWishList: BaseModel) {
                                    super.onNext(removeFromWishList)
                                    mIsProcessing = false
                                    if (removeFromWishList.success) {
                                        mContext.mContentViewBinding.data!!.isInWishList = false
                                        mContext.mContentViewBinding.data!!.wishListItemId = ""
                                        mDataBaseHandler.updateRecentlyViewedProduct(AppSharedPref.getStoreId(mContext), AppSharedPref.getCurrencyCode(mContext), mContext.mContentViewBinding.data!!.id, ObjectMapper().writeValueAsString(mContext.mContentViewBinding.data))
                                        showToast(mContext,removeFromWishList.message,Toast.LENGTH_LONG)
                                    } else {
                                        mContext.mContentViewBinding.wishlistAnimationView.cancelAnimation()
                                        mContext.mContentViewBinding.wishlistAnimationView.progress = 1f
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    mIsProcessing = false
                                    mContext.mContentViewBinding.wishlistAnimationView.cancelAnimation()
                                    mContext.mContentViewBinding.wishlistAnimationView.progress = 1f
                                }
                            })
                },
                mContext.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
    }

    private fun addItemToWishList() {
        collectAllOptionData(false)
        mIsProcessing = true
        mContext.mContentViewBinding.data!!.isInWishList = true
        mContext.mContentViewBinding.wishlistAnimationView.speed = 1f
        mContext.mContentViewBinding.wishlistAnimationView.playAnimation()
        ApiConnection.addToWishList(mContext, mContext.mContentViewBinding.data!!.id, mProductParamsJSON, mContext.mContentViewBinding.data!!.qty, mFiles)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AddToWishListResponseModel>(mContext, false) {
                    override fun onNext(addItemToWishList: AddToWishListResponseModel) {
                        super.onNext(addItemToWishList)
                        mIsProcessing = false
                        if (addItemToWishList.success) {
                            FirebaseAnalyticsHelper.logAddToWishListEvent(mContext.mContentViewBinding.data!!.id, mContext.mContentViewBinding.data!!.name, mContext.mContentViewBinding.data!!.qty,mProductParamsJSON)
                            mContext.mContentViewBinding.data!!.isInWishList = true
                            mContext.mContentViewBinding.data!!.wishListItemId = addItemToWishList.itemId
                            showToast(mContext,addItemToWishList.message,Toast.LENGTH_LONG)
                            mDataBaseHandler.updateRecentlyViewedProduct(AppSharedPref.getStoreId(mContext), AppSharedPref.getCurrencyCode(mContext), mContext.mContentViewBinding.data!!.id, ObjectMapper().writeValueAsString(mContext.mContentViewBinding.data))
                        } else {

                            mContext.mContentViewBinding.wishlistAnimationView.cancelAnimation()
                            mContext.mContentViewBinding.wishlistAnimationView.progress = 0f
                            mContext.mContentViewBinding.data!!.isInWishList = false
                            when (addItemToWishList.otherError) {
                                ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                                    showNewCustomDialog(
                                            mContext,
                                            mContext.getString(R.string.error),
                                            addItemToWishList.message,
                                            false,
                                            mContext.getString(R.string.ok),
                                            DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                                                dialogInterface.dismiss()
                                                Utils.logoutAndGoToHome(mContext)
                                            }
                                            , ""
                                            , null)
                                }
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mIsProcessing = false
                        mContext.mContentViewBinding.wishlistAnimationView.cancelAnimation()
                        mContext.mContentViewBinding.wishlistAnimationView.progress = 0f
                    }
                })
    }

    private fun collectAllOptionData(isChecksEnabled: Boolean): Boolean {
        mProductParamsJSON = JSONObject()
        var isAllRequiredOptionFilled = true

        if (mContext.mContentViewBinding.data!!.isAvailable) {
            when (mContext.mContentViewBinding.data!!.typeId) {
                "grouped" -> try {
                    val groupOptionReqJSON = JSONObject()
                    var isQtyOfAnyProductSet = false
                    for (noOfGroupedData in 0 until mContext.mContentViewBinding.data!!.groupedData.size) {
                        val qtyEt = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>(noOfGroupedData).findViewById<AppCompatTextView>(R.id.qty)
                        groupOptionReqJSON.put(mContext.mContentViewBinding.data!!.groupedData[noOfGroupedData].id, qtyEt.text.toString())
                        if (Integer.parseInt(qtyEt.text.toString()) > 0) {
                            isQtyOfAnyProductSet = true
                        }
                    }

                    if (isChecksEnabled && !isQtyOfAnyProductSet) {
                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.bundle_product_specify_qty))
                        isAllRequiredOptionFilled = false
                    }
                    mProductParamsJSON.put("super_group", groupOptionReqJSON)
                } catch (e: Exception) {
                    isAllRequiredOptionFilled = false
                    showToast(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    e.printStackTrace()
                }

                "bundle" -> try {
                    if (mContext.mContentViewBinding.data!!.bundleOptions.toString() != "[]" && mContext.mContentViewBinding.data!!.isAvailable) {
                        val bundleOptionsItemObj = JSONObject()
                        val bundleOptionsQtyObj = JSONObject()

                        for (noOfBundleOpt in 0 until mContext.mContentViewBinding.data!!.bundleOptions.size) {
                            if (!isAllRequiredOptionFilled) {
                                break
                            }
                            when (mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].type) {
                                "select" -> {
                                    val dropdownBundleOptionSpinner = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("bundleoption$noOfBundleOpt").findViewById<AppCompatSpinner>(R.id.dropdown_bundle_option_spinner)
                                    val qtyDropdownET = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("bundleoption$noOfBundleOpt").findViewById<AppCompatTextView>(R.id.qty_et)
                                    val selectedItemPosition = dropdownBundleOptionSpinner.selectedItemPosition
                                    if (!isChecksEnabled || selectedItemPosition != 0) {
                                        bundleOptionsItemObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId,
                                                mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionValues[Integer.parseInt(dropdownBundleOptionSpinner.selectedView.tag.toString())].optionValueId)
                                        bundleOptionsQtyObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId,
                                                qtyDropdownET.text.toString())
                                    } else {
                                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                                + mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].title
                                                + " " + mContext.getString(R.string._is_not_complete_))
                                        isAllRequiredOptionFilled = false
                                    }
                                }
                                "radio" -> {
                                    val bundleOptionRg = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("bundleoption$noOfBundleOpt").findViewById<RadioGroup>(R.id.bundle_option_rg)
                                    val qtyRadioET = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("bundleoption$noOfBundleOpt").findViewById<AppCompatTextView>(R.id.qty_et)
                                    try {
                                        if (mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].required == 1) {
                                            if (!isChecksEnabled || bundleOptionRg.checkedRadioButtonId != -1) {
                                                val selectedRadioBtn = mContext.mContentViewBinding.root.findViewById<RadioButton>(bundleOptionRg.checkedRadioButtonId)
                                                bundleOptionsItemObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId,
                                                        mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionValues[Integer.parseInt(selectedRadioBtn.tag.toString())].optionValueId)
                                                bundleOptionsQtyObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId, qtyRadioET.text.toString())
                                            } else {
                                                showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                                        + mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].title
                                                        + " " + mContext.getString(R.string._is_not_complete_))
                                                isAllRequiredOptionFilled = false
                                            }

                                        } else {
                                            if (bundleOptionRg.checkedRadioButtonId != -1) {
                                                val selectedRadioBtn = mContext.mContentViewBinding.root.findViewById<RadioButton>(bundleOptionRg.checkedRadioButtonId)
                                                if (!selectedRadioBtn.tag.toString().isEmpty()) {
                                                    bundleOptionsItemObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId,
                                                            mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionValues[Integer.parseInt(selectedRadioBtn.tag.toString())].optionValueId)
                                                    bundleOptionsQtyObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId, qtyRadioET.text.toString())
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                                "checkbox", "multi" -> {
                                    var noOfSelectedCheckbox = 0
                                    val selectedOptionValuesArr = JSONArray()
                                    for (noOfOptions in 0 until mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionValues.size) {
                                        val eachCheckTypeBundleOptionValue = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("bundleoption$noOfBundleOpt").findViewWithTag<CheckBox>(noOfOptions)
                                        if (eachCheckTypeBundleOptionValue.isChecked) {
                                            selectedOptionValuesArr.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionValues[Integer.parseInt(eachCheckTypeBundleOptionValue.tag.toString())].optionValueId)
                                            noOfSelectedCheckbox++
                                        }
                                    }

                                    bundleOptionsItemObj.put(mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].optionId, selectedOptionValuesArr)

                                    if (isChecksEnabled && noOfSelectedCheckbox == 0) {
                                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                                + mContext.mContentViewBinding.data!!.bundleOptions[noOfBundleOpt].title
                                                + " " + mContext.getString(R.string._is_not_complete_))
                                        isAllRequiredOptionFilled = false
                                    }
                                }
                            }
                        }
                        mProductParamsJSON.put("bundle_option", bundleOptionsItemObj)
                        mProductParamsJSON.put("bundle_option_qty", bundleOptionsQtyObj)
                    }
                } catch (e: Exception) {
                    isAllRequiredOptionFilled = false
                    showToast(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    e.printStackTrace()
                }

                "downloadable" -> try {
                    var noOfLinksChecked = 0
                    val linksPurchasedSeparatelyIds = JSONObject()

                    if (mContext.mContentViewBinding.data!!.links.linksPurchasedSeparately == 1) {
                        for (i in 0 until mContext.mContentViewBinding.data!!.links.linkData.size) {
                            /* i+1 as it contain a extra text box for label*/
                            if ((mContext.mContentViewBinding.otherProductOptionsContainer.getChildAt(i + 1) as CheckBox).isChecked) {
                                linksPurchasedSeparatelyIds.put(noOfLinksChecked++.toString(), mContext.mContentViewBinding.data!!.links.linkData[i].id)
                            }
                        }
                    }

                    if (isChecksEnabled && noOfLinksChecked == 0 && mContext.mContentViewBinding.data!!.links.linksPurchasedSeparately == 1
                            && mContext.mContentViewBinding.data!!.links.linkData.size != 0) {
                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                + mContext.mContentViewBinding.data!!.links.title
                                + " " + mContext.getString(R.string._is_not_complete_))
                        isAllRequiredOptionFilled = false
                    }
                    if (linksPurchasedSeparatelyIds.length() > 0)
                        mProductParamsJSON.put("links", linksPurchasedSeparatelyIds)
                } catch (e: Exception) {
                    isAllRequiredOptionFilled = false
                    showToast(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    e.printStackTrace()
                }

                "configurable" -> try {
                    val configurableOptnObj = JSONObject()

                    var isSpinnerOpen = false
                    for (attributePosition in 0 until (mContext.mContentViewBinding.data!!.configurableData.attributes?.size?:0)) {
                        if (mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.swatchType == "visual" || mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.swatchType == "text") {
                            val eachSwatchRecyclerView = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<RecyclerView>(R.id.configurable_item_rv)
                            val recyclerViewOpions = (eachSwatchRecyclerView.adapter as ProductAttributesSwatchRvAdapter).getSwatchValuesData()

                            var selectedItemId = ""
                            for (swatch in recyclerViewOpions) {
                                if (swatch.isSelected) {
                                    selectedItemId = swatch.id
                                    break
                                }
                            }
                            if (isChecksEnabled && selectedItemId.isEmpty()) {
                                val errorTextView = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<AppCompatTextView>(R.id.configurable_attribute_error)
                                errorTextView.visibility = View.VISIBLE
                                errorTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake_error))
                                mContext.mContentViewBinding.scrollView.scrollTo(0, mContext.mContentViewBinding.otherProductOptionsContainer.top - 20)
                                isAllRequiredOptionFilled = false
                            } else {
                                mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<AppCompatTextView>(R.id.configurable_attribute_error).visibility = View.GONE
                                configurableOptnObj.put(mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.id, selectedItemId)
                            }
                        } else {
                            val eachConfigurableAttributeSpinner = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<AppCompatSpinner>(R.id.spinner_configurable_item)
                            val selectedItemOfEacchConfigurableProductAttributeSpinner = eachConfigurableAttributeSpinner.selectedItemPosition
                            val actualPositionOfEachConfigurableProductAttributeSpinner = eachConfigurableAttributeSpinner.getItemAtPosition(selectedItemOfEacchConfigurableProductAttributeSpinner) as String

                            eachConfigurableAttributeSpinner.isFocusable = true
                            eachConfigurableAttributeSpinner.isFocusableInTouchMode = true
                            if (isChecksEnabled && actualPositionOfEachConfigurableProductAttributeSpinner.isEmpty()) {
                                val errorTextView = mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<AppCompatTextView>(R.id.configurable_attribute_error)
                                errorTextView.visibility = View.VISIBLE
                                errorTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake_error))
                                mContext.mContentViewBinding.scrollView.scrollTo(0, mContext.mContentViewBinding.otherProductOptionsContainer.top - 20)
                                isAllRequiredOptionFilled = false
                                if (!isSpinnerOpen) {
                                    Utils.setSpinnerError(eachConfigurableAttributeSpinner, "")
                                    isSpinnerOpen = true
                                }
                            } else {
                                mContext.mContentViewBinding.otherProductOptionsContainer.findViewWithTag<LinearLayoutCompat>("config$attributePosition").findViewById<AppCompatTextView>(R.id.configurable_attribute_error).visibility = View.GONE
                                if (actualPositionOfEachConfigurableProductAttributeSpinner.isEmpty())
                                    configurableOptnObj.put(mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.id, "")
                                else {
                                    val attributeOptionValueId = mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.options?.get(Integer.parseInt(actualPositionOfEachConfigurableProductAttributeSpinner))?.id
                                    configurableOptnObj.put(mContext.mContentViewBinding.data!!.configurableData.attributes?.get(attributePosition)?.id, attributeOptionValueId)
                                }
                            }
                        }
                    }
                    mProductParamsJSON.put("super_attribute", configurableOptnObj)
                } catch (e: Exception) {
                    isAllRequiredOptionFilled = false
                    showToast(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    e.printStackTrace()
                }
            }

            try {
                if (isAllRequiredOptionFilled && mContext.mContentViewBinding.data!!.customOptions.size != 0) {
                    val customOptnObj = JSONObject()
                    for (noOfCustomOpt in 0 until mContext.mContentViewBinding.data!!.customOptions.size) {
                        when (mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].type) {
                            "field", "area" -> {
                                val fieldTypeCustomOption = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<AppCompatEditText>(noOfCustomOpt)
                                customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id, fieldTypeCustomOption.text.toString())

                                if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1 && fieldTypeCustomOption.text.toString().isEmpty()) {
                                    showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                            + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                            + " " + mContext.getString(R.string._is_not_complete_))
                                    isAllRequiredOptionFilled = false
                                }
                            }
                            "file" -> {
                                val fileNameTv = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<AppCompatTextView>("fileName$noOfCustomOpt")
                                if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1 && fileNameTv.text.toString() == mContext.getString(R.string.no_file_selected)) {
                                    showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                            + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                            + " " + mContext.getString(R.string._is_not_complete_))
                                    isAllRequiredOptionFilled = false
                                } else {
                                    try {
                                        val filePart = prepareFilePart("options_" + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id + "_file", mContext.mSelectedImageUri[noOfCustomOpt]!!)
                                        if (filePart != null) {
                                            mFiles.add(filePart)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            }

                            "drop_down" -> {
                                //if fist element(none 0) is selected then -1 is send.
                                val custonOptionDropDownSpinner = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<AppCompatSpinner>(noOfCustomOpt)
                                val positionOfSpinnerItem = custonOptionDropDownSpinner.selectedItemPosition

                                if (positionOfSpinnerItem != 0) {
                                    customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfSpinnerItem - 1].optionId,
                                            mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].optionValues!![positionOfSpinnerItem - 1].optionTypeId)
                                } else {
                                    if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1) {
                                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                                + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                                + " " + mContext.getString(R.string._is_not_complete_))
                                        isAllRequiredOptionFilled = false
                                    }
                                }
                            }

                            /*TODOTODO:: put the check in case radio group has none option*/
                            "radio" -> {
                                val customoptionRG = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<RadioGroup>(noOfCustomOpt)
                                val selectedRadioBtn = mContext.mContentViewBinding.root.findViewById<RadioButton>(customoptionRG.checkedRadioButtonId)
                                val indexOfSelectedOptionValue = customoptionRG.indexOfChild(selectedRadioBtn)
                                //return negative if nothing is selected
                                if (indexOfSelectedOptionValue != -1) {
                                    customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id, selectedRadioBtn.tag.toString())
                                } else {
                                    if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1) {
                                        showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                                + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                                + " " + mContext.getString(R.string._is_not_complete_))
                                        isAllRequiredOptionFilled = false
                                    }
                                }
                            }

                            "checkbox", "multiple" -> {

                                var noOfCheckBoxChecked = 0
                                val selectedCheckBoxCustomOptionJSON = JSONArray()
                                val checkBoxCustomOptionLayout = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(noOfCustomOpt)
                                for (childPosition in 0 until checkBoxCustomOptionLayout.childCount) {
                                    val customOptionValueCheck = checkBoxCustomOptionLayout.getChildAt(childPosition) as CheckBox
                                    if (customOptionValueCheck.isChecked) {
                                        noOfCheckBoxChecked++
                                        break
                                    }

                                }

                                if (isChecksEnabled && noOfCheckBoxChecked == 0 && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1) {
                                    showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                            + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                            + " " + mContext.getString(R.string._is_not_complete_))
                                    isAllRequiredOptionFilled = false
                                } else {
                                    for (childPoistion in 0 until checkBoxCustomOptionLayout.childCount) {
                                        val customOptionValueCheck = checkBoxCustomOptionLayout.getChildAt(childPoistion) as CheckBox
                                        if (customOptionValueCheck.isChecked) {
                                            selectedCheckBoxCustomOptionJSON.put(customOptionValueCheck.tag.toString())
                                        }
                                    }
                                    customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt]
                                            .option_id, selectedCheckBoxCustomOptionJSON)
                                }
                            }

                            "date", "time" -> {
                                val customOptionDateView = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>(noOfCustomOpt)

                                val dateOrTimeET: AppCompatTextView
                                if (mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].type == "date") {
                                    dateOrTimeET = customOptionDateView.findViewById(R.id.dateET)
                                } else {
                                    dateOrTimeET = customOptionDateView.findViewById(R.id.timeET)
                                }
                                if (!dateOrTimeET.text.toString().isEmpty()) {
                                    if (mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].type == "date") {

                                        val date = dateOrTimeET.text.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                        val temp = JSONObject()
                                        temp.put("month", date[0])
                                        temp.put("day", date[1])
                                        temp.put("year", date[2])
                                        customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id, temp)
                                    } else {
                                        val time = dateOrTimeET.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                        val minAmPm = time[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                        val temp = JSONObject()
                                        temp.put("hour", time[0])
                                        temp.put("minute", minAmPm[0])
                                        temp.put("day_part", minAmPm[1].toLowerCase())
                                        customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id, temp)
                                    }
                                } else if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1) {
                                    showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                            + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                            + " " + mContext.getString(R.string._is_not_complete_))
                                    isAllRequiredOptionFilled = false
                                }
                            }

                            "date_time" -> {
                                val dateEditText1 = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>("date$noOfCustomOpt").findViewById<AppCompatTextView>(R.id.dateET)
                                val timeEditText1 = mContext.mContentViewBinding.productCustomOptionsContainer.findViewWithTag<LinearLayoutCompat>("time$noOfCustomOpt").findViewById<AppCompatTextView>(R.id.timeET)

                                val temp = JSONObject()
                                if (!dateEditText1.text.toString().isEmpty() && !timeEditText1.text.toString().isEmpty()) {
                                    val date = dateEditText1.text.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    temp.put("month", date[0])
                                    temp.put("day", date[1])
                                    temp.put("year", date[2])
                                    val time = timeEditText1.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    val minAmPm = time[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    temp.put("hour", time[0])
                                    temp.put("minute", minAmPm[0])
                                    temp.put("day_part", minAmPm[1].toLowerCase())
                                    customOptnObj.put(mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].option_id, temp)
                                } else if (isChecksEnabled && mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].is_require == 1
                                        && (dateEditText1.text.toString().isEmpty() || timeEditText1.text.toString().isEmpty())) {
                                    showNewCustomDialog(mContext, mContext.mContentViewBinding.data!!.name, mContext.getString(R.string.field_) + " "
                                            + mContext.mContentViewBinding.data!!.customOptions[noOfCustomOpt].title
                                            + " " + mContext.getString(R.string._is_not_complete_))
                                    isAllRequiredOptionFilled = false
                                }
                            }
                        }
                    }
                    mProductParamsJSON.put("options", customOptnObj)
                }
            } catch (e: Exception) {
                isAllRequiredOptionFilled = false
                showToast(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                e.printStackTrace()
            }
        }

        return isAllRequiredOptionFilled
    }

    fun onClickAddToCompareListBtn() {
        mContext.mContentViewBinding.loading = true
        ApiConnection.addToCompare(mContext, mContext.mContentViewBinding.data!!.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<BaseModel>(mContext, true) {
                    override fun onNext(addToCompareResponse: BaseModel) {
                        super.onNext(addToCompareResponse)
                        mContext.mContentViewBinding.loading = false
                        showToast(mContext, addToCompareResponse.message)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContext.mContentViewBinding.loading = false
                        showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                    }
                })
    }

    fun onClickShareBtn() {
        Utils.shareProduct(mContext, mContext.mContentViewBinding.data!!.productUrl)

    }

    fun onClickQtyDecrementBtn() {
        mContext.mContentViewBinding.data!!.qty = (mContext.mContentViewBinding.data!!.qty.toInt() - 1).toString()
    }

    fun onClickQtyIncrementBtn() {
        mContext.mContentViewBinding.data!!.qty = (mContext.mContentViewBinding.data!!.qty.toInt() + 1).toString()
    }

    fun onLongClickQtyDecrementBtn(view: View, qty: String): Boolean {
        mProductQty = Integer.parseInt(qty)
        mIsIncrement = false
        mRefreshRate = 200
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                repeatUpdateHandler.removeCallbacks(mAutoChangeQty)
            }
            false
        }
        repeatUpdateHandler.postDelayed(mAutoChangeQty, 0)
        return true
    }

    fun onLongClickQtyIncrementBtn(view: View, qty: String): Boolean {
        mProductQty = Integer.parseInt(qty)
        mIsIncrement = true
        mRefreshRate = 200
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                repeatUpdateHandler.removeCallbacks(mAutoChangeQty)
            }
            false
        }
        repeatUpdateHandler.postDelayed(mAutoChangeQty, 0)
        return true
    }

    fun onClickAddToCartBtn(goToCheckout: Boolean) {
        if (collectAllOptionData(true)) {
            getAllRelatedProducts()
            mContext.mContentViewBinding.loading = true

            if (mContext.mItemId.isBlank()) {
                if (NetworkHelper.isNetworkAvailable(mContext)) {
                    ApiConnection.addToCart(mContext, mContext.mContentViewBinding.data!!.id, mContext.mContentViewBinding.data!!.qty, mProductParamsJSON, mFiles, mRelatedProductsJSONArray)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(mContext, true) {
                                override fun onNext(addToCartResponse: AddToCartResponseModel) {
                                    super.onNext(addToCartResponse)
                                    mContext.mContentViewBinding.loading = false
                                    if (addToCartResponse.success) {
                                        addToCartResponseModel=addToCartResponse
                                        mContext.updateCartCount(addToCartResponse.cartCount)
                                        FirebaseAnalyticsHelper.logAddToCartEvent(mContext.mContentViewBinding.data!!.id, mContext.mContentViewBinding.data!!.name, mContext.mContentViewBinding.data!!.qty, mProductParamsJSON)
                                        showToast(mContext, addToCartResponse.message)
                                        if (addToCartResponse.quoteId != 0) {
                                            AppSharedPref.setQuoteId(mContext, addToCartResponse.quoteId)
                                        }
                                        if (goToCheckout) {

                                            if (!AppSharedPref.isLoggedIn(mContext) && (mContext.mContentViewBinding.data!!.typeId == "downloadable") && !mContext.mContentViewBinding.data!!.canGuestCheckoutDownloadable) {
                                                guestCheckout(addToCartResponse)
                                            } else
                                                if (AppSharedPref.isLoggedIn(mContext)) {

                                                    checkout(addToCartResponse)
                                                } else {

                                                    guestCheckout(addToCartResponse)
                                                }
                                        }
                                    } else {
                                        when (addToCartResponse.otherError) {
                                            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                                                showNewCustomDialog(
                                                        mContext,
                                                        mContext.getString(R.string.error),
                                                        addToCartResponse.message,
                                                        false,
                                                        mContext.getString(R.string.ok),
                                                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                                                            dialogInterface.dismiss()
                                                            Utils.logoutAndGoToHome(mContext)
                                                        }, "", null)
                                            }
                                            else -> showToast(mContext, addToCartResponse.message)
                                        }
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    mContext.mContentViewBinding.loading = false
                                    showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                                }
                            })
                } else {
                    addToOfflineCart()
                }
            } else {
                ApiConnection.updateProduct(mContext, mContext.mContentViewBinding.data!!.id, mContext.mContentViewBinding.data!!.qty, mProductParamsJSON, mFiles, mRelatedProductsJSONArray, mContext.mItemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(mContext, true) {
                            override fun onNext(addToCartResponse: AddToCartResponseModel) {
                                super.onNext(addToCartResponse)
                                mContext.mContentViewBinding.loading = false
                                addToCartResponseModel=addToCartResponse
                                showToast(mContext, addToCartResponse.message)
                                mContext.updateCartCount(addToCartResponse.cartCount)
                                if (addToCartResponse.success) {
                                    mContext.finish()
                                }
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                mContext.mContentViewBinding.loading = false
                                showToast(mContext, mContext.resources.getString(R.string.something_went_wrong))
                            }
                        })
            }
        }
    }

     fun checkout(addToCartResponse: AddToCartResponseModel) {
        if (addToCartResponse.isCheckoutAllowed) {
            val intent = Intent(mContext, CheckoutActivity::class.java)
            intent.putExtra(BundleKeysHelper.BUNDLE_KEY_IS_VIRTUAL_CART, addToCartResponse.isVirtual)
            mContext.startActivity(intent)
        } else {
            if (addToCartResponse.descriptionMessage.isNullOrBlank()) {
                if (addToCartResponse.minimumAmount > 0)
                    showToast(mContext, mContext.getString(R.string.min_order_amt_error) + " " + addToCartResponse.minimumFormattedAmount)
                else{
                    val intent = Intent(mContext, CheckoutActivity::class.java)
                    intent.putExtra(BundleKeysHelper.BUNDLE_KEY_IS_VIRTUAL_CART, addToCartResponse.isVirtual)
                    mContext.startActivity(intent)
                }
            } else {
                showToast(mContext, addToCartResponse.descriptionMessage!!)
            }
        }
    }

    fun guestCheckout(addToCartResponse: AddToCartResponseModel) {
        if(mContext.mContentViewBinding.data!!.isAllowedGuestCheckout && !addToCartResponse.isCheckoutAllowed) {
            if (addToCartResponse.descriptionMessage.isNullOrBlank()) {
                if (addToCartResponse.minimumAmount > 0)
                    showToast(
                            mContext,
                            mContext.getString(R.string.min_order_amt_error) + " " + addToCartResponse.minimumFormattedAmount
                    )
                else {
                    proceedGuestCheckout(addToCartResponse)
                }
            } else {
                showToast(mContext, addToCartResponse.descriptionMessage!!)
            }
        } else {
            proceedGuestCheckout(addToCartResponse)
        }
    }

    private fun proceedGuestCheckout(addToCartResponse: AddToCartResponseModel) {
        val proceedCheckoutBottomSheetFragment =
                ProceedCheckoutBottomSheetFragment.newInstance(
                        addToCartResponse.isVirtual,
                        addToCartResponse.isAllowedGuestCheckout,
                        mContext.mContentViewBinding.data!!.typeId == "downloadable",
                        addToCartResponse.canGuestCheckoutDownloadable)
        proceedCheckoutBottomSheetFragment.show(
                mContext.supportFragmentManager,
                proceedCheckoutBottomSheetFragment.tag
        )
    }




    private fun addToOfflineCart() {
        mContext.mContentViewBinding.loading = false
        showNewCustomDialog(
                mContext,
                mContext.getString(R.string.added_to_offline_cart),
                mContext.getString(R.string.offline_mode_add_to_cart_message),
                false,
                mContext.getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })

        try {
            mDataBaseHandler.addToCartOffline(
                    mContext.mContentViewBinding.data!!.id
                    , mContext.mContentViewBinding.data!!.qty
                    , mProductParamsJSON.toString()
                    , mRelatedProductsJSONArray.toString()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToast(mContext, mContext.getString(R.string.something_went_wrong))
        }
    }

    private fun getAllRelatedProducts() {
        mRelatedProductsJSONArray = JSONArray()
        if (mContext.mContentViewBinding.data!!.relatedProductList.size > 0) {
            for (noOfRelatedProducts in 0 until mContext.mContentViewBinding.data!!.relatedProductList.size) {
                if (mContext.mContentViewBinding.data!!.relatedProductList[noOfRelatedProducts].addToCart) {
                    mRelatedProductsJSONArray.put(mContext.mContentViewBinding.data!!.relatedProductList[noOfRelatedProducts].id)
                }
            }
        }
    }

    fun onClickViewAllReviewsBtn() {
        val fragmentManager = mContext.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        fragmentTransaction.add(android.R.id.content, ProductReviewListFragment.newInstance(mContext.mContentViewBinding.data!!.id))
        fragmentTransaction.addToBackStack(ProductReviewListFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun onClickDetailsBtn() {
        if (mContext.mContentViewBinding.description.visibility == View.VISIBLE) {
            mContext.mContentViewBinding.description.visibility = View.GONE
            mContext.mContentViewBinding.descriptionHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_down_arrow_grey_wrapper), null)
        } else {
            mContext.mContentViewBinding.description.visibility = View.VISIBLE
            mContext.mContentViewBinding.descriptionHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_up_arrow_grey_wrapper), null)
           Handler(Looper.getMainLooper()).postDelayed({
                val scrollTo = mContext.mContentViewBinding.scrollView.top + mContext.mContentViewBinding.descriptionHeading.top
                mContext.mContentViewBinding.scrollView.smoothScrollTo(0, scrollTo)
            }, 200)
        }
    }

    fun onClickMoreInformationBtn() {
        if (mContext.mContentViewBinding.moreInformation.visibility == View.VISIBLE) {
            mContext.mContentViewBinding.moreInformation.visibility = View.GONE
            mContext.mContentViewBinding.moreInformationHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_down_arrow_grey_wrapper), null)
        } else {
            mContext.mContentViewBinding.moreInformation.visibility = View.VISIBLE
            mContext.mContentViewBinding.moreInformationHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_up_arrow_grey_wrapper), null)
           Handler(Looper.getMainLooper()).postDelayed({
                val scrollTo = mContext.mContentViewBinding.scrollView.top + mContext.mContentViewBinding.moreInformationHeading.top
                mContext.mContentViewBinding.scrollView.smoothScrollTo(0, scrollTo)
            }, 200)
        }
    }

    fun onClickReviewsBtn() {
        if (mContext.mContentViewBinding.reviews.visibility == View.VISIBLE) {
            mContext.mContentViewBinding.reviews.visibility = View.GONE
            mContext.mContentViewBinding.reviewsHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_down_arrow_grey_wrapper), null)
        } else {
            mContext.mContentViewBinding.reviews.visibility = View.VISIBLE
            mContext.mContentViewBinding.reviewsHeading.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_up_arrow_grey_wrapper), null)
           Handler(Looper.getMainLooper()).postDelayed({
                val scrollTo = mContext.mContentViewBinding.scrollView.top + mContext.mContentViewBinding.reviewsHeading.top
                mContext.mContentViewBinding.scrollView.smoothScrollTo(0, scrollTo)
            }, 200)
        }
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        var pathName = ""
        try {
            pathName = PathUtil.getPath(mContext, fileUri)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val bitmap = BitmapFactory.decodeFile(pathName)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        val fileBody = bos.toByteArray().toRequestBody(getMimeType(fileUri)!!.toMediaTypeOrNull())
        var filePart: MultipartBody.Part? = null
        try {
            filePart = MultipartBody.Part.createFormData(partName, File(pathName).name, fileBody)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return filePart
    }

    private fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = mContext.applicationContext.contentResolver
            mimeType = cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
        return mimeType
    }

    fun onClickArBtn() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startArActivity()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                mContext.requestPermissions(permissions, ConstantsHelper.RC_AR)
            }
        }
    }

    fun startArActivity() {
        val intent: Intent?
        if (mContext.mContentViewBinding.data!!.arType == "3D") {
            intent=Intent(mContext, ArActivity::class.java)
            intent.putExtra(BundleKeysHelper.BUNDLE_KEY_AR_MODEL_URL, mContext.mContentViewBinding.data!!.arModelUrl)
        } else {
            intent= Intent(mContext, CameraWithImageActivity::class.java)
            intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE, mContext.mContentViewBinding.data!!.arModelUrl)
        }
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME, mContext.mContentViewBinding.data!!.name)

        mContext.startActivity(intent)
    }
}