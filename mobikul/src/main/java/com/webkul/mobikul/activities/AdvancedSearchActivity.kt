package com.webkul.mobikul.activities

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityAdvancedSearchBinding
import com.webkul.mobikul.handlers.AdvancedSearchActivityHandler
import com.webkul.mobikul.helpers.AlertDialogHelper
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.NetworkHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.models.catalog.AdvancedSearchFormModel
import com.webkul.mobikul.models.extra.AdvanceSearchFieldList
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*


class AdvancedSearchActivity : BaseActivity() {

    lateinit var mContentViewBinding: ActivityAdvancedSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_advanced_search)
        startInitialization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun startInitialization() {
        initSupportActionBar()
        callApi()
    }

    override fun initSupportActionBar() {
        supportActionBar?.title = getString(R.string.activity_title_advanced_search)
        super.initSupportActionBar()
    }

    private fun callApi() {
        mContentViewBinding.loading = true
        mHashIdentifier = Utils.getMd5String("getAdvanceSearchFormData" + AppSharedPref.getStoreId(this) + AppSharedPref.getCurrencyCode(this))
        ApiConnection.getAdvanceSearchFormData(this, mDataBaseHandler.getETagFromDatabase(mHashIdentifier))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : ApiCustomCallback<AdvancedSearchFormModel>(this, false) {
                    override fun onNext(advancedSearchFormModel: AdvancedSearchFormModel) {
                        super.onNext(advancedSearchFormModel)
                        mContentViewBinding.loading = false
                        if (advancedSearchFormModel.success) {
                            onSuccessfulResponse(advancedSearchFormModel)
                        } else {
                            onFailureResponse(advancedSearchFormModel)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        mContentViewBinding.loading = false
                        onErrorResponse(e)
                    }
                })
    }

    private fun onSuccessfulResponse(advancedSearchFormModel: AdvancedSearchFormModel) {
        mContentViewBinding.data = advancedSearchFormModel
        mContentViewBinding.handler = AdvancedSearchActivityHandler(this)
        setupAdvanceSearchForm(advancedSearchFormModel)
        setupScrollView()
    }

    private fun setupScrollView() {
        mContentViewBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY - oldScrollY < 0 || scrollY > mContentViewBinding.scrollView.getChildAt(0).height - mContentViewBinding.scrollView.height - 100) {
                mContentViewBinding.searchBtn.animate().alpha(1.0f).translationY(0f).interpolator = DecelerateInterpolator(1.4f)
            } else {
                mContentViewBinding.searchBtn.animate().alpha(0f).translationY(mContentViewBinding.searchBtn.height.toFloat()).interpolator = AccelerateInterpolator(1.4f)
            }
        })
    }

    private fun setupAdvanceSearchForm(advancedSearchFormModel: AdvancedSearchFormModel) {
        if (advancedSearchFormModel.fieldList.isNotEmpty()) {
            advancedSearchFormModel.fieldList.forEachIndexed { index, eachField ->
                when (eachField.inputType) {
                    "string" -> {
                        addStringTypeAttribute(index, eachField)
                    }
                    "price" -> {
                        addPriceTypeAttribute(index)
                    }
                    "date" -> {
                        addDateTypeAttribute(index)
                    }
                    "select" -> {
                        addSelectTypeAttribute(index, eachField)
                    }
                    "yesno" -> {

                    }
                    "button" -> {

                    }
                }
            }
//            for (eachField in advancedSearchFormModel.fieldList) {
//
//            }
        }
    }

    private fun addStringTypeAttribute(index: Int, eachField: AdvanceSearchFieldList) {
        val til = layoutInflater.inflate(R.layout.custom_input_layout, null) as TextInputLayout
        til.hint = eachField.label
        val editText = til.editText
        editText?.textSize = 16f
        editText?.tag = "inputType/$index"
        mContentViewBinding.advanceSearchFieldContainer.addView(til)
    }

    private fun addPriceTypeAttribute(index: Int) {
        val priceLinearLayout = LinearLayout(this)
        var priceLinearLayoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        priceLinearLayoutParams.setMargins(0, 16, 0, 0)
        priceLinearLayout.layoutParams = priceLinearLayoutParams
        priceLinearLayout.tag = "inputType/$index"
        priceLinearLayout.orientation = LinearLayout.HORIZONTAL
        priceLinearLayout.gravity = Gravity.CENTER_VERTICAL

        val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f

        val priceFromTil = layoutInflater.inflate(R.layout.custom_input_layout, null) as TextInputLayout
        priceFromTil.layoutParams = layoutParams
        priceFromTil.hint = resources.getString(R.string.price_from)

        val priceFromEditText = priceFromTil.editText
        priceFromEditText?.apply {
            id = R.id.price_from_et
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine()
            tag = "priceFrom"
        }


        priceLinearLayout.addView(priceFromTil)

        priceLinearLayout.addView(getDashView())

        val priceToTil = layoutInflater.inflate(R.layout.custom_input_layout, null) as TextInputLayout
        priceToTil.hint = resources.getString(R.string.price_to)
        priceToTil.layoutParams = layoutParams

        val priceToEditText = priceToTil.editText
        priceToEditText?.apply {
            id = R.id.price_to_et
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine()
            tag = "priceTo"
        }

        priceLinearLayout.addView(priceToTil)

        val currencyTv = TextView(this)
        currencyTv.textSize = 16f
        currencyTv.setPadding(10, 0, 10, 0)
        currencyTv.text = AppSharedPref.getCurrencyCode(this)
        currencyTv.setTextColor(ContextCompat.getColor(this, R.color.text_color_secondary))

        priceLinearLayout.addView(currencyTv)

        mContentViewBinding.advanceSearchFieldContainer.addView(priceLinearLayout)
    }

    private fun addDateTypeAttribute(index: Int) {
        val myCalendar = Calendar.getInstance()

        val dateLinearLayout = LinearLayout(this)
        dateLinearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dateLinearLayout.tag = "inputType/$index"
        dateLinearLayout.orientation = LinearLayout.HORIZONTAL
        dateLinearLayout.gravity = Gravity.CENTER_VERTICAL
        dateLinearLayout.setPadding(0, 30, 0, 0)

        val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f

        val dateFromTil = layoutInflater.inflate(R.layout.custom_input_layout, null) as TextInputLayout

        dateFromTil.layoutParams = layoutParams

        val editText3 =dateFromTil.editText
        editText3?.apply {
            setSingleLine()
            tag = "dateFrom"
            hint = resources.getString(R.string.date_from)
            textSize = 14f
            inputType = InputType.TYPE_NULL
            isFocusable = false
        }


        dateLinearLayout.addView(dateFromTil)

        val date1: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "MM/dd/yy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editText3?.setText(sdf.format(myCalendar.time))
        }
        editText3?.setOnClickListener { DatePickerDialog(this@AdvancedSearchActivity, R.style.AlertDialogTheme, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show() }

        dateLinearLayout.addView(getDashView())


        val dateToTil = layoutInflater.inflate(R.layout.custom_input_layout, null) as TextInputLayout
        dateToTil.layoutParams = layoutParams

        val editText4 = dateToTil.editText
        editText4?.apply {
            setSingleLine()
            tag = "dateTo"
            hint = resources.getString(R.string.date_to)
            textSize = 14f
            inputType = InputType.TYPE_NULL
            isFocusable = false
        }

        dateLinearLayout.addView(dateToTil)

        val fillGape = View(this)
        fillGape.layoutParams = LinearLayout.LayoutParams(0, ActionBar.LayoutParams.MATCH_PARENT, 1.0f)
        fillGape.setBackgroundColor(Color.WHITE)
        dateLinearLayout.addView(fillGape)

        val resetDateBtn = Button(this)
        resetDateBtn.tag = index
        resetDateBtn.text = resources.getString(R.string.reset_date)
        resetDateBtn.textSize = 14f

        resetDateBtn.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        resetDateBtn.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        resetDateBtn.setOnClickListener {
            (dateLinearLayout.findViewWithTag<View>("dateFrom") as TextInputEditText).setText("")
            (dateLinearLayout.findViewWithTag<View>("dateTo") as TextInputEditText).setText("")
        }
        dateLinearLayout.addView(resetDateBtn)

        val date2: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "MM/dd/yy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editText4?.setText(sdf.format(myCalendar.time))
        }

        editText4?.setOnClickListener { DatePickerDialog(this@AdvancedSearchActivity, R.style.AlertDialogTheme, date2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show() }
        mContentViewBinding.advanceSearchFieldContainer.addView(dateLinearLayout)
    }

    private fun getDashView(): TextView {
        val customTextViewDash = TextView(this)
        customTextViewDash.text = "   -   "
        customTextViewDash.textSize = 14f
        customTextViewDash.setTextColor(ContextCompat.getColor(this,R.color.text_color_secondary))
        return customTextViewDash
    }

    private fun addSelectTypeAttribute(index: Int, eachField: AdvanceSearchFieldList) {
        eachField.options.forEachIndexed { optionIndex, eachFieldOption ->
            val chk = CheckBox(this)
            chk.tag = "inputType/$index/check/$optionIndex"
            chk.text = eachFieldOption.label
            chk.textSize = 14f
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(chk, ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent)))//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                chk.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent))//setButtonTintList is accessible directly on API>19
            }
            chk.setTextColor(ContextCompat.getColor(this,R.color.text_color_primary))
            mContentViewBinding.advanceSearchFieldContainer.addView(chk)
        }
    }

    private fun onFailureResponse(advancedSearchFormModel: AdvancedSearchFormModel) {
        AlertDialogHelper.showNewCustomDialog(
                this,
                getString(R.string.error),
                advancedSearchFormModel.message,
                false,
                getString(R.string.try_again),
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    callApi()
                }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        })
    }

    private fun onErrorResponse(error: Throwable) {

        if ((!NetworkHelper.isNetworkAvailable(this@AdvancedSearchActivity) || (error is HttpException && error.code() == 304))) {
            checkLocalData(error)
        } else {
            AlertDialogHelper.showNewCustomDialog(
                    this@AdvancedSearchActivity,
                    getString(R.string.error),
                    NetworkHelper.getErrorMessage(this@AdvancedSearchActivity, error),
                    false,
                    getString(R.string.try_again),
                    { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        callApi()
                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                finish()
            })
        }

    }

    private fun checkLocalData(error: Throwable) {
        mDataBaseHandler.getResponseFromDatabaseOnThread(mHashIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<String> {
                    override fun onNext(response: String) {
                        if (response.isNotBlank()) {
                            onSuccessfulResponse(mObjectMapper.readValue(response, AdvancedSearchFormModel::class.java))
                        } else {
                            AlertDialogHelper.showNewCustomDialog(
                                    this@AdvancedSearchActivity,
                                    getString(R.string.error),
                                    NetworkHelper.getErrorMessage(this@AdvancedSearchActivity, error),
                                    false,
                                    getString(R.string.try_again),
                                    { dialogInterface: DialogInterface, _: Int ->
                                        dialogInterface.dismiss()
                                        callApi()
                                    }, getString(R.string.dismiss), { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                                finish()
                            })
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(disposable: Disposable) {
                        mCompositeDisposable.add(disposable)
                    }

                    override fun onComplete() {

                    }
                })

    }
}