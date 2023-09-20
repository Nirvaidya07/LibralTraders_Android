package com.webkul.mobikul.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.CatalogActivity
import com.webkul.mobikul.databinding.FragmentFilterBottomSheetBinding
import com.webkul.mobikul.databinding.ItemCatalogSelectedFilterBinding
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.catalog.LayeredData
import io.github.inflationx.calligraphy3.TypefaceUtils
import org.json.JSONArray


class FilterBottomSheetFragment : BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener {

    companion object {
        var sAllLayeredDatas: ArrayList<LayeredData> = ArrayList()
    }

    lateinit var mContentViewBinding: FragmentFilterBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_bottom_sheet, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        setupFilterOptions()
        loadSelectedFiltersData()
    }

    private fun setupFilterOptions() {
        sAllLayeredDatas.addAll((context as CatalogActivity).mContentViewBinding.data!!.layeredData)
        val labelTopPadding = Utils.convertDpToPixel(requireContext().resources.getDimension(R.dimen.spacing_generic), context as Context).toInt()
        for (filterDataIdx in (context as CatalogActivity).mContentViewBinding.data!!.layeredData.indices) {
            val eachFilterData = (context as CatalogActivity).mContentViewBinding.data!!.layeredData[filterDataIdx]

            if (eachFilterData.options.isNotEmpty()) {
                val eachFilterDataLabel = TextView(context)
                eachFilterDataLabel.text = eachFilterData.label
                eachFilterDataLabel.textSize = 16f
                eachFilterDataLabel.typeface = TypefaceUtils.load(requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
                eachFilterDataLabel.setTextColor(ContextCompat.getColor(context as CatalogActivity, R.color.text_color_secondary))
                eachFilterDataLabel.setTypeface(eachFilterDataLabel.typeface, Typeface.BOLD)
                if (filterDataIdx != 0) {
                    eachFilterDataLabel.setPadding(0, labelTopPadding, 0, 0)
                }
                mContentViewBinding.filterContainer.addView(eachFilterDataLabel)
                val view = View(context)
                view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.convertDpToPixel(1.0f, context as Context).toInt())
                view.setBackgroundColor(ContextCompat.getColor(context as CatalogActivity, R.color.divider_color))
                mContentViewBinding.filterContainer.addView(view)

                val eachFilterRg = RadioGroup(context)
                eachFilterRg.tag = eachFilterData.code
                for (eachFilterDataOptions in eachFilterData.options) {
                    val eachFilterDataOptionRb = RadioButton(context)
                    eachFilterDataOptionRb.tag = eachFilterDataOptions.id
                    eachFilterDataOptionRb.text = "${eachFilterDataOptions.label} (${eachFilterDataOptions.count})"
                    eachFilterDataOptionRb.textSize = 14f
                    eachFilterDataOptionRb.typeface = TypefaceUtils.load(requireContext().assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_REGULAR)
                    eachFilterDataOptionRb.setTextColor(ContextCompat.getColor(context as CatalogActivity, R.color.text_color_primary))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        eachFilterDataOptionRb.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                    }
                    eachFilterRg.addView(eachFilterDataOptionRb)
                }
                eachFilterRg.setOnCheckedChangeListener(this)
                mContentViewBinding.filterContainer.addView(eachFilterRg)
            }
        }
    }

    override fun onCheckedChanged(radioGroup: RadioGroup?, checkedId: Int) {
        val selectedRadioBtn = radioGroup!!.findViewById<RadioButton>(checkedId)
        (context as CatalogActivity).mSellerAttributesIdOptionCodeMap[radioGroup.tag.toString()] = selectedRadioBtn.tag.toString()
        updateFilterInputData()
    }

    private fun updateFilterInputData() {
        (context as CatalogActivity).mFilterInputJson = JSONArray()
        val selectedAttributeIdArr = JSONArray()
        val selectedAttributeCodeArr = JSONArray()

        for (pair in (context as CatalogActivity).mSellerAttributesIdOptionCodeMap.entries) {
            selectedAttributeIdArr.put(pair.key)
            selectedAttributeCodeArr.put(pair.value)
        }

        (context as CatalogActivity).mFilterInputJson.put(selectedAttributeCodeArr)
        (context as CatalogActivity).mFilterInputJson.put(selectedAttributeIdArr)
        (context as CatalogActivity).mPageNumber = 1
        (context as CatalogActivity).mContentViewBinding.isFiltered = (context as CatalogActivity).mFilterInputJson.length() > 0
        (context as CatalogActivity).callApi()
        dismiss()
    }

    private fun loadSelectedFiltersData() {
        if ((context as CatalogActivity).mSellerAttributesIdOptionCodeMap.size != 0) {
            for (pair in (context as CatalogActivity).mSellerAttributesIdOptionCodeMap.entries) {
                val itemCatalogSelectedFilter = DataBindingUtil.inflate<ItemCatalogSelectedFilterBinding>(LayoutInflater.from(context), R.layout.item_catalog_selected_filter, mContentViewBinding.selectedFiltersLayout, true)
                itemCatalogSelectedFilter.selectedOption = getSelectedFilterContent(pair)
                itemCatalogSelectedFilter.clearFilterIv.setOnClickListener {
                    (context as CatalogActivity).mSellerAttributesIdOptionCodeMap.entries.remove(pair)
                    updateFilterInputData()
                }
            }

            mContentViewBinding.selectedFiltersLayout.visibility = View.VISIBLE
            mContentViewBinding.clearAll.visibility = View.VISIBLE

            mContentViewBinding.clearAll.setOnClickListener { clearAllFilters() }
        }
    }

    private fun getSelectedFilterContent(pair: MutableMap.MutableEntry<String, String>): String {
        return getLayeredDataAttributeLabel(sAllLayeredDatas, pair) + " : " + getLayeredDataOptionLabel(sAllLayeredDatas, pair);
    }

    private fun getLayeredDataAttributeLabel(mLayeredDatas: ArrayList<LayeredData>, pair: MutableMap.MutableEntry<String, String>): String {
        for (layeredData in mLayeredDatas) {
            if (layeredData.code == pair.key) {
                return layeredData.label
            }
        }
        return ""
    }

    private fun getLayeredDataOptionLabel(mLayeredDatas: ArrayList<LayeredData>, pair: MutableMap.MutableEntry<String, String>): String {
        for (layeredData in mLayeredDatas) {
            if (layeredData.code == pair.key) {
                for (layeredDataOption in layeredData.options) {
                    if (layeredDataOption.id == pair.value) {
                        return layeredDataOption.label
                    }
                }
            }
        }
        return ""
    }

    private fun clearAllFilters() {
        mContentViewBinding.selectedFiltersLayout.visibility = View.GONE
        mContentViewBinding.clearAll.visibility = View.GONE
        sAllLayeredDatas = ArrayList()
        (context as CatalogActivity).mFilterInputJson = JSONArray()
        (context as CatalogActivity).mSellerAttributesIdOptionCodeMap.clear()
        (context as CatalogActivity).mPageNumber = 1
        (context as CatalogActivity).mContentViewBinding.isFiltered = (context as CatalogActivity).mFilterInputJson.length() > 0
        (context as CatalogActivity).callApi()
        dismiss()
    }
}