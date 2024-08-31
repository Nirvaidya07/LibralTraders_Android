package com.webkul.mobikul.activities

import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityCategoryBinding
import com.webkul.mobikul.fragments.SubCategoryFragment
import com.webkul.mobikul.helpers.BundleKeysHelper


class CategoryActivity : BaseActivity() {
    lateinit var mContentViewBinding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_category)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        setToolbar()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container,
                SubCategoryFragment.newInstance(intent?.getParcelableArrayListExtra(BundleKeysHelper.BUNDLE_KEY_HOME_PAGE_DATA)), SubCategoryFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.categories)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        super.initSupportActionBar()
    }
}