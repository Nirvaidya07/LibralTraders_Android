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

package com.webkul.mobikul.helpers

class AuthKeyHelper {

    companion object {
        private var mInstance: AuthKeyHelper? = null

        @JvmStatic
        fun getInstance(): AuthKeyHelper {
            if (mInstance == null) {
                mInstance = AuthKeyHelper()
            }
            return mInstance as AuthKeyHelper
        }
    }

    var authKey = ""
    var token: String? = ""
        get() = field ?: ""
}