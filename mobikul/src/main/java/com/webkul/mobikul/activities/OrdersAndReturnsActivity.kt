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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.ActivityOrdersAndReturnsBinding
import com.webkul.mobikul.handlers.OrdersAndReturnsActivityHandler
import java.util.*

class OrdersAndReturnsActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityOrdersAndReturnsBinding
    var mOrderType: String = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_orders_and_returns)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        setUpOrderBy()

        mContentViewBinding.handler = OrdersAndReturnsActivityHandler(this)
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_orders_and_return)
        super.initSupportActionBar()
    }

    private fun setUpOrderBy() {
        val orderBySpinnerData = ArrayList<String>()
        orderBySpinnerData.add(getString(R.string.email))
        orderBySpinnerData.add(getString(R.string.zip_code))


        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.custom_spinner_item,
            orderBySpinnerData)

        mContentViewBinding.orderBySpinner.setAdapter(adapter)

        mContentViewBinding.orderBySpinner.onItemClickListener = AdapterView.OnItemClickListener { parent, arg1, position, id ->
            if (position == 0) {
                mOrderType = "email"
                mContentViewBinding.emailTil.visibility = View.VISIBLE
                mContentViewBinding.zipTil.visibility = View.GONE
            } else {
                mOrderType = "zip"
                mContentViewBinding.emailTil.visibility = View.GONE
                mContentViewBinding.zipTil.visibility = View.VISIBLE
            }
        }

    }
}
