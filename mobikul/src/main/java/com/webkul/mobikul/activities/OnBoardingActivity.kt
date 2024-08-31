package com.webkul.mobikul.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityOnBoardingBinding
import com.webkul.mobikul.fragments.OnBoardFragment
import com.webkul.mobikul.handlers.OnBoardActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_ON_BOARD_DATA
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.homepage.OnBoardListData
import com.webkul.mobikul.models.homepage.OnBoardResponseModel
import retrofit2.HttpException

class OnBoardingActivity : BaseActivity() {
    lateinit var mContentViewBinding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.handler = OnBoardActivityHandler(this)
        if (intent.hasExtra(BUNDLE_KEY_ON_BOARD_DATA)) {
            val responseModel = intent.getStringExtra(BUNDLE_KEY_ON_BOARD_DATA).let {
                mObjectMapper.readValue(it, OnBoardResponseModel::class.java)
            }
            if (responseModel != null) {
                onSuccessfulResponse(responseModel)
            } else {
                mContentViewBinding.handler?.callApi(this, ::onSuccessfulResponse, ::onErrorResponse, ::onFailureResponse)
            }

        } else {
            mContentViewBinding.handler?.callApi(this, ::onSuccessfulResponse, ::onErrorResponse, ::onFailureResponse)
        }
    }


    private fun onSuccessfulResponse(responseModel: OnBoardResponseModel) {
        mContentViewBinding.loading = false
        responseModel.walkThroughVersion?.let { AppSharedPref.setOnBoardVersion(this, it) }
        mContentViewBinding.data = responseModel
        when {
            mContentViewBinding.data?.walkThroughData?.isNullOrEmpty() == true -> {
                mContentViewBinding.handler?.onSkipClick()
            }
            mContentViewBinding.data!!.walkThroughData?.size == 1 -> {
                mContentViewBinding.handler?.isGetStarted = true
                mContentViewBinding.nextBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                mContentViewBinding.nextBtn.text = getString(R.string.done)
                mContentViewBinding.skipBtn.visibility = View.GONE
                mContentViewBinding.loading = false
                setupViewPager()
            }
            else -> {
                mContentViewBinding.loading = false
                setupViewPager()
            }
        }
    }


    private fun setupViewPager() {
        mContentViewBinding.tabLayout.setupWithViewPager(mContentViewBinding.onBoardVp)

        for (i in 0 until mContentViewBinding.data!!.walkThroughData!!.size) {
            mContentViewBinding.tabLayout.addTab(mContentViewBinding.tabLayout.newTab())
        }

        mContentViewBinding.onBoardVp.adapter = ViewPagerAdapter(this, supportFragmentManager, mContentViewBinding.tabLayout.tabCount, mContentViewBinding.data!!.walkThroughData!!)

        mContentViewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mContentViewBinding.onBoardVp.currentItem = tab.position
                /*previous button visibility*/
                when {
                    mContentViewBinding.tabLayout.selectedTabPosition > 0 -> {
                        mContentViewBinding.previousBtn.visibility = View.VISIBLE
                    }
                    mContentViewBinding.tabLayout.selectedTabPosition == 0 -> {
                        mContentViewBinding.previousBtn.visibility = View.INVISIBLE
                    }
                }

                /*Next button text change*/
                when {
                    mContentViewBinding.tabLayout.selectedTabPosition == mContentViewBinding.tabLayout.tabCount - 1 -> {
                        mContentViewBinding.nextBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        mContentViewBinding.nextBtn.text = getString(R.string.done)
                        mContentViewBinding.skipBtn.visibility = View.GONE
                        mContentViewBinding.handler?.isGetStarted = true
                    }
                    mContentViewBinding.tabLayout.selectedTabPosition < mContentViewBinding.tabLayout.tabCount -> {
                        mContentViewBinding.nextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_vector_right_arrow, 0);
                        mContentViewBinding.nextBtn.text = getString(R.string.next)
                        mContentViewBinding.skipBtn.visibility = View.VISIBLE
                        mContentViewBinding.handler?.isGetStarted = false
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        mContentViewBinding.onBoardVp.offscreenPageLimit = 5
    }

    class ViewPagerAdapter internal constructor(val context: OnBoardingActivity, fm: FragmentManager, private val mNumOfTabs: Int, private val data: ArrayList<OnBoardListData>) : FragmentStatePagerAdapter(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        var fragment: Fragment = Fragment()
        var registeredFragments: SparseArray<Fragment> = SparseArray<Fragment>()

        override fun getItem(position: Int): Fragment {
            for (i in 0 until mNumOfTabs) {
                if (i == position) {
                    fragment = OnBoardFragment.newInstance(data[i])
                    break
                }
            }
            return fragment
        }

        override fun getCount(): Int {
            return mNumOfTabs
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container!!, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            registeredFragments.remove(position)
            super.destroyItem(container!!, position, `object`!!)
        }
    }


    override fun onFailureResponse(response: Any) {
        mContentViewBinding.loading = false
        super.onFailureResponse(response)
        when ((response as BaseModel).otherError) {
            ConstantsHelper.CUSTOMER_NOT_EXIST -> {
                // Do nothing as it will be handled from the super.
            }
            else -> {
                ToastHelper.showToast(this, response.message)
            }
        }
    }

    private fun onErrorResponse(error: Throwable) {
        mContentViewBinding.loading = false

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
                        mContentViewBinding.loading = true
                        mContentViewBinding.handler?.callApi(this, ::onSuccessfulResponse, ::onErrorResponse, ::onFailureResponse)
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
        }
    }

}