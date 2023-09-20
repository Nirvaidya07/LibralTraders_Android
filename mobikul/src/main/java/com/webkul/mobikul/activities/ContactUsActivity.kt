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

import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityContactUsBinding
import com.webkul.mobikul.handlers.ContactUsActivityHandler
import com.webkul.mobikul.helpers.AppSharedPref

class ContactUsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        setupAutoFillData()

        mContentViewBinding.handler = ContactUsActivityHandler(this)
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_contact_us)
        super.initSupportActionBar()
    }

    private fun setupAutoFillData() {
        if (AppSharedPref.isLoggedIn(this)) {
            mContentViewBinding.name.setText(AppSharedPref.getCustomerName(this))
            mContentViewBinding.email.setText(AppSharedPref.getCustomerEmail(this))
            mContentViewBinding.msg.requestFocus()
        }
    }
}