package com.webkul.mobikul.fragments

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.databinding.FragmentSortBottomSheetBinding
import com.webkul.mobikul.helpers.ApplicationConstants
import io.github.inflationx.calligraphy3.TypefaceUtils
import org.json.JSONArray
import org.json.JSONException


class SortBottomSheetFragment : BottomSheetDialogFragment() {

    private val SORTING_DATA_CODE_POSITION = "position"
    private val SORTING_DATA_CODE_NAME = "name"
    private val SORTING_DATA_CODE_PRICE = "price"

    private val DIRECTION_HIGH_TO_LOW = 1
    private val DIRECTION_LOW_TO_HIGH = 2

    lateinit var mContentViewBinding: FragmentSortBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sort_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        setupSortOptions()
        setupClickListener()
    }

    private fun setupSortOptions() {
        for (eachSortingData in (context as CatalogActivity).mContentViewBinding.data!!.sortingData) {
            val eachRb1 = RadioButton(context)
            eachRb1.text = String.format("%s %s", eachSortingData.label, getSuffix(eachSortingData.code, DIRECTION_LOW_TO_HIGH))
            eachRb1.textSize = 14f
            eachRb1.typeface = TypefaceUtils.load(requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
            eachRb1.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                eachRb1.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent));
            }
            eachRb1.highlightColor =ContextCompat.getColor(requireContext(),R.color.colorAccent)

            mContentViewBinding.sortRg.addView(eachRb1)

            val eachRb2 = RadioButton(context)
            eachRb2.text = String.format("%s %s", eachSortingData.label, getSuffix(eachSortingData.code, DIRECTION_HIGH_TO_LOW))
            eachRb2.textSize = 14f
            eachRb2.typeface = TypefaceUtils.load(requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
            eachRb2.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                eachRb2.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            }
            mContentViewBinding.sortRg.addView(eachRb2)

            /*SET SELECTION OF LAST SELECTED ITEM*/
            try {
                if ((context as CatalogActivity).mSortingInputJson.getString(0) == eachSortingData.code) {
                    if ((context as CatalogActivity).mSortingInputJson.getInt(1) == 0) {
                        eachRb1.isChecked = true
                    } else {
                        eachRb2.isChecked = true
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupClickListener() {
        mContentViewBinding.sortRg.setOnCheckedChangeListener { radioGroup: RadioGroup, checkedId: Int ->
            val selectedRbPosition = radioGroup.indexOfChild(radioGroup.findViewById(checkedId))
            (context as CatalogActivity).mSortingInputJson = JSONArray()
            (context as CatalogActivity).mSortingInputJson.put((context as CatalogActivity).mContentViewBinding.data!!.sortingData[selectedRbPosition / 2].code)
            (context as CatalogActivity).mSortingInputJson.put(selectedRbPosition % 2)
            (context as CatalogActivity).mPageNumber = 1
            (context as CatalogActivity).mContentViewBinding.isSorted = (context as CatalogActivity).mSortingInputJson.length() > 0
            (context as CatalogActivity).callApi()
            dismiss()
        }
    }

    private fun getSuffix(code: String, direction: Int): String {
        when (code) {
            SORTING_DATA_CODE_NAME -> return if (direction == DIRECTION_LOW_TO_HIGH) {
                getString(R.string.sort_by_suffix_a2z)
            } else {
                getString(R.string.sort_by_suffix_z2a)
            }

            SORTING_DATA_CODE_POSITION, SORTING_DATA_CODE_PRICE -> return if (direction == DIRECTION_LOW_TO_HIGH) {
                getString(R.string.sort_by_suffix_l2h)
            } else {
                getString(R.string.sort_by_suffix_h2l)
            }
            else -> return if (direction == DIRECTION_LOW_TO_HIGH) {
                getString(R.string.sort_by_suffix_l2h)
            } else {
                getString(R.string.sort_by_suffix_h2l)
            }
        }
    }
}
