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

import android.content.Context
import android.text.Html
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_DOMINANT_COLOR
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.MobikulApplication


class SearchSuggestionProductHandler(val mContext: Context) {

    fun onProductSelected(id: String, name: String, dominantColor: String) {
        val intent = (mContext?.applicationContext as MobikulApplication).getProductDetailsActivity(mContext!!)
        intent.putExtra(BUNDLE_KEY_PRODUCT_ID, id)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, android.text.Html.fromHtml(name, Html.FROM_HTML_MODE_LEGACY).toString())
        } else {
            intent.putExtra(BUNDLE_KEY_PRODUCT_NAME, android.text.Html.fromHtml(name).toString())
        }
        intent.putExtra(BUNDLE_KEY_PRODUCT_DOMINANT_COLOR, dominantColor)
        mContext.startActivity(intent)
    }
}