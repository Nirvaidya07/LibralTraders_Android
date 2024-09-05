package com.webkul.mobikul.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_BANNER
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_CATEGORY
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_CATEGORY_DIVIDER
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_HOT_SELLER
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_PARENT_CATEGORY
import com.webkul.mobikul.adapters.SubCategoryFragmentAdapter.Companion.VIEW_TYPE_PRODUCTS
import com.libraltraders.android.databinding.FragmentSubCategoryBinding
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.models.CategoriesData
import com.webkul.mobikul.models.catalog.SubCategoryResponseModel
import com.webkul.mobikul.models.homepage.Category
import com.webkul.mobikul.network.ApiClient
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SubCategoryFragment : BaseFragment() {
    lateinit var mContentViewBinding: FragmentSubCategoryBinding
    private var mSubCategory: Category = Category()
    var mSubCategoryDisposable = CompositeDisposable()

    companion object {
        fun newInstance(category: List<Category>?): SubCategoryFragment {
            val subCategoryFragment = SubCategoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(
                BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA,
                category as ArrayList
            )
            subCategoryFragment.arguments = args
            return subCategoryFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContentViewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sub_category, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentViewBinding.loading = true
        (context as BaseActivity).mHashIdentifier = ""
        mainCategoryView()
    }

    private fun mainCategoryView() {
        val category: ArrayList<Category>? =
            requireArguments().getParcelableArrayList(BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA)
        val radioButtons = ArrayList<RadioButton>()
        if (isAdded) {

            category?.forEach {
                val radioButton =
                    layoutInflater.inflate(R.layout.custom_radio_button, null) as RadioButton
                radioButtons.add(radioButton)
                radioButton.layoutParams = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                radioButton.text = it.name
                radioButton.tag = it
                mContentViewBinding.categoryRg.addView(radioButton)
            }

            mContentViewBinding.categoryRg.setOnCheckedChangeListener { group, checkedId ->
                val selectedRb = group.findViewById<RadioButton>(checkedId)
                radioButtons.forEach {
                    it.setTypeface(selectedRb.typeface, Typeface.NORMAL)
                }
                selectedRb.setTypeface(selectedRb.typeface, Typeface.BOLD)

                if (mContentViewBinding.subCategoriesRv.adapter != null) {
                    (mContentViewBinding.subCategoriesRv.adapter as SubCategoryFragmentAdapter).updateItem(
                        ArrayList()
                    )
                }
                mSubCategory = selectedRb.tag as Category
                callSubCategoryApi()
            }
            if (!category.isNullOrEmpty()) {
                (mContentViewBinding.categoryRg.getChildAt(0) as RadioButton).isChecked = true
            }

        }

    }

    /*SubCategory*/
    private fun callSubCategoryApi() {
        mSubCategoryDisposable.clear()
        mContentViewBinding.loading = true
        val hashIdentifier = Utils.getMd5String(
            "getSubCategoryData" + AppSharedPref.getStoreId(requireContext()) + AppSharedPref.getCustomerToken(
                requireContext()
            ) + mSubCategory.id.toString() + mSubCategory.name
        )
        checkAndLoadLocalSubCateSubCategoryData(hashIdentifier)
        ApiConnection.getSubCategoryData(
            requireContext(),
            BaseActivity.mDataBaseHandler.getETagFromDatabase(hashIdentifier),
            mSubCategory.id.toString()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCustomCallback<SubCategoryResponseModel>(requireContext(), false) {
                override fun onNext(responseModel: SubCategoryResponseModel) {
                    super.onNext(responseModel)
                    mContentViewBinding.loading = false
                    if (responseModel.success) {
                        if (comparePOJO(responseModel)) {
                            saveSubChildCategoryLocalData(hashIdentifier, responseModel)
                            onSuccessfulSubCategoryResponse(responseModel)
                        }
                    } else {
                        onFailureSubCategoryResponse(responseModel)
                    }
                }

                override fun onSubscribe(disposable: Disposable) {
                    mSubCategoryDisposable.add(disposable)

                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    mContentViewBinding.loading = false
                    onErrorSubCategoryResponse(e)
                }
            })

    }

    fun comparePOJO(obj1: SubCategoryResponseModel): Boolean {
        return if (mContentViewBinding.data == null) {
            true
        } else {
            !BaseActivity.mObjectMapper.writeValueAsString(obj1)
                .equals(BaseActivity.mObjectMapper.writeValueAsString(mContentViewBinding.data))
        }
    }

    private fun saveSubChildCategoryLocalData(
        mHashIdentifier: String,
        subCategoryResponseModel: SubCategoryResponseModel
    ) {
        if (ApplicationConstants.ENABLE_OFFLINE_MODE && mHashIdentifier.isNotEmpty()) {

            BaseActivity.mDataBaseHandler.saveOfflineTable(
                mHashIdentifier,
                subCategoryResponseModel.eTag,
                BaseActivity.mObjectMapper.writeValueAsString(subCategoryResponseModel)
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<Boolean> {
                    override fun onNext(data: Boolean) {

                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {

                    }
                })
        }
    }


    @SuppressLint("CheckResult")
    private fun checkAndLoadLocalSubCateSubCategoryData(hashIdentifier: String) {
        BaseActivity.mDataBaseHandler.getLocalData(hashIdentifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.isNotBlank()) {
                    //      mContentViewBinding.loading = false
                    onSuccessfulSubCategoryResponse(
                        BaseActivity.mObjectMapper.readValue(
                            it,
                            SubCategoryResponseModel::class.java
                        )
                    )
                }
            }
    }

    private fun onSuccessfulSubCategoryResponse(subCategoryResponseModel: SubCategoryResponseModel) {
        if (isAdded) {
            mContentViewBinding.data = subCategoryResponseModel
            val subCategoriesData = ArrayList<CategoriesData>()

            if (subCategoryResponseModel.smallBannerImage.isNotEmpty()) {
                subCategoriesData.add(
                    CategoriesData(
                        VIEW_TYPE_BANNER,
                        bannerImage = subCategoryResponseModel.smallBannerImage
                    )
                )
            }

            subCategoryResponseModel.subCategoriesList.forEach { parentCategory ->
//                if (parentCategory.bannerImage.isNotEmpty()) {
//                    subCategoriesData.add(CategoriesData(VIEW_TYPE_CHILD_CATEGORY_BANNER, bannerImage = subCategoryResponseModel.bannerImage,parentCategoryId = parentCategory.id))
//                }
                if (parentCategory.hasChildren) {
                    subCategoriesData.add(CategoriesData(VIEW_TYPE_CATEGORY_DIVIDER))
                    subCategoriesData.add(
                        CategoriesData(
                            VIEW_TYPE_PARENT_CATEGORY,
                            category = parentCategory,
                            parentCategoryId = parentCategory.id
                        )
                    )
                    parentCategory.childCategories.forEach { category ->
                        subCategoriesData.add(
                            CategoriesData(
                                VIEW_TYPE_CATEGORY,
                                category = category,
                                parentCategoryId = parentCategory.id
                            )
                        )
                    }
                } else {
                    subCategoriesData.add(CategoriesData(VIEW_TYPE_CATEGORY_DIVIDER))
                    subCategoriesData.add(
                        CategoriesData(
                            VIEW_TYPE_PARENT_CATEGORY,
                            category = parentCategory,
                            parentCategoryId = parentCategory.id
                        )
                    )
                }
                subCategoriesData.add(
                    CategoriesData(
                        VIEW_TYPE_CATEGORY,
                        category = parentCategory,
                        parentCategoryId = parentCategory.id
                    )
                )
            }

            if (subCategoryResponseModel.subCategoriesList.size > 0) {
                subCategoriesData.add(CategoriesData(VIEW_TYPE_CATEGORY_DIVIDER))
            }

            if (subCategoryResponseModel.hotSeller.isNotEmpty()) {
                subCategoriesData.add(
                    CategoriesData(
                        VIEW_TYPE_HOT_SELLER,
                        productList = subCategoryResponseModel.hotSeller,
                        category = mSubCategory,
                        title = getString(R.string.hot_sellers)
                    )
                )
            }

            if (subCategoryResponseModel.productList.isNotEmpty()) {
                subCategoriesData.add(
                    CategoriesData(
                        VIEW_TYPE_PRODUCTS,
                        productList = subCategoryResponseModel.productList,
                        category = mSubCategory,
                        title = getString(R.string.products)
                    )
                )
            }

            if (mContentViewBinding.subCategoriesRv.adapter == null) {
                mContentViewBinding.subCategoriesRv.layoutManager = GridLayoutManager(requireContext(), 3)
                mContentViewBinding.subCategoriesRv.adapter =
                    SubCategoryFragmentAdapter(subCategoriesData)

                mContentViewBinding.subCategoriesRv.setHasFixedSize(true)
            } else {
                (mContentViewBinding.subCategoriesRv.adapter as SubCategoryFragmentAdapter).updateItem(
                    subCategoriesData
                )
            }

        }
    }


    private fun onFailureSubCategoryResponse(subCategoryResponseModel: SubCategoryResponseModel) {
        AlertDialogHelper.showNewCustomDialog(
            activity as BaseActivity,
            getString(R.string.error),
            subCategoryResponseModel.message,
            false,
            getString(R.string.try_again),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                callSubCategoryApi()
            },
            getString(R.string.dismiss),
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
    }

    private fun onErrorSubCategoryResponse(error: Throwable) {
        if ((!NetworkHelper.isNetworkAvailable(activity as BaseActivity) || (error is HttpException && error.code() == 304)) && mContentViewBinding.data != null) {
            // Do Nothing as the data is already loaded
        } else {
            AlertDialogHelper.showNewCustomDialog(
                activity as BaseActivity,
                getString(R.string.error),
                NetworkHelper.getErrorMessage(activity as BaseActivity, error),
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callSubCategoryApi()
                },
                getString(R.string.dismiss),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
        }
    }

    override fun onDestroy() {
        (context as BaseActivity).mCompositeDisposable.clear()
        mSubCategoryDisposable.clear()
        ApiClient.getDispatcher().cancelAll()
        super.onDestroy()
    }

}