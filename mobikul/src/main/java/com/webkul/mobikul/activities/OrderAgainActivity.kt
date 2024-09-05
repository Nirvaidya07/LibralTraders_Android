package com.webkul.mobikul.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityOrderAgainBinding

class OrderAgainActivity : BaseActivity() {

    private lateinit var mContentViewBinding: ActivityOrderAgainBinding
    lateinit var mMenuItemMoreOptions: MenuItem
    lateinit var mNavigationIconClickListener: OrderDetailsActivity.NavigationIconClickListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //binding = ActivityOrderAgainBinding.inflate(layoutInflater)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_again)
        //setContentView(binding.root)

    }

}