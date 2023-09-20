package com.webkul.mobikul.customviews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.adapters.RecentSearchesAdapter
import com.webkul.mobikul.adapters.SearchSuggestionProductAdapter
import com.webkul.mobikul.adapters.SearchSuggestionTagAdapter
import com.webkul.mobikul.adapters.SearchViewCategoryRvAdapter
import com.webkul.mobikul.databinding.MaterialSearchViewBinding
import com.webkul.mobikul.handlers.MaterialSearchViewHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_SEARCH
import com.webkul.mobikul.helpers.ConstantsHelper.RC_VOICE
import com.webkul.mobikul.models.extra.SearchSuggestionResponse
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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

class MaterialSearchView : FrameLayout {

    companion object {
        private const val MAX_VOICE_RESULTS = 1
    }

    lateinit var mBinding: MaterialSearchViewBinding
    val mContext: Context
    private var mOpen: Boolean = false
    private var mCurrentQuery: CharSequence? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initializeView()
    }

    private fun initializeView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.material_search_view, this, true)
        mBinding.handler = MaterialSearchViewHandler(this)
        displayVoiceButton(true)
        initSearchView()
        setupRecentSearchData()
        setupCategoryData()
    }

    private fun initSearchView() {
        mBinding.etSearch.setOnEditorActionListener { v, _, _ ->
            onSubmitQuery()
            true
        }

        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (!charSequence.toString().isEmpty() && charSequence.length >= 3) {
                        (mContext as BaseActivity).mCompositeDisposable.clear()
                        mBinding.loader.visibility = View.VISIBLE
                        ApiConnection.getSearchSuggestions(mContext, charSequence.toString())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(object : ApiCustomCallback<SearchSuggestionResponse>(mContext, false) {
                                    override fun onNext(searchSuggestionResponse: SearchSuggestionResponse) {
                                        super.onNext(searchSuggestionResponse)
                                        mBinding.data = searchSuggestionResponse
                                        mBinding.loader.visibility = View.GONE

                                        // Setup Suggestions Rv
                                        if (mBinding.suggestionTagsRv.adapter == null) {
                                            mBinding.suggestionTagsRv.addItemDecoration(DividerItemDecoration(mContext, LinearLayout.VERTICAL))
                                            mBinding.suggestionTagsRv.adapter = SearchSuggestionTagAdapter(this@MaterialSearchView, searchSuggestionResponse.suggestProductArray.tags)
                                        } else {
                                            (mBinding.suggestionTagsRv.adapter as SearchSuggestionTagAdapter).updateData(searchSuggestionResponse.suggestProductArray.tags)
                                        }

                                        // Setup Products Rv
                                        if (mBinding.popularProductsRv.adapter == null) {
                                            mBinding.popularProductsRv.addItemDecoration(DividerItemDecoration(mContext, LinearLayout.VERTICAL))
                                            mBinding.popularProductsRv.adapter = SearchSuggestionProductAdapter(mContext, searchSuggestionResponse.suggestProductArray.products)
                                        } else {
                                            (mBinding.popularProductsRv.adapter as SearchSuggestionProductAdapter).updateData(searchSuggestionResponse.suggestProductArray.products)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        super.onError(e)
                                        mBinding.loader.visibility = View.GONE
                                    }
                                })
                    } else {
                        (mContext as BaseActivity).mCompositeDisposable.clear()
                        mBinding.loader.visibility = View.GONE
                    }
                    updateButtons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mBinding.etSearch.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Utils.showKeyboard(mBinding.etSearch)
            }
        }

        mBinding.searchLayout.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { nestedScrollView, _, scrollY, _, oldScrollY ->
            Utils.hideKeyboard(nestedScrollView)
        })
    }

    private fun updateButtons() {
        mCurrentQuery = mBinding.etSearch.text
        if (TextUtils.isEmpty(mCurrentQuery)) {
            displayVoiceButton(true)
            displayClearButton(false)
        } else {
            displayVoiceButton(false)
            displayClearButton(true)
        }
    }

    fun setupRecentSearchData() {
        if (BaseActivity.isDbInitialized()) {
            val recentSearchesList = mDataBaseHandler.getRecentSearchList(AppSharedPref.getStoreId(mContext))
            if (!AppSharedPref.getSearchHistoryEnabled(mContext) || recentSearchesList.isEmpty()) {
                mBinding.recentSearchesLayout.visibility = View.GONE
            } else {
                mBinding.recentSearchesLayout.visibility = View.VISIBLE
                if (mBinding.recentSearchRv.adapter == null) {
                    mBinding.recentSearchRv.addItemDecoration(DividerItemDecoration(mContext, LinearLayout.VERTICAL))
                }
                mBinding.recentSearchRv.adapter = RecentSearchesAdapter(this@MaterialSearchView, mDataBaseHandler.getRecentSearchList(AppSharedPref.getStoreId(mContext)))
            }
        }
    }

    private fun setupCategoryData() {
        val categoryList = AppSharedPref.getCategoryData(mContext)
        if (categoryList.isNotEmpty()) {
            mBinding.categoryLayout.visibility=View.VISIBLE
            mBinding.categoriesRv.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimension(R.dimen.spacing_tiny).toInt()))
            mBinding.categoriesRv.adapter = SearchViewCategoryRvAdapter(this@MaterialSearchView, categoryList)
        }else{
            mBinding.categoryLayout.visibility=View.GONE
        }
    }

    fun isOpen(): Boolean {
        return mOpen
    }

    fun openSearch() {
        if (!mOpen) {
            mBinding.etSearch.requestFocus()
            mOpen = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBinding.searchLayout.visibility = View.VISIBLE
                    AnimationHelper.circleRevealView(mBinding.searchBar)
                } else {
                    AnimationHelper.fadeInView(mBinding.searchLayout)
                }
                mBinding.materialSearchLayout.visibility = View.VISIBLE

                if (mContext is HomeActivity) {
                    val actionBarHeight = getStatusBarHeight()
                    if (actionBarHeight > 0) {
                        mBinding.materialSearchLayout.setPadding(0, actionBarHeight, 0, 0)
                    } else {
                        mBinding.materialSearchLayout.setPadding(0, 48, 0, 0)
                    }
                }
            } else {
                mBinding.searchLayout.visibility = View.VISIBLE
                mBinding.materialSearchLayout.visibility = View.VISIBLE

                (mContext as BaseActivity).supportActionBar?.hide()
            }
            setupCategoryData()
        }
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { (mContext as BaseActivity).window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

    fun closeSearch() {
        if (mOpen) {
            clearFocus()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                val listenerAdapter = object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mOpen = false
                        mBinding.searchLayout.visibility = View.GONE
                        mBinding.materialSearchLayout.visibility = View.GONE
                        (mContext as BaseActivity).supportActionBar?.show()
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimationHelper.circleHideView(mBinding.searchBar, listenerAdapter)
                } else {
                    AnimationHelper.fadeOutView(mBinding.searchLayout)
                }
            } else {
                mOpen = false
                mBinding.searchLayout.visibility = View.GONE
                mBinding.materialSearchLayout.visibility = View.GONE
                (mContext as BaseActivity).supportActionBar?.show()
            }
        }
    }

    fun setQuery(query: CharSequence?, submit: Boolean = false) {
        if (query != null) {
            BindingAdapterUtils.setLoadHtmlText(mBinding.etSearch, query.toString())
            mBinding.etSearch.setSelection(mBinding.etSearch.length())
            mCurrentQuery = query
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    private fun onSubmitQuery() {
        val query = mBinding.etSearch.text
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            FirebaseAnalyticsHelper.logSearchEvent(query.toString())
            val intent = Intent(mContext, CatalogActivity::class.java)
            intent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_SEARCH)
            intent.putExtra(BUNDLE_KEY_CATALOG_TITLE, query.toString())
            intent.putExtra(BUNDLE_KEY_CATALOG_ID, query.toString())
            mContext.startActivity(intent)
            if (AppSharedPref.getSearchHistoryEnabled(mContext)) {
                mDataBaseHandler.addRecentSearchQuery(AppSharedPref.getStoreId(mContext), query.toString())
            }
            closeSearch()
        }
    }

    fun onVoiceClicked() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mContext.getString(R.string.speak_an_item_name))

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, AppSharedPref.getStoreCode(mContext))
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, AppSharedPref.getStoreCode(mContext))
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, AppSharedPref.getStoreCode(mContext))
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, AppSharedPref.getStoreCode(mContext))

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_VOICE_RESULTS) // Quantity of results we want to receive
        (mContext as BaseActivity).startActivityForResult(intent, RC_VOICE)
    }

    private fun displayVoiceButton(display: Boolean) {
        if (display && Utils.isVoiceAvailable(mContext)) {
            mBinding.actionVoice.visibility = View.VISIBLE
        } else {
            mBinding.actionVoice.visibility = View.GONE
        }
    }

    private fun displayClearButton(display: Boolean) {
        mBinding.actionClear.visibility = if (display) View.VISIBLE else View.GONE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupRecentSearchData()
    }
}