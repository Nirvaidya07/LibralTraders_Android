package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api
import com.google.gson.Gson
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.MyOrdersRvAdapter
import com.webkul.mobikul.adapters.OrderAgainAdapter
import com.libraltraders.android.databinding.ActivityMyOrdersBinding
import com.webkul.mobikul.fragments.CartBottomSheetFragment
import com.webkul.mobikul.fragments.EmptyFragment
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.checkout.AddToCartResponseModel
import com.webkul.mobikul.models.product.ProductData
import com.webkul.mobikul.models.product.ProductResponse
import com.webkul.mobikul.models.user.OrderListResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

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

class MyOrdersActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityMyOrdersBinding
    private var mPageNumber = 1
    private var isFirstTime: Boolean = true
    var productList = ArrayList<ProductData>()
    var isFromOrderAgain = false
    var lastItemCount = 0;
    var lastCount = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders)
        startInitialization()
    }

    private fun startInitialization() {
        initClick()
        initSupportActionBar()
        callApi()
        initSwipeRefresh()

    }

    var count = 0;
    private fun initClick() {
        mContentViewBinding.btnAddToCart.setOnClickListener(View.OnClickListener {
            if (productList.size != 0) {
                addAllToCart()
            }

        })
    }

    private fun addAllToCart() {
        try {
            var productData = productList.get(count);
            if (NetworkHelper.isNetworkAvailable(applicationContext)) {
                mContentViewBinding.swipeRefreshLayout.isRefreshing = true
                ApiConnection.addToCart(
                    applicationContext,
                    productData.productid.toString(),
                    productData.qty_order.toString(),
                    JSONObject(),
                    null,
                    JSONArray()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<AddToCartResponseModel>(
                        applicationContext,
                        true
                    ) {
                        override fun onNext(addToCartResponse: AddToCartResponseModel) {
                            super.onNext(addToCartResponse)
                            mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                            /*   runOnUiThread(Runnable {
                            ToastHelper.showToast(
                                applicationContext,
                                addToCartResponse.message
                            )
                        })*/

                            if (addToCartResponse.cartCount != 0) {
                                lastCount = addToCartResponse.cartCount
                            }
                            updateCartCount(lastCount)
//                        updateCartCount(addToCartResponse.cartCount)
                            if (addToCartResponse.success) {
                                if (addToCartResponse.quoteId != 0) {
                                    AppSharedPref.setQuoteId(
                                        applicationContext,
                                        addToCartResponse.quoteId
                                    )
                                }
                            }
                            count = count + 1;
                            if (count < productList.size) {
                                addAllToCart()
                            } else {
                                count = 0
                                CartBottomSheetFragment().show(
                                    supportFragmentManager,
                                    CartBottomSheetFragment::class.java.simpleName
                                )
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            mContentViewBinding.swipeRefreshLayout.isRefreshing = true
                            /*ToastHelper.showToast(
                            applicationContext,
                            resources.getString(R.string.something_went_wrong)
                        )*/
                        }
                    })
            } else {
                if (count == 0) {
                    addToOfflineCart(
                        productData.productid.toString(),
                        productData.qty_order.toString()
                    )
                } else {
                    count = count + 1;
                    if (count < productList.size) {
                        try {
                            BaseActivity.mDataBaseHandler.addToCartOffline(
                                productData.productid.toString(),
                                productData.qty_order.toString(),
                                "",
                                ""
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            ToastHelper.showToast(
                                applicationContext,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        count = 0
                        CartBottomSheetFragment().show(
                            supportFragmentManager,
                            CartBottomSheetFragment::class.java.simpleName
                        )
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun addToOfflineCart(entityId: String, qunty: String) {
        AlertDialogHelper.showNewCustomDialog(
            applicationContext as BaseActivity,
            getString(R.string.added_to_offline_cart),
            getString(R.string.offline_mode_add_to_cart_message),
            false,
            getString(R.string.ok),
            DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })

        try {
            BaseActivity.mDataBaseHandler.addToCartOffline(
                entityId, qunty, "", ""
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ToastHelper.showToast(applicationContext, getString(R.string.something_went_wrong))
        }
    }

    override fun initSupportActionBar() {
        isFromOrderAgain =
            intent.getBooleanExtra(BundleKeysHelper.BUNDLE_KEY_FROM_ORDER_AGAIN, false)
        if (isFromOrderAgain) {
            supportActionBar?.title = getString(R.string.title_activity_order_again)
        } else {
            supportActionBar?.title = getString(R.string.activity_title_my_orders)
        }
        super.initSupportActionBar()
    }

    private fun initSwipeRefresh() {
        mContentViewBinding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        mContentViewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkHelper.isNetworkAvailable(this)) {
                mPageNumber = 1
                callApi()
            } else {
                mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                ToastHelper.showToast(this@MyOrdersActivity, getString(R.string.you_are_offline))
            }
        }
    }


    private fun callApi() {
        mContentViewBinding.swipeRefreshLayout.isRefreshing = true
        mHashIdentifier = Utils.getMd5String(
            "getOrderList" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + AppSharedPref.getCurrencyCode(
                this
            ) + mPageNumber
        )
        if (isFirstTime && !isFromOrderAgain) {
            checkAndLoadLocalData()
            isFirstTime = false
        }
        if (isFromOrderAgain) {
            mContentViewBinding.btnAddToCart.visibility = View.VISIBLE
            ApiConnection.orderAgain(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val response = it
                    var productResponse = Gson().fromJson(response, ProductResponse::class.java)
                    productList = productResponse?.data!!
                    mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                    setupOrderAgainRv()
                    Log.d("TAG", "callApi:orderagain " + productList)
                },
                    {
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(
                            this,
                            "shipment details not available for this order",
                            Toast.LENGTH_LONG
                        ).show()
                    })
        } else {
            mContentViewBinding.btnAddToCart.visibility = View.GONE
            ApiConnection.getOrderList(
                this,
                mDataBaseHandler.getETagFromDatabase(mHashIdentifier),
                mPageNumber++,
                false
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<OrderListResponseModel>(this, false) {
                    override fun onNext(orderListResponseModel: OrderListResponseModel) {
                        super.onNext(orderListResponseModel)
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        if (orderListResponseModel.success) {
                            onSuccessfulResponse(orderListResponseModel)
                        } else {
                            onFailureResponse(orderListResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.swipeRefreshLayout.isRefreshing = false
                        onErrorResponse(e)
                    }
                })
        }
    }

    lateinit var orderAgainAdapter: OrderAgainAdapter;
    private fun setupOrderAgainRv() {
        orderAgainAdapter = OrderAgainAdapter(this, productList)
        mContentViewBinding.ordersRv.adapter = orderAgainAdapter
    }

    private fun checkAndLoadLocalData() {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<String> {
                override fun onNext(response: String) {
                    if (response.isNotBlank()) {
                        val orderListResponseModel: OrderListResponseModel =
                            mObjectMapper.readValue(response, OrderListResponseModel::class.java)
                        if (orderListResponseModel.orderList.size > 0) {
                            onSuccessfulResponse(orderListResponseModel)
                        }
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

    private fun onSuccessfulResponse(orderListResponseModel: OrderListResponseModel) {
        if (mPageNumber == 2) {
            mContentViewBinding.data = orderListResponseModel
            if (mContentViewBinding.data!!.orderList.isEmpty()) {
                addEmptyLayout()
            } else {
                removeEmptyLayout()
                setupOrderListRv()
            }
        } else {
            mContentViewBinding.data!!.orderList.addAll(orderListResponseModel.orderList)
            mContentViewBinding.ordersRv.adapter?.notifyItemRangeChanged(
                mContentViewBinding.data!!.orderList.size - orderListResponseModel.orderList.size,
                mContentViewBinding.data!!.orderList.size
            )
        }
    }

    private fun addEmptyLayout() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            android.R.id.content,
            EmptyFragment.newInstance(
                "empty_order_list.json",
                getString(R.string.empty_order_list),
                getString(R.string.add_item_to_your_order_list_now),
                false
            ),
            EmptyFragment::class.java.simpleName
        )
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun removeEmptyLayout() {
        val emptyFragment =
            supportFragmentManager.findFragmentByTag(EmptyFragment::class.java.simpleName)
        if (emptyFragment != null)
            supportFragmentManager.beginTransaction().remove(emptyFragment)
                .commitAllowingStateLoss()
    }

    private fun setupOrderListRv() {
        mContentViewBinding.ordersRv.adapter =
            MyOrdersRvAdapter(this, mContentViewBinding.data!!.orderList)
        mContentViewBinding.ordersRv.isNestedScrollingEnabled = false

        mContentViewBinding.ordersRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastCompletelyVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (!mContentViewBinding.swipeRefreshLayout.isRefreshing ?: false && mContentViewBinding.data!!.orderList.size < mContentViewBinding.data!!.totalCount
                    && lastCompletelyVisibleItemPosition > mContentViewBinding.data!!.orderList.size - 3
                ) {
                    callApi()
                }
            }
        })
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
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        mPageNumber--
                        callApi()
                    }, "", null
                )
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
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    mPageNumber--
                    callApi()
                },
                getString(R.string.dismiss),
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    if (mPageNumber == 2)
                        finish()
                })
        }
    }

    fun callCartMine(sku: String, qty: String, productData: ProductData) {
        Log.d(
            "TAG",
            "callCartMine: " + AppSharedPref.getCustomerToken(applicationContext) + "\n" + productData
        )
//        ApiConnection.orderAgain(this)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe({
//                val response = it
//                var productResponse = Gson().fromJson(response, ProductResponse::class.java)
//                productList = productResponse?.data!!
//                mContentViewBinding.swipeRefreshLayout.isRefreshing = false
//                setupOrderAgainRv()
//                Log.d("TAG", "callApi:orderagain " + productList)
//            },
//                {
//                    mContentViewBinding.swipeRefreshLayout.isRefreshing = false
//                    Toast.makeText(
//                        this,
//                        "shipment details not available for this order",
//                        Toast.LENGTH_LONG
//                    ).show()
//                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //menu.findItem(R.id.menu_item_notification)?.isVisible = false
        menu.findItem(R.id.menu_item_search).isVisible = false
        menu.findItem(R.id.menu_item_notification).isVisible = false
        menu.findItem(R.id.menu_item_wishlist).isVisible = false
        menu.findItem(R.id.menu_item_share).isVisible = false
        return true
    }

}