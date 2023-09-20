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

import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.customviews.MaterialSearchView


class RecentSearchesHandler(private val mMaterialSearchView: MaterialSearchView) {

    fun onClickSearch(query: String) {
        mMaterialSearchView.setQuery(query)
    }

    fun onClickDeleteSearch(query: String) {
        BaseActivity.mDataBaseHandler.deleteParticularRecentSearch(query)
        mMaterialSearchView.setupRecentSearchData()
    }
}