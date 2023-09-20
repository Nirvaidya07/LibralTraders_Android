package com.webkul.mobikul.helpers

import android.content.Context
import android.widget.Toast
import com.webkul.mobikul.activities.BaseActivity

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

class ToastHelper {

    companion object {
        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            if (message.isNotEmpty()) {
                (context as BaseActivity).mToast?.cancel()
                context.mToast = Toast.makeText(context, message, duration)
                context.mToast!!.show()
            }
        }

        fun dismiss(context: Context) {
            (context as BaseActivity).mToast?.cancel()
        }
    }
}