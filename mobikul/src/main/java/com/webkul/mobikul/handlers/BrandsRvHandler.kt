package com.webkul.mobikul.handlers

import android.content.Context
import android.content.Intent
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.helpers.BundleKeysHelper

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

class BrandsRvHandler(val mContext: Context, val mCategoryId: String?) {

    fun onClickSeller(brandId: String, brandCode: String, label: String) {
        val intent = Intent(mContext, CatalogActivity::class.java)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE, BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_CATEGORY)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE, label)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_ID, mCategoryId)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_BRAND_CODE, brandCode)
        intent.putExtra(BundleKeysHelper.BUNDLE_KEY_CATALOG_BRAND_ID, brandId)
        mContext.startActivity(intent)
    }
}