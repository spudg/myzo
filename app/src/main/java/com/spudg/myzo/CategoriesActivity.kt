package com.spudg.myzo

import android.app.Dialog
import android.content.Intent
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.dialog_add_category.*
import kotlinx.android.synthetic.main.dialog_add_category.etTitleLayout
import kotlinx.android.synthetic.main.dialog_add_category.view.etTitle
import kotlinx.android.synthetic.main.dialog_add_transaction.tvAdd
import kotlinx.android.synthetic.main.dialog_add_transaction.tvCancel
import kotlinx.android.synthetic.main.dialog_delete_transaction.*
import kotlinx.android.synthetic.main.dialog_update_category.*
import kotlinx.android.synthetic.main.dialog_update_transaction.tvUpdate

class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        setUpCategoryList()

        add_category.setOnClickListener {
            addCategory()
        }

        back_to_trans_from_categories.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun getCategoriesList(): ArrayList<CategoryModel> {
        val dbHandler = CategoriesHandler(this, null)
        val result = dbHandler.getAllCategories()
        dbHandler.close()
        return result
    }


    private fun setUpCategoryList() {
        if (getCategoriesList().size >= 0) {
            rvCategories.layoutManager = LinearLayoutManager(this)
            val categoriesAdapter = CategoryAdapter(this, getCategoriesList())
            rvCategories.adapter = categoriesAdapter
        }
    }

    private fun addCategory() {
        val addDialog = Dialog(this, R.style.Theme_Dialog)
        addDialog.setCancelable(false)
        addDialog.setContentView(R.layout.dialog_add_category)
        addDialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))

        addDialog.colourPickerAdd.showOldCenterColor = false

        addDialog.tvAdd.setOnClickListener {
            val title = addDialog.etTitleLayout.etTitle.text.toString()
            Constants.CAT_COL_SELECTED = addDialog.colourPickerAdd.color
            val colour = Constants.CAT_COL_SELECTED.toString()

            val dbHandler = CategoriesHandler(this, null)

            if (title.isNotEmpty() && colour.isNotEmpty()) {
                dbHandler.addCategory(CategoryModel(0, title, colour))
                if (Constants.CAT_UNIQUE_TITLE == 1) {
                    Toast.makeText(this, "Category added.", Toast.LENGTH_LONG).show()
                    setUpCategoryList()
                    addDialog.dismiss()
                } else {
                    Toast.makeText(this, "Category title already exists.", Toast.LENGTH_LONG).show()
                }

                setUpCategoryList()


            } else {
                Toast.makeText(this, "Title or colour can't be blank.", Toast.LENGTH_LONG).show()
            }

            dbHandler.close()

        }

        addDialog.tvCancel.setOnClickListener {
            addDialog.dismiss()
        }

        addDialog.show()
    }

    fun updateCategory(category: CategoryModel) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update_category)
        updateDialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))

        updateDialog.etTitleLayout.etTitle.setText(category.title)

        updateDialog.colourPickerUpdate.color = category.colour.toInt()
        updateDialog.colourPickerUpdate.showOldCenterColor = false

        updateDialog.tvUpdate.setOnClickListener {
            val title = updateDialog.etTitleLayout.etTitle.text.toString()
            Constants.CAT_COL_SELECTED = updateDialog.colourPickerUpdate.color
            val colour = Constants.CAT_COL_SELECTED.toString()

            val dbHandler = CategoriesHandler(this, null)

            if (title.isNotEmpty() && colour.isNotEmpty()) {
                dbHandler.updateCategory(CategoryModel(category.id, title, colour))
                if (Constants.CAT_UNIQUE_TITLE == 1) {
                    Toast.makeText(this, "Category updated.", Toast.LENGTH_LONG).show()
                    setUpCategoryList()
                    updateDialog.dismiss()
                } else {
                    Toast.makeText(this, "Category title already exists.", Toast.LENGTH_LONG).show()
                }
                setUpCategoryList()
            } else {
                Toast.makeText(this, "Title or colour can't be blank.", Toast.LENGTH_LONG).show()
            }

            dbHandler.close()

        }

        updateDialog.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    fun deleteCategory(category: CategoryModel) {
        val deleteDialog = Dialog(this, R.style.Theme_Dialog)
        deleteDialog.setCancelable(false)
        deleteDialog.setContentView(R.layout.dialog_delete_category)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))

        deleteDialog.tvDelete.setOnClickListener {
            val dbHandlerCat = CategoriesHandler(this, null)
            val dbHandlerTrans = TransactionsHandler(this, null)
            dbHandlerCat.deleteCategory(CategoryModel(category.id, "", ""))
            dbHandlerTrans.changeTransactionCategoryDueToCategoryDeletion(
                CategoryModel(
                    category.id,
                    "",
                    ""
                )
            )

            Toast.makeText(this, "Category deleted.", Toast.LENGTH_LONG).show()
            setUpCategoryList()
            dbHandlerCat.close()
            dbHandlerTrans.close()
            deleteDialog.dismiss()
        }

        deleteDialog.tvCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()

    }


}