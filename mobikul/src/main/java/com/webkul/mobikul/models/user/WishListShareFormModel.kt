package com.webkul.mobikul.models.user

import androidx.databinding.BaseObservable
import com.libraltraders.android.R
import com.webkul.mobikul.activities.WishListSharingActivity
import com.webkul.mobikul.helpers.Utils.Companion.showShakeError

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

class WishListShareFormModel : BaseObservable() {

    var emails: String = ""
    var message: String = ""

    fun isFormValidated(context: WishListSharingActivity): Boolean {
        var isFormValidated = true

        /* Checking Emails */
        if (emails.isBlank()) {
            isFormValidated = false
            context.mContentViewBinding.email.error = context.getString(R.string.email) + " " + context.getString(R.string.is_required)
            showShakeError(context, context.mContentViewBinding.email)
            context.mContentViewBinding.email.requestFocus()
        } else {
            context.mContentViewBinding.email.isErrorEnabled = false
            context.mContentViewBinding.email.error = null
        }

        val emailsList = emails.split(",")
        for (eachEmail in emailsList) {
            if (!validEmail(eachEmail)) {
                isFormValidated = false
                context.mContentViewBinding.email.error = context.getString(R.string.enter_a_valid) + " " + context.getString(R.string.email)
                showShakeError(context, context.mContentViewBinding.email)
                context.mContentViewBinding.email.requestFocus()
                break
            }
        }
        return isFormValidated
    }

    private fun validEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}