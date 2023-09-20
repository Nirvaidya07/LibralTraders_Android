/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import android.content.Intent
import com.webkul.mobikul.activities.AddEditAddressActivity
import com.webkul.mobikul.activities.AddressBookActivity
import com.webkul.mobikul.fragments.DashboardAddressesFragment
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.ConstantsHelper.RC_ADD_EDIT_ADDRESS


class DashboardAddressesFragmentHandler(private val mFragmentContext: DashboardAddressesFragment) {

    fun onClickViewAll() {
        mFragmentContext.startActivityForResult(Intent(mFragmentContext.context, AddressBookActivity::class.java), RC_ADD_EDIT_ADDRESS)
    }

    fun onClickAddress(addressId: String) {
        val intent = Intent(mFragmentContext.context, AddEditAddressActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_ADDRESS_ID, if (addressId == "0") "" else addressId)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_ADDRESS_COUNT, mFragmentContext.mContentViewBinding.data!!.addressCount)
        mFragmentContext.startActivityForResult(intent, RC_ADD_EDIT_ADDRESS)
    }
}