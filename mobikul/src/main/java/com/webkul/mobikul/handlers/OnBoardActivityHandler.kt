package com.webkul.mobikul.handlers

import android.content.Context
import android.content.Intent
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.activities.OnBoardingActivity
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.models.homepage.OnBoardResponseModel
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OnBoardActivityHandler(val mContext: OnBoardingActivity?) {
    constructor() : this(null)


    var isGetStarted: Boolean = false

    fun onButtonClick(isIncrease: Boolean) {
        if (isGetStarted && isIncrease) {
            onSkipClick()
        } else {
            if (isIncrease) {
                if (mContext!!.mContentViewBinding.tabLayout.selectedTabPosition >= 0 && mContext.mContentViewBinding.tabLayout.selectedTabPosition < mContext.mContentViewBinding.tabLayout.tabCount)
                    mContext.mContentViewBinding.tabLayout.getTabAt(mContext.mContentViewBinding.tabLayout.selectedTabPosition + 1)?.select()
            } else {
                if (mContext!!.mContentViewBinding.tabLayout.selectedTabPosition > 0 && mContext.mContentViewBinding.tabLayout.selectedTabPosition <= mContext.mContentViewBinding.tabLayout.tabCount)
                    mContext.mContentViewBinding.tabLayout.getTabAt(mContext.mContentViewBinding.tabLayout.selectedTabPosition - 1)?.select()
            }
        }
    }

    fun onSkipClick() {
        mContext?.let {
            val mIsFreshHomePageData = it.intent?.getBooleanExtra(BundleKeysHelper.BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA, false)
            AppSharedPref.setShowOnBoardVersion(it, false)
            val intent = Intent(it, HomeActivity::class.java)

            intent.putExtra(BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA, it.intent?.getStringExtra(BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA))
            intent.putExtra(BundleKeysHelper.BUNDLE_KEY_IS_FRESH_HOME_PAGE_DATA, mIsFreshHomePageData)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            it.startActivity(intent)

            mContext.mContentViewBinding.loading = true

        }
    }


    fun callApi(context: Context, onSuccessfulResponse: (m: OnBoardResponseModel) -> Unit, onErrorResponse: (m: Throwable) -> Unit, onFailureResponse: (m: OnBoardResponseModel) -> Unit) {
        ApiConnection.getOnBoardData(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<OnBoardResponseModel>(context, false) {
                    override fun onNext(responseModel: OnBoardResponseModel) {
                        super.onNext(responseModel)
                        if (responseModel.success) {
                            onSuccessfulResponse(responseModel)
                        } else {
                            onFailureResponse(responseModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        onErrorResponse(e)
                    }
                })
    }
}