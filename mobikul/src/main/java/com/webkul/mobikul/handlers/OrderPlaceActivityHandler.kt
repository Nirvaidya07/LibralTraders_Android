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

package com.webkul.mobikul.handlers

import android.content.Intent
import android.view.View
import com.webkul.mobikul.activities.HomeActivity
import com.webkul.mobikul.activities.OrderDetailsActivity
import com.webkul.mobikul.activities.OrderPlacedActivity
import com.webkul.mobikul.fragments.SignUpBottomSheetFragment
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_INCREMENT_ID
import com.webkul.mobikul.models.checkout.SaveOrderResponseModel


class OrderPlaceActivityHandler {

    fun onClickStartShopping(view: View) {
        val intent = Intent(view.context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        view.context.startActivity(intent)
    }

    fun onClickViewOrder(view: View, incrementId: String) {
        val intent = Intent(view.context, OrderDetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_INCREMENT_ID, incrementId)
        view.context.startActivity(intent)
    }

    fun onClickCreateAnAccount(view: View, saveOrderResponseModel: SaveOrderResponseModel) {
        val signUpBottomSheetFragment = SignUpBottomSheetFragment.newInstance(saveOrderResponseModel)
        signUpBottomSheetFragment.show((view.context as OrderPlacedActivity).supportFragmentManager, signUpBottomSheetFragment.tag)
    }
}