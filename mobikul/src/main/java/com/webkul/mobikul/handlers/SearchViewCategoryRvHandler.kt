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
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.activities.SubCategoryActivity
import com.webkul.mobikul.customviews.MaterialSearchView
import com.webkul.mobikul.helpers.BundleKeysHelper


class SearchViewCategoryRvHandler(private val mMaterialSearchView: MaterialSearchView) {

    fun onClickCategory(name: String, id: String, hasChildren: Boolean) {
        val intent: Intent
        if (hasChildren) {
            intent = Intent(mMaterialSearchView.context, SubCategoryActivity::class.java)
        } else {
            intent = Intent(mMaterialSearchView.context, CatalogActivity::class.java)
        }
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE, BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CATEGORY)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE, name)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_ID, id)
        mMaterialSearchView.context.startActivity(intent)
    }
}