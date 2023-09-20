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

package com.webkul.mobikul.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.FragmentProductOptionsBottomSheetBinding
import com.webkul.mobikul.helpers.ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_OPTIONS
import com.webkul.mobikul.models.user.OptionsItem

class ProductOptionsBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(productOptions: ArrayList<OptionsItem>): ProductOptionsBottomSheetFragment {
            val productOptionsBottomSheetFragment = ProductOptionsBottomSheetFragment()
            val args = Bundle()
            args.putParcelableArrayList(BUNDLE_KEY_PRODUCT_OPTIONS, productOptions)
            productOptionsBottomSheetFragment.arguments = args
            return productOptionsBottomSheetFragment
        }
    }

    lateinit var mContentViewBinding: FragmentProductOptionsBottomSheetBinding
    private var mProductOptions: ArrayList<OptionsItem> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_options_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProductOptions = requireArguments().getParcelableArrayList(BUNDLE_KEY_PRODUCT_OPTIONS)?:ArrayList()
        startInitialization()
    }

    private fun startInitialization() {
        for (eachOptionData in mProductOptions) {
            val tableRow = TableRow(context)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            tableRow.setPadding(0, 32, 0, 0)
            val labelTv = TextView(context)
            labelTv.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.3f)
            labelTv.text = eachOptionData.label
            labelTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_secondary))
            labelTv.textSize = 14f
            labelTv.typeface = Typeface.createFromAsset(resources.assets, CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
            tableRow.addView(labelTv)

            val valueTv = TextView(context)
            valueTv.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.7f)
            var value = eachOptionData.value?.get(0)
            for (noOfValues in 1 until (eachOptionData.value?.size ?: 0)) {
                value = value + "\n" + eachOptionData.value?.get(noOfValues)
            }
            valueTv.text = value
            valueTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            valueTv.textSize = 14f
            valueTv.gravity = Gravity.END
            valueTv.typeface = Typeface.createFromAsset(resources.assets, CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
            tableRow.addView(valueTv)

            mContentViewBinding.optionTableLayout.addView(tableRow)
        }
    }
}