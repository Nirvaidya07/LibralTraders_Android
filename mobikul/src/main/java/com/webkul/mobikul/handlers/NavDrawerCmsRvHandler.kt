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
import com.webkul.mobikul.activities.CmsPageActivity
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CMS_PAGE_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CMS_PAGE_TITLE


class NavDrawerCmsRvHandler {

    fun onClickItem(view: View, id: String, title: String) {
        val intent = Intent(view.context, CmsPageActivity::class.java)
        intent.putExtra(BUNDLE_KEY_CMS_PAGE_ID, id)
        intent.putExtra(BUNDLE_KEY_CMS_PAGE_TITLE, title)
        view.context.startActivity(intent)
    }
}