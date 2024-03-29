package com.spudg.myzo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CategoriesHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "SMMCategories.db"
        private const val TABLE_CATEGORIES = "categories"

        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_COLOUR = "colour"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val createCategoriesTable =
            ("CREATE TABLE $TABLE_CATEGORIES($KEY_ID INTEGER PRIMARY KEY,$KEY_TITLE TEXT,$KEY_COLOUR TEXT)")
        db?.execSQL(createCategoriesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }


    fun addCategory(category: CategoryModel) {
        val values = ContentValues()
        values.put(KEY_TITLE, category.title)
        values.put(KEY_COLOUR, category.colour)
        val existingTitles = getAllCategoryTitles()

        val db = this.writableDatabase

        var alreadyExists = false
        for (title in existingTitles) {
            if (title == category.title) {
                alreadyExists = true
            }
        }

        if (!alreadyExists) {
            db.insert(TABLE_CATEGORIES, null, values)
            Constants.CAT_UNIQUE_TITLE = 1
        } else {
            Constants.CAT_UNIQUE_TITLE = 0
        }

        db.close()
    }

    fun updateCategory(category: CategoryModel) {
        val values = ContentValues()
        values.put(KEY_TITLE, category.title)
        values.put(KEY_COLOUR, category.colour)
        val existingTitles = getAllCategoryTitles()

        val dbForSearch = this.readableDatabase
        val dbForUpdate = this.writableDatabase

        if (existingTitles.contains(category.title)) {
            Constants.CAT_UNIQUE_TITLE = 0
        } else {
            dbForUpdate.update(TABLE_CATEGORIES, values, KEY_ID + "=" + category.id, null)
            Constants.CAT_UNIQUE_TITLE = 1
        }

        if (Constants.CAT_UNIQUE_TITLE == 0) {
            val cursor =
                dbForSearch.rawQuery(
                    "SELECT * FROM $TABLE_CATEGORIES WHERE _id = ${category.id}",
                    null
                )
            if (cursor.moveToFirst()) {
                val oldTitle = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                val newTitle = category.title
                if (oldTitle == newTitle) {
                    dbForUpdate.update(
                        TABLE_CATEGORIES,
                        values,
                        KEY_ID + "=" + category.id,
                        null
                    )
                    Constants.CAT_UNIQUE_TITLE = 1
                } else {
                    Constants.CAT_UNIQUE_TITLE = 0
                }
            }

            cursor.close()
            dbForSearch.close()
            dbForUpdate.close()

        }

    }

    fun deleteCategory(category: CategoryModel): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CATEGORIES, KEY_ID + "=" + category.id, null)
        db.close()
        return success
    }

    fun getAllCategories(): ArrayList<CategoryModel> {
        val list = ArrayList<CategoryModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)

        var id: Int
        var title: String
        var colour: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                colour = cursor.getString(cursor.getColumnIndex(KEY_COLOUR))
                val category = CategoryModel(
                    id = id,
                    title = title,
                    colour = colour,
                )
                list.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list

    }

    fun getAllCategoryTitles(): ArrayList<String> {
        val list = ArrayList<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)

        var title: String

        if (cursor.moveToFirst()) {
            do {
                title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                list.add(title)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list

    }

    fun getCategoryColour(categoryId: Int): Int {
        val db = this.readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $KEY_ID = '$categoryId'", null)

        val colour: Int = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(KEY_COLOUR)).toInt()
        } else {
            0
        }

        cursor.close()
        db.close()

        return colour

    }

    fun getCategoryId(categoryTitle: String): Int {
        val db = this.readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $KEY_TITLE = '$categoryTitle'", null)

        val id: Int = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(KEY_ID))
        } else {
            0
        }

        cursor.close()
        db.close()

        return id

    }

    fun getCategoryTitle(categoryId: Int): String {
        val db = this.readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $KEY_ID = $categoryId", null)

        val title: String = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(KEY_TITLE))
        } else {
            "Error"
        }

        cursor.close()
        db.close()

        return title

    }

    fun resetOnImport() {
        val db = this.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }


}