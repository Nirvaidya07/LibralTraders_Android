package com.webkul.mobikul.activities

import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityWishListSharingBinding
import com.webkul.mobikul.handlers.WishListSharingActivityHandler
import com.webkul.mobikul.models.user.WishListShareFormModel

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

class WishListSharingActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityWishListSharingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list_sharing)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        mContentViewBinding.data = WishListShareFormModel()
        mContentViewBinding.handler = WishListSharingActivityHandler(this)
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_wish_list_sharing)
        super.initSupportActionBar()
    }
}