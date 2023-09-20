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

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.CompareProductsAddToCartRvAdapter
import com.webkul.mobikul.adapters.CompareProductsAttributesListRvAdapter
import com.webkul.mobikul.adapters.CompareProductsRvAdapter
import com.webkul.mobikul.databinding.ActivityCompareProductsBinding
import com.webkul.mobikul.databinding.ItemCompareProductsAttributeBinding
import com.webkul.mobikul.fragments.EmptyFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.catalog.CompareListData
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class CompareProductsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityCompareProductsBinding
    private var mTouchedRvTag = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_compare_products)
        startInitialization()
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
        checkAndLoadLocalData()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_compare_products)
        super.initSupportActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getCompareList" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + AppSharedPref.getCurrencyCode(this))
        ApiConnection.getCompareList(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<CompareListData>(this, false) {
                    override fun onNext(compareListData: CompareListData) {
                        super.onNext(compareListData)
                        mContentViewBinding.loading = false
                        if (compareListData.success) {
                            onSuccessfulResponse(compareListData)
                        } else {
                            onFailureResponse(compareListData)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            val compareListData: CompareListData =mObjectMapper.readValue(response, CompareListData::class.java)
                            if (!compareListData.productList.isNullOrEmpty())
                                onSuccessfulResponse(compareListData)
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

    private fun onSuccessfulResponse(compareListData: CompareListData) {
        mContentViewBinding.data = compareListData

        if (mContentViewBinding.data!!.productList.isEmpty()) {
            addEmptyLayout()
        } else {
            removeEmptyLayout()
            setupCompareProductsRv()
        }
    }

    private fun addEmptyLayout() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(android.R.id.content
                , EmptyFragment.newInstance("empty_compare_list.json", getString(R.string.empty_compare_list), getString(R.string.add_item_to_your_compare_list_now), false, getString(R.string.start_shopping))
                , EmptyFragment::class.java.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun removeEmptyLayout() {
        val emptyFragment = supportFragmentManager.findFragmentByTag(EmptyFragment::class.java.simpleName)
        if (emptyFragment != null)
            supportFragmentManager.beginTransaction().remove(emptyFragment).commitAllowingStateLoss()
    }

    private fun setupCompareProductsRv() {
        mContentViewBinding.attributesContainer.removeAllViews()
        if (mContentViewBinding.productsRv.adapter == null) {
            mContentViewBinding.productsRv.tag = 0
            mContentViewBinding.productsRv.addItemDecoration(DividerItemDecoration(this, LinearLayout.HORIZONTAL))
            mContentViewBinding.productsRv.addOnItemTouchListener(mRvOnTouchListener)
            mContentViewBinding.productsRv.addOnScrollListener(mRvOnScrollListener)
        }
        mContentViewBinding.productsRv.adapter = CompareProductsRvAdapter(this, mContentViewBinding.data!!.productList)

        for (noOfAttributes in 0 until mContentViewBinding.data!!.attributeValueList.size) {
            val itemCompareProductAttribute = ItemCompareProductsAttributeBinding.inflate(layoutInflater, mContentViewBinding.attributesContainer, true)

            itemCompareProductAttribute.attributeName.text = mContentViewBinding.data!!.attributeValueList[noOfAttributes].attributeName

            itemCompareProductAttribute.attributeRv.adapter = CompareProductsAttributesListRvAdapter(this, mContentViewBinding.data!!.attributeValueList[noOfAttributes].value)
            itemCompareProductAttribute.attributeRv.tag = noOfAttributes + 1
            itemCompareProductAttribute.attributeRv.addItemDecoration(DividerItemDecoration(this, LinearLayout.HORIZONTAL))
            itemCompareProductAttribute.attributeRv.addOnItemTouchListener(mRvOnTouchListener)
            itemCompareProductAttribute.attributeRv.addOnScrollListener(mRvOnScrollListener)
        }

        if (mContentViewBinding.addToCartRv.adapter == null) {
            mContentViewBinding.addToCartRv.tag = mContentViewBinding.data!!.attributeValueList.size + 1
            mContentViewBinding.addToCartRv.addItemDecoration(DividerItemDecoration(this, LinearLayout.HORIZONTAL))
            mContentViewBinding.addToCartRv.addOnItemTouchListener(mRvOnTouchListener)
            mContentViewBinding.addToCartRv.addOnScrollListener(mRvOnScrollListener)
        }
        mContentViewBinding.addToCartRv.adapter = CompareProductsAddToCartRvAdapter(this, mContentViewBinding.data!!.productList)
    }

    private val mRvOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val currentRvTag = recyclerView.tag as Int
            if (currentRvTag == mTouchedRvTag) {
                for (noOfAttributes in 0 until mContentViewBinding.data!!.attributeValueList.size + 2) {
                    if (noOfAttributes != currentRvTag) {
                        val tempRecyclerView: RecyclerView? = mContentViewBinding.container.findViewWithTag(noOfAttributes)
                        tempRecyclerView?.scrollBy(dx, dy)
                    }
                }
            }
        }
    }

    private val mRvOnTouchListener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            mTouchedRvTag = rv.tag as Int
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    override fun onFailureResponse(response: Any) {
        super.onFailureResponse(response)
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                // Do nothing as it will be handled from the super.
            }
            else -> {
                AlertDialogHelper.showNewCustomDialog(
                        this,
                        getString(R.string.error),
                        response.message,
                        false,
                        getString(R.string.ok),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            callApi()
                        }
                        , ""
                        , null)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this) || (error is HttpException && error.code() == 304)) && mContentViewBinding.data != null) {
            // Do Nothing as the data is already loaded
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }
                    , getString(R.string.dismiss)
                    , { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                if (mContentViewBinding.data == null) {
                    finish()
                }
            })
        }
    }
}