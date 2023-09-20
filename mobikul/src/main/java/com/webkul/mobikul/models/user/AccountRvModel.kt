package com.webkul.mobikul.models.user

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

class AccountRvModel(var type: Int, var name: String = "", var drawableId: Int = 0) {

    companion object {
        const val DASHBOARD = 101
        const val WISH_LIST = 102
        const val ORDERS = 103
        const val DOWNLOADABLE_PRODUCTS = 104
        const val PRODUCT_REVIEWS = 105
        const val ADDRESS_BOOK = 106
        const val ACCOUNT_INFORMATION = 107
        const val QR_CODE_LOGIN = 108
        const val ZOPIM_CHAT = 109
    }
}