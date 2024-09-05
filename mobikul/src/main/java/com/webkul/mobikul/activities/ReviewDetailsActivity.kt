package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.RatingDataRvAdapter
import com.libraltraders.android.databinding.ActivityReviewDetailsBinding
import com.webkul.mobikul.handlers.ReviewDetailsActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.user.ReviewDetailsResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class ReviewDetailsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityReviewDetailsBinding
    private var mReviewId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_review_details)
        startInitialization()
    }

    private fun startInitialization() {
        mReviewId = intent.getStringExtra(BundleKeysHelper.BUNDLE_KEY_REVIEW_ID)!!
        initSupportActionBar()
        callApi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_review_details)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getReviewDetails" + AppSharedPref.getStoreId(this) + AppSharedPref.getCustomerToken(this) + mReviewId)
        ApiConnection.getReviewDetails(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier), mReviewId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<ReviewDetailsResponseModel>(this, true) {
                    override fun onNext(reviewDetailsResponseModel: ReviewDetailsResponseModel) {
                        super.onNext(reviewDetailsResponseModel)
                        mContentViewBinding.loading = false
                        if (reviewDetailsResponseModel.success) {
                            onSuccessfulResponse(reviewDetailsResponseModel)
                        } else {
                            onFailureResponse(reviewDetailsResponseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(reviewDetailsResponseModel: ReviewDetailsResponseModel) {
        mContentViewBinding.data = reviewDetailsResponseModel
        mContentViewBinding.handler = ReviewDetailsActivityHandler(this)

        setupRatingDataRv()
    }

    private fun setupRatingDataRv() {
        mContentViewBinding.ratingDataRv.adapter = RatingDataRvAdapter(this, mContentViewBinding.data!!.ratingData)
        mContentViewBinding.ratingDataRv.isNestedScrollingEnabled = false
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
                            callApi()
                        }, "", null)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(this@ReviewDetailsActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@ReviewDetailsActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@ReviewDetailsActivity, error),
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


    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, ReviewDetailsResponseModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@ReviewDetailsActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@ReviewDetailsActivity, error),
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
