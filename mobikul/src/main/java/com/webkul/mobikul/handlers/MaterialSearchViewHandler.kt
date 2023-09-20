package com.webkul.mobikul.handlers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.webkul.mobikul.activities.AdvancedSearchActivity
import com.webkul.mobikul.activities.BaseActivity.Companion.mDataBaseHandler
import com.webkul.mobikul.customviews.MaterialSearchView
import com.webkul.mobikul.fragments.CameraSearchChoiceDialogFragment
import com.webkul.mobikul.helpers.Utils

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

class MaterialSearchViewHandler(var mMaterialSearchView: MaterialSearchView) {

    fun backPressed() {
        mMaterialSearchView.closeSearch()
        Utils.hideKeyboard(mMaterialSearchView)
    }

    fun onImageSearchClicked() {
        CameraSearchChoiceDialogFragment().show((mMaterialSearchView.context as AppCompatActivity).supportFragmentManager, CameraSearchChoiceDialogFragment::class.java.simpleName)
    }

    fun onVoiceClicked() {
        mMaterialSearchView.onVoiceClicked()
    }

    fun clearSearch() {
        mMaterialSearchView.mBinding.etSearch.setText("")
    }

    fun onClickMakeAnAdvancedSearchBtn() {
        mMaterialSearchView.context.startActivity(Intent(mMaterialSearchView.context, AdvancedSearchActivity::class.java))
    }

    fun onClickClearAllRecentSearchesBtn() {
        mDataBaseHandler.clearRecentSearchData()
        mMaterialSearchView.setupRecentSearchData()
    }
}