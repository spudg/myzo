package com.spudg.spudgmoneymanager

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recurrings.*
import kotlinx.android.synthetic.main.day_month_year_picker.*
import kotlinx.android.synthetic.main.dialog_add_recurring.*
import kotlinx.android.synthetic.main.dialog_add_recurring.etAmountLayoutRecurring
import kotlinx.android.synthetic.main.dialog_add_recurring.tvCancelRecurring
import kotlinx.android.synthetic.main.dialog_add_recurring.view.*
import kotlinx.android.synthetic.main.dialog_add_recurring.view.etAmountRecurring
import kotlinx.android.synthetic.main.dialog_add_transaction.*
import kotlinx.android.synthetic.main.dialog_add_transaction.view.*
import kotlinx.android.synthetic.main.dialog_add_transaction.view.etAmount
import kotlinx.android.synthetic.main.dialog_add_transaction.view.expenditure_radio
import kotlinx.android.synthetic.main.dialog_add_transaction.view.income_radio
import kotlinx.android.synthetic.main.dialog_delete_recurring.*
import kotlinx.android.synthetic.main.dialog_delete_transaction.*
import kotlinx.android.synthetic.main.dialog_update_recurring.*
import kotlinx.android.synthetic.main.dialog_update_recurring.view.*
import kotlinx.android.synthetic.main.dialog_update_transaction.*
import kotlinx.android.synthetic.main.dialog_update_transaction.etAmountLayout
import kotlinx.android.synthetic.main.dialog_update_transaction.inc_exp_radio_group
import kotlinx.android.synthetic.main.dialog_update_transaction.tvCancel
import kotlinx.android.synthetic.main.dialog_update_transaction.view.*
import java.util.*

class RecurringsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var isIncome = true
    private var selectedCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurrings)

        setUpRecurringList()

        add_recurring.setOnClickListener {
            addRecurring()
        }

        back_to_trans_from_recurrings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setUpRecurringList() {
        if (getRecurringsList().size > 0) {
            rvRecurrings.visibility = View.VISIBLE
            tvNoRecurrings.visibility = View.GONE
            var manager = LinearLayoutManager(this)
            rvRecurrings.layoutManager = manager
            val recurringAdapter = RecurringAdapter(this, getRecurringsList())
            rvRecurrings.adapter = recurringAdapter
        } else {
            llRecurrings.visibility = View.GONE
            tvNoRecurrings.visibility = View.VISIBLE
        }
    }

    private fun getRecurringsList(): ArrayList<RecurringModel> {
        val dbHandler = RecurringsHandler(this, null)
        val result = dbHandler.filterRecurrings(-1)
        dbHandler.close()
        return result
    }

    private fun addRecurring() {
        val addDialog = Dialog(this, R.style.Theme_Dialog)
        addDialog.setCancelable(false)
        addDialog.setContentView(R.layout.dialog_add_recurring)
        addDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        var monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
        var yearPicked = Calendar.getInstance()[Calendar.YEAR]

        addDialog.change_date_add_recurring.text =
            "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"

        addDialog.change_date_add_recurring.setOnClickListener {
            val changeDateDialog = Dialog(this, R.style.Theme_Dialog)
            changeDateDialog.setCancelable(false)
            changeDateDialog.setContentView(R.layout.day_month_year_picker)
            changeDateDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 4 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 6 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 9 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 11) {
                changeDateDialog.dmyp_day.maxValue = 30
                changeDateDialog.dmyp_day.minValue = 1
            } else if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 2 && (Calendar.getInstance()[Calendar.DAY_OF_MONTH] % 4 == 0)) {
                changeDateDialog.dmyp_day.maxValue = 29
                changeDateDialog.dmyp_day.minValue = 1
            } else if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 2 && (Calendar.getInstance()[Calendar.DAY_OF_MONTH] % 4 != 0)) {
                changeDateDialog.dmyp_day.maxValue = 28
                changeDateDialog.dmyp_day.minValue = 1
            } else {
                changeDateDialog.dmyp_day.maxValue = 31
                changeDateDialog.dmyp_day.minValue = 1
            }

            changeDateDialog.dmyp_month.maxValue = 12
            changeDateDialog.dmyp_month.minValue = 1
            changeDateDialog.dmyp_year.maxValue = 2999
            changeDateDialog.dmyp_year.minValue = 1000

            changeDateDialog.dmyp_day.value = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            changeDateDialog.dmyp_month.value = Calendar.getInstance()[Calendar.MONTH] + 1
            changeDateDialog.dmyp_year.value = Calendar.getInstance()[Calendar.YEAR]
            dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
            yearPicked = Calendar.getInstance()[Calendar.YEAR]

            changeDateDialog.dmyp_month.displayedValues = Constants.MONTHS_SHORT_ARRAY

            changeDateDialog.dmyp_day.setOnValueChangedListener { _, _, newVal ->
                dayPicked = newVal
            }

            changeDateDialog.dmyp_month.setOnValueChangedListener { _, _, newVal ->
                if (newVal == 4 || newVal == 6 || newVal == 9 || newVal == 11) {
                    changeDateDialog.dmyp_day.maxValue = 30
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal == 2 && (changeDateDialog.dmyp_year.value % 4 == 0)) {
                    changeDateDialog.dmyp_day.maxValue = 29
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal == 2 && (changeDateDialog.dmyp_year.value % 4 != 0)) {
                    changeDateDialog.dmyp_day.maxValue = 28
                    changeDateDialog.dmyp_day.minValue = 1
                } else {
                    changeDateDialog.dmyp_day.maxValue = 31
                    changeDateDialog.dmyp_day.minValue = 1
                }
                monthPicked = newVal
            }

            changeDateDialog.dmyp_year.setOnValueChangedListener { _, _, newVal ->
                if (newVal % 4 == 0 && changeDateDialog.dmyp_month.value == 2) {
                    changeDateDialog.dmyp_day.maxValue = 29
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal % 4 != 0 && changeDateDialog.dmyp_month.value == 2) {
                    changeDateDialog.dmyp_day.maxValue = 28
                    changeDateDialog.dmyp_day.minValue = 1
                }
                yearPicked = newVal
            }

            changeDateDialog.submit_dmy.setOnClickListener {
                addDialog.change_date_add_recurring.text =
                    "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            changeDateDialog.dmyp_day.wrapSelectorWheel = true
            changeDateDialog.dmyp_month.wrapSelectorWheel = true
            changeDateDialog.dmyp_year.wrapSelectorWheel = true

            changeDateDialog.cancel_dmy.setOnClickListener {
                dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
                monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
                yearPicked = Calendar.getInstance()[Calendar.YEAR]
                addDialog.change_date_add_recurring.text =
                    "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            changeDateDialog.show()

        }


        addDialog.etAmountLayoutRecurring.etAmountRecurring.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {
                val str = addDialog.etAmountLayoutRecurring.etAmountRecurring.text.toString()
                if (str.isEmpty()) return
                val str2: String = currencyInputFilter(str, 6, 2)
                if (str2 != str) {
                    addDialog.etAmountLayoutRecurring.etAmountRecurring.setText(str2)
                    addDialog.etAmountLayoutRecurring.etAmountRecurring.setSelection(str2.length)
                }
            }
        })

        val categoryListHandler = CategoriesHandler(this, null)
        val items = categoryListHandler.getAllCategoryTitles()
        categoryListHandler.close()
        val categoryAdapter = ArrayAdapter(this, R.layout.custom_spinner, items)
        addDialog.category_spinner_add_trans_recurring.adapter = categoryAdapter
        addDialog.category_spinner_add_trans_recurring.onItemSelectedListener = this

        addDialog.inc_exp_radio_group_recurring_add.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.income_radio_recurring_add -> {
                    isIncome = true
                }
                R.id.expenditure_radio_recurring_add -> {
                    isIncome = false
                }
                else -> {
                    Toast.makeText(
                        this,
                        "An error has occurred. Please try restarting the app.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        addDialog.tvAddRecurring.setOnClickListener {

            Constants.TRANSACTIONS_ADDED_SESSION += 1

            val dbHandlerRec = RecurringsHandler(this, null)
            val dbHandlerCat = CategoriesHandler(this, null)

            val category = dbHandlerCat.getCategoryId(selectedCategory)
            val amount = addDialog.etAmountLayoutRecurring.etAmountRecurring.text.toString()
            val note = addDialog.etNoteLayoutAddRecurring.etNoteAddRecurring.text.toString()
            val account = Constants.CURRENT_ACCOUNT
            val nextMonth = monthPicked
            val nextOGDay = dayPicked
            val nextDay = dayPicked
            val nextYear = yearPicked

            if (selectedCategory.isNotEmpty() && amount.isNotEmpty() && note.isNotEmpty()) {
                if (isIncome) {
                    dbHandlerRec.addRecurring(
                        RecurringModel(
                            0,
                            note,
                            category,
                            amount,
                            account,
                            nextMonth,
                            nextOGDay,
                            nextDay,
                            nextYear,
                            ""
                        )
                    )
                } else if (!isIncome) {
                    dbHandlerRec.addRecurring(
                        RecurringModel(
                            0,
                            note,
                            category,
                            (amount.toDouble() * -1).toString(),
                            account,
                            nextMonth,
                            nextOGDay,
                            nextDay,
                            nextYear,
                            ""
                        )
                    )
                }

                Toast.makeText(this, "Recurring transaction added.", Toast.LENGTH_LONG).show()
                setUpRecurringList()
                addDialog.dismiss()

            } else {
                Toast.makeText(this, "Category, amount or note can't be blank.", Toast.LENGTH_LONG)
                    .show()
            }

            dbHandlerRec.close()
            dbHandlerCat.close()

        }

        addDialog.tvCancelRecurring.setOnClickListener {
            addDialog.dismiss()
        }

        addDialog.show()
    }

    fun updateRecurring(recurring: RecurringModel) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update_recurring)
        updateDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dayPicked = recurring.nextDay
        var monthPicked = recurring.nextMonth
        var yearPicked = recurring.nextYear

        updateDialog.change_date_update_recurring.text =
            "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"

        updateDialog.change_date_update_recurring.setOnClickListener {
            val changeDateDialog = Dialog(this, R.style.Theme_Dialog)
            changeDateDialog.setCancelable(false)
            changeDateDialog.setContentView(R.layout.day_month_year_picker)
            changeDateDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (recurring.nextMonth == 4 || recurring.nextMonth == 6 || recurring.nextMonth == 9 || recurring.nextMonth == 11) {
                changeDateDialog.dmyp_day.maxValue = 30
                changeDateDialog.dmyp_day.minValue = 1
            } else if (recurring.nextMonth == 2 && recurring.nextMonth % 4 == 0) {
                changeDateDialog.dmyp_day.maxValue = 29
                changeDateDialog.dmyp_day.minValue = 1
            } else if (recurring.nextMonth == 2 && recurring.nextMonth % 4 != 0) {
                changeDateDialog.dmyp_day.maxValue = 28
                changeDateDialog.dmyp_day.minValue = 1
            } else {
                changeDateDialog.dmyp_day.maxValue = 31
                changeDateDialog.dmyp_day.minValue = 1
            }

            changeDateDialog.dmyp_month.maxValue = 12
            changeDateDialog.dmyp_month.minValue = 1
            changeDateDialog.dmyp_year.maxValue = 2999
            changeDateDialog.dmyp_year.minValue = 1000

            changeDateDialog.dmyp_day.value = recurring.nextDay
            changeDateDialog.dmyp_month.value = recurring.nextMonth
            changeDateDialog.dmyp_year.value = recurring.nextYear
            dayPicked = recurring.nextDay
            monthPicked = recurring.nextMonth
            yearPicked = recurring.nextYear

            changeDateDialog.dmyp_month.displayedValues = Constants.MONTHS_SHORT_ARRAY

            changeDateDialog.dmyp_day.setOnValueChangedListener { _, _, newVal ->
                dayPicked = newVal
            }

            changeDateDialog.dmyp_month.setOnValueChangedListener { _, _, newVal ->
                if (newVal == 4 || newVal == 6 || newVal == 9 || newVal == 11) {
                    changeDateDialog.dmyp_day.maxValue = 30
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal == 2 && (changeDateDialog.dmyp_year.value % 4 == 0)) {
                    changeDateDialog.dmyp_day.maxValue = 29
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal == 2 && (changeDateDialog.dmyp_year.value % 4 != 0)) {
                    changeDateDialog.dmyp_day.maxValue = 28
                    changeDateDialog.dmyp_day.minValue = 1
                } else {
                    changeDateDialog.dmyp_day.maxValue = 31
                    changeDateDialog.dmyp_day.minValue = 1
                }
                monthPicked = newVal
            }

            changeDateDialog.dmyp_year.setOnValueChangedListener { _, _, newVal ->
                if (newVal % 4 == 0 && changeDateDialog.dmyp_month.value == 2) {
                    changeDateDialog.dmyp_day.maxValue = 29
                    changeDateDialog.dmyp_day.minValue = 1
                } else if (newVal % 4 != 0 && changeDateDialog.dmyp_month.value == 2) {
                    changeDateDialog.dmyp_day.maxValue = 28
                    changeDateDialog.dmyp_day.minValue = 1
                }
                yearPicked = newVal
            }

            changeDateDialog.submit_dmy.setOnClickListener {
                updateDialog.change_date_update_recurring.text =
                    "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            changeDateDialog.dmyp_day.wrapSelectorWheel = true
            changeDateDialog.dmyp_month.wrapSelectorWheel = true
            changeDateDialog.dmyp_year.wrapSelectorWheel = true

            changeDateDialog.cancel_dmy.setOnClickListener {
                dayPicked = recurring.nextDay
                monthPicked = recurring.nextMonth
                yearPicked = recurring.nextYear
                updateDialog.change_date_update_recurring.text =
                    "$dayPicked ${Constants.getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            changeDateDialog.show()

        }

        updateDialog.etAmountLayoutRecurring.etAmountRecurring.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {
                val str = updateDialog.etAmountLayoutRecurring.etAmountRecurring.text.toString()
                if (str.isEmpty()) return
                val str2: String = currencyInputFilter(str, 6, 2)
                if (str2 != str) {
                    updateDialog.etAmountLayoutRecurring.etAmountRecurring.setText(str2)
                    updateDialog.etAmountLayoutRecurring.etAmountRecurring.setSelection(str2.length)
                }
            }
        })

        val categoryListHandler = CategoriesHandler(this, null)
        val items = categoryListHandler.getAllCategoryTitles()
        categoryListHandler.close()
        val categoryAdapter = ArrayAdapter(this, R.layout.custom_spinner, items)
        updateDialog.category_spinner_update_trans_recurring.adapter = categoryAdapter
        updateDialog.category_spinner_update_trans_recurring.onItemSelectedListener = this
        updateDialog.category_spinner_update_trans_recurring.setSelection(recurring.category - 1)

        updateDialog.etNoteLayoutUpdateRecurring.etNoteUpdateRecurring.setText(recurring.note)

        if (recurring.amount.toFloat() >= 0) {
            updateDialog.inc_exp_radio_group_recurring_update.income_radio_recurring_update.isChecked = true
            updateDialog.inc_exp_radio_group_recurring_update.expenditure_radio_recurring_update.isChecked = false
            updateDialog.etAmountLayoutRecurring.etAmountRecurring.setText(recurring.amount)
        } else {
            updateDialog.inc_exp_radio_group_recurring_update.expenditure_radio_recurring_update.isChecked = true
            updateDialog.inc_exp_radio_group_recurring_update.income_radio_recurring_update.isChecked = false
            updateDialog.etAmountLayoutRecurring.etAmountRecurring.setText((recurring.amount.toFloat() * -1).toString())
        }

        updateDialog.inc_exp_radio_group_recurring_update.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.income_radio_recurring_update -> {
                    isIncome = true
                }
                R.id.expenditure_radio_recurring_update -> {
                    isIncome = false
                }
                else -> {
                    Toast.makeText(
                        this,
                        "An error has occurred. Please try restarting the app.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        updateDialog.tvUpdateRecurring.setOnClickListener {
            val dbHandler = RecurringsHandler(this, null)
            val dbHandlerCat = CategoriesHandler(this, null)

            val category = dbHandlerCat.getCategoryId(selectedCategory)
            val amount = updateDialog.etAmountLayoutRecurring.etAmountRecurring.text.toString()
            val account = Constants.CURRENT_ACCOUNT
            val note = updateDialog.etNoteLayoutUpdateRecurring.etNoteUpdateRecurring.text.toString()
            val nextMonth = monthPicked
            val nextOGDay = dayPicked
            val nextDay = dayPicked
            val nextYear = yearPicked

            if (selectedCategory.isNotEmpty() && amount.isNotEmpty() && note.isNotEmpty()) {
                if (isIncome) {
                    dbHandler.updateRecurring(
                        RecurringModel(
                            recurring.id,
                            note,
                            category,
                            amount,
                            account,
                            nextMonth,
                            nextOGDay,
                            nextDay,
                            nextYear,
                            ""
                        )
                    )
                } else if (!isIncome) {
                    dbHandler.updateRecurring(
                        RecurringModel(
                            recurring.id,
                            note,
                            category,
                            (amount.toDouble() * -1).toString(),
                            account,
                            nextMonth,
                            nextOGDay,
                            nextDay,
                            nextYear,
                            ""
                        )
                    )
                }

                Toast.makeText(this, "Recurring transaction updated.", Toast.LENGTH_LONG).show()
                setUpRecurringList()
                updateDialog.dismiss()

            } else {
                Toast.makeText(this, "Category, amount or note can't be blank.", Toast.LENGTH_LONG)
                    .show()
            }

            dbHandler.close()
            dbHandlerCat.close()

        }

        updateDialog.tvCancelRecurring.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    fun deleteRecurring(recurring: RecurringModel) {
        val deleteDialog = Dialog(this, R.style.Theme_Dialog)
        deleteDialog.setCancelable(false)
        deleteDialog.setContentView(R.layout.dialog_delete_recurring)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        deleteDialog.tvDeleteRecurring.setOnClickListener {
            val dbHandler = RecurringsHandler(this, null)
            dbHandler.deleteRecurring(RecurringModel(recurring.id, "", 0, "", 0,0, 0, 0, 0, ""))

            Toast.makeText(this, "Recurring transaction deleted.", Toast.LENGTH_LONG).show()
            setUpRecurringList()
            dbHandler.close()
            deleteDialog.dismiss()
        }

        deleteDialog.tvCancelRecurring.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()

    }

    fun getTransactionCategoryTitle(CategoryId: Int): String {
        val dbHandlerCat = CategoriesHandler(this, null)
        val result = dbHandlerCat.getCategoryTitle(CategoryId)
        dbHandlerCat.close()
        return result
    }

    fun getTransactionCategoryColour(categoryId: Int): Int {
        val dbHandler = CategoriesHandler(this, null)
        val result = dbHandler.getCategoryColour(categoryId)
        dbHandler.close()
        return result
    }


    fun currencyInputFilter(str: String, MAX_BEFORE_POINT: Int, MAX_DECIMAL: Int): String {
        var str = str
        if (str[0] == '.') str = "0$str"
        val max = str.length
        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
                if (up > MAX_BEFORE_POINT) return rFinal
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "Nothing's selected in category dropdown.", Toast.LENGTH_SHORT).show()
    }


}