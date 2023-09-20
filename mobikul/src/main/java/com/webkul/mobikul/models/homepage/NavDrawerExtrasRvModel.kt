package com.webkul.mobikul.models.homepage

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
class NavDrawerExtrasRvModel(var name: String, var drawableId: Int, var type: Int) {

    companion object {
        const val COMPARE_PRODUCTS = 0
        const val CONTACT_US = 1
        const val ORDERS_AND_RETURNS = 2
        const val ACCOUNT = 3
    }
}