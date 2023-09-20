package com.webkul.mobikul.handlers

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.AdvancedSearchActivity
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TITLE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CATALOG_TYPE_ADV_SEARCH
import org.json.JSONObject

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

class AdvancedSearchActivityHandler(val mContext: AdvancedSearchActivity) {

    private var mAdvanceSearchInputJSONObject: JSONObject = JSONObject()

    fun onClickSearchBtn() {
        mAdvanceSearchInputJSONObject = JSONObject()
        var isAnyOptionSelected = false

        mContext.mContentViewBinding.data!!.fieldList.forEachIndexed { index, eachField ->
            when (eachField.inputType) {
                "string" -> {
                    val eT = mContext.mContentViewBinding.advanceSearchFieldContainer.findViewWithTag("inputType/$index") as TextInputEditText
                    if (eT.text.toString().trim { it <= ' ' } != "") {
                        isAnyOptionSelected = true
                    }
                    val stringValue = JSONObject()
                    stringValue.put("code", eachField.attributeCode)
                    stringValue.put("value", eT.text.toString())
                    stringValue.put("inputType", "string")
                    mAdvanceSearchInputJSONObject.put(index.toString(), stringValue)
                }
                "price" -> {
                    val priceLinearLayout = mContext.mContentViewBinding.advanceSearchFieldContainer.findViewWithTag("inputType/$index") as LinearLayout
                    val priceFromEt = priceLinearLayout.findViewWithTag<View>("priceFrom") as TextInputEditText
                    val priceToEt = priceLinearLayout.findViewWithTag<View>("priceTo") as TextInputEditText

                    if (!(priceFromEt.text.toString().trim { it <= ' ' } == "" || priceToEt.text.toString().trim { it <= ' ' } == "")) {
                        isAnyOptionSelected = true
                    }
                    val priceValue = JSONObject()
                    priceValue.put("code", eachField.attributeCode)
                    priceValue.put("inputType", "price")

                    val priceRange = JSONObject()
                    priceRange.put("from", priceFromEt.text.toString().trim { it <= ' ' })
                    priceRange.put("to", priceToEt.text.toString().trim { it <= ' ' })

                    priceValue.put("value", priceRange)
                    mAdvanceSearchInputJSONObject.put(index.toString(), priceValue)
                }
                "date" -> {
                    val dateLinearLayout = mContext.mContentViewBinding.advanceSearchFieldContainer.findViewWithTag("inputType/$index") as LinearLayout
                    val dateFromEt = dateLinearLayout.findViewWithTag<View>("dateFrom") as TextInputEditText
                    val dateToEt = dateLinearLayout.findViewWithTag<View>("dateTo") as TextInputEditText

                    if (!(dateFromEt.text.toString().trim { it <= ' ' } == "" || dateToEt.text.toString().trim { it <= ' ' } == "")) {
                        isAnyOptionSelected = true
                    }
                    val dateValue = JSONObject()
                    dateValue.put("code", eachField.attributeCode)
                    dateValue.put("inputType", "date")

                    val dateRange = JSONObject()
                    dateRange.put("from", dateFromEt.text.toString().trim { it <= ' ' })
                    dateRange.put("to", dateToEt.text.toString().trim { it <= ' ' })

                    dateValue.put("value", dateRange)
                    mAdvanceSearchInputJSONObject.put(index.toString(), dateValue)
                }
                "select" -> {
                    val temp5 = JSONObject()
                    val temp6 = JSONObject()
                    for (noOfOption in 0 until eachField.options.size) {
                        val chk = mContext.mContentViewBinding.advanceSearchFieldContainer.findViewWithTag("inputType/$index/check/$noOfOption") as CheckBox
                        if (chk.isChecked) {
                            temp6.put(eachField.options[noOfOption].value, "true")
                            isAnyOptionSelected = true
                        } else {
                            temp6.put(eachField.options[noOfOption].value, "false")
                        }
                    }
                    temp5.put("code", eachField.attributeCode)
                    temp5.put("inputType", "select")
                    temp5.put("value", temp6)
                    mAdvanceSearchInputJSONObject.put("" + index, temp5)
                }
                "yesno" -> {

                }
                "button" -> {

                }
            }
        }

        if (isAnyOptionSelected) {
            handleResponseObject()
        } else {
            AlertDialogHelper.showNewCustomDialog(mContext
                    , mContext.getString(R.string.advance_search_nothing_selected)
                    , mContext.getString(R.string.advance_search_please_select_one_option_message)
                    , true
                    , mContext.getString(R.string.ok)
                    , null)
        }
    }

    private fun handleResponseObject() {
        val advancedSearchResultsIntent = Intent(mContext, CatalogActivity::class.java)
        advancedSearchResultsIntent.putExtra(BUNDLE_KEY_CATALOG_TYPE, BUNDLE_KEY_CATALOG_TYPE_ADV_SEARCH)
        advancedSearchResultsIntent.putExtra(BUNDLE_KEY_CATALOG_TITLE, mContext.resources.getString(R.string.activity_title_advanced_search))
        advancedSearchResultsIntent.putExtra(BUNDLE_KEY_CATALOG_ID, mAdvanceSearchInputJSONObject.toString())
        mContext.startActivity(advancedSearchResultsIntent)
    }
}