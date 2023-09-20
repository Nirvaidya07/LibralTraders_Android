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
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_SEARCH


class SearchTermsActivityHandler {

    fun onClickSearchItem(view: View, term: String) {
        val intent = Intent(view.context, CatalogActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_SEARCH)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE, term)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_ID, term)
        view.context.startActivity(intent)
    }
}