package com.webkul.mobikul.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.webkul.mobikul.helpers.ApplicationConstants.DEFAULT_NUMBER_OF_RECENTLY_VIEWED_PRODUCTS
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_OFFLINE_MODE
import com.webkul.mobikul.models.product.ProductTileData
import io.reactivex.Observable
import org.json.JSONObject

import java.util.*

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
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 9
        private const val DATABASE_NAME = "OfflineDatabase.db"
    }

    private val TAG = "DatabaseHelper"

    // Table Name
    private val TABLE_OFFLINE_DATA = "OFFLINE_DATA"
    private val TABLE_OFFLINE_CART_DATA = "OFFLINE_CART_DATA"
    private val TABLE_RECENTLY_VIEWED_PRODUCTS = "RECENTLY_VIEWED_PRODUCTS_DATA"
    private val TABLE_RECENT_SEARCH = "RECENT_SEARCH_DATA"
    private val TABLE_OFFLINE_WISH_LIST_DATA = "OFFLINE_WISH_LIST_DATA"
    private val TABLE_OFFLINE_WISH_LIST_ALL_DATA = "OFFLINE_WISH_LIST_ALL_DATA"

    // Column Names
    private val TABLE_OFFLINE_DATA_COLUMN_ID = "id"
    private val TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = "hashIdentifier"
    private val TABLE_OFFLINE_DATA_COLUMN_ETAG = "eTag"
    private val TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA = "responsedata"

    //offline All wishilst column names
    private val TABLE_ALL_WISH_LIST_DATA_COLUMN_ID = "id"
    private val TABLE_ALL_WISH_LIST_DATA_COLUMN_PRODUCT_DATA = "productData"

    //offline wishilst column names
    private val TABLE_WISH_LIST_DATA_COLUMN_ID = "id"
    private val TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID = "productId"
    private val TABLE_WISH_LIST_DATA_COLUMN_QTY = "qty"
    private val TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID = "itemId"

    private val TABLE_CART_DATA_COLUMN_ID = "id"
    private val TABLE_CART_DATA_COLUMN_PRODUCT_ID = "productId"
    private val TABLE_CART_DATA_COLUMN_QTY = "qty"
    private val TABLE_CART_DATA_COLUMN_PARAMS = "params"
    private val TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS = "relatedProducts"

    private val TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_ID = "id"
    private val TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID = "storeId"
    private val TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE = "currencyCode"
    private val TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID = "productId"
    private val TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA = "productData"

    private val TABLE_RECENT_SEARCH_COLUMN_ID = "id"
    private val TABLE_RECENT_SEARCH_COLUMN_STORE_ID = "storeId"
    private val TABLE_RECENT_SEARCH_COLUMN_QUERY = "query"

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_OFFLINE_DATA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_OFFLINE_CART_DATA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECENTLY_VIEWED_PRODUCTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECENT_SEARCH")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_OFFLINE_WISH_LIST_DATA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_OFFLINE_WISH_LIST_ALL_DATA")

        // Create tables again
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createOfflineDataTable = "CREATE TABLE $TABLE_OFFLINE_DATA (" +
                "$TABLE_OFFLINE_DATA_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "$TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER VARCHAR, " +
                "$TABLE_OFFLINE_DATA_COLUMN_ETAG VARCHAR, " +
                "$TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA VARCHAR)"
        db?.execSQL(createOfflineDataTable)

        val createOfflineCartDataTable = "CREATE TABLE " + TABLE_OFFLINE_CART_DATA + "(" +
                TABLE_CART_DATA_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                TABLE_CART_DATA_COLUMN_PRODUCT_ID + " VARCHAR, " +
                TABLE_CART_DATA_COLUMN_QTY + " VARCHAR, " +
                TABLE_CART_DATA_COLUMN_PARAMS + " VARCHAR, " +
                TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS + " VARCHAR) "
        db?.execSQL(createOfflineCartDataTable)

        val createRecentlyViewedProductsDataTable =
            "CREATE TABLE $TABLE_RECENTLY_VIEWED_PRODUCTS (" +
                    "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID INTEGER, " +
                    "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE VARCHAR, " +
                    "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID VARCHAR, " +
                    "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA VARCHAR)"
        db?.execSQL(createRecentlyViewedProductsDataTable)

        val createRecentSearchDataTable = "CREATE TABLE $TABLE_RECENT_SEARCH (" +
                "$TABLE_RECENT_SEARCH_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "$TABLE_RECENT_SEARCH_COLUMN_STORE_ID INTEGER, " +
                "$TABLE_RECENT_SEARCH_COLUMN_QUERY VARCHAR)"
        db?.execSQL(createRecentSearchDataTable)

        val createOfflineWishListDataTable = "CREATE TABLE " + TABLE_OFFLINE_WISH_LIST_DATA + "(" +
                TABLE_WISH_LIST_DATA_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID + " VARCHAR, " +
                TABLE_WISH_LIST_DATA_COLUMN_QTY + " VARCHAR, " +
                TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID + " VARCHAR)"
        db?.execSQL(createOfflineWishListDataTable)


        val createOfflineWishListAllDataTable = "CREATE TABLE " + TABLE_OFFLINE_WISH_LIST_ALL_DATA + "(" +
                TABLE_ALL_WISH_LIST_DATA_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                TABLE_ALL_WISH_LIST_DATA_COLUMN_PRODUCT_DATA + " VARCHAR)"
        db?.execSQL(createOfflineWishListAllDataTable)
    }


    /* Offline database related functions */

    fun addOrUpdateIntoOfflineTable(hashIdentifier: String, eTag: String, responseData: String) {
        val cursor = writableDatabase.rawQuery(
            "SELECT $TABLE_OFFLINE_DATA_COLUMN_ETAG, $TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA FROM $TABLE_OFFLINE_DATA WHERE $TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = '$hashIdentifier'",
            null
        )
        if (cursor.count == 0) {
            insertIntoOfflineTable(hashIdentifier, eTag, responseData)
            cursor.close()
        } else{
            updateIntoOfflineTable(hashIdentifier, eTag, responseData)
            cursor.close()
        }
    }

    private fun insertIntoOfflineTable(hashIdentifier: String, eTag: String, responseData: String) {
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER, hashIdentifier)
        values.put(TABLE_OFFLINE_DATA_COLUMN_ETAG, eTag)
        values.put(TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA, responseData)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = writableDatabase.insert(TABLE_OFFLINE_DATA, null, values)
    }

    private fun updateIntoOfflineTable(hashIdentifier: String, eTag: String, responseData: String) {
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER, hashIdentifier)
        values.put(TABLE_OFFLINE_DATA_COLUMN_ETAG, eTag)
        values.put(TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA, responseData)

        // Define 'where' part of query.
        val selection = "$TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER LIKE ?"

        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(hashIdentifier)

        // Issue SQL statement.
        writableDatabase.update(TABLE_OFFLINE_DATA, values, selection, selectionArgs)
    }

    fun getETagFromDatabase(hashIdentifier: String): String {
        var cursor: Cursor? = null
        try {
            cursor = writableDatabase.rawQuery(
                "SELECT $TABLE_OFFLINE_DATA_COLUMN_ETAG FROM $TABLE_OFFLINE_DATA WHERE $TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = '$hashIdentifier'",
                null
            )
            if (cursor.count != 0) {
                cursor.moveToFirst()
                val eTag = cursor.getString(0)
                cursor.close()
                return eTag
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return ""
    }

    fun getResponseFromDatabaseOnThread(hashIdentifier: String): Observable<String> {
        var cursor: Cursor? = null
        try {
            if (ENABLE_OFFLINE_MODE) {
                val cursor = writableDatabase.rawQuery(
                    "SELECT $TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA FROM $TABLE_OFFLINE_DATA WHERE $TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = '$hashIdentifier'",
                    null
                )
                if (cursor.count != 0) {
                    cursor.moveToFirst()
                    val response = cursor.getString(0)
                    cursor.close()
                    Log.d(TAG, "getResponseFromDatabase: $response")
                    return Observable.just(response)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            writableDatabase?.close()
        }
        return Observable.just("")
    }

    fun clearOfflineTable() {
        writableDatabase?.delete(TABLE_OFFLINE_DATA, null, null)
    }


    /* Offline Cart Functiosn */

    fun addToCartOffline(productId: String, qty: String, params: String, relatedProducts: String) {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT " + TABLE_CART_DATA_COLUMN_QTY + ", " + TABLE_CART_DATA_COLUMN_ID + " FROM " + TABLE_OFFLINE_CART_DATA + " WHERE "
                        + TABLE_CART_DATA_COLUMN_PRODUCT_ID + " = '" + productId + "' AND "
                        + TABLE_CART_DATA_COLUMN_PARAMS + " = '" + params + "' AND "
                        + TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS + " = '" + relatedProducts + "'",
                null
            )

            if (cursor.count != 0) {
                cursor.moveToFirst()
                val previousQtyInDb = cursor.getString(0)
                val id = cursor.getString(1)

                val values = ContentValues()
                values.put(
                    TABLE_CART_DATA_COLUMN_QTY,
                    (previousQtyInDb.toInt() + qty.toInt()).toString()
                )

                val selection = "$TABLE_CART_DATA_COLUMN_ID LIKE ?"
                val selectionArgs = arrayOf(id.toString())

                val count = db.update(TABLE_OFFLINE_CART_DATA, values, selection, selectionArgs)
            } else {
                val values = ContentValues()
                values.put(TABLE_CART_DATA_COLUMN_PRODUCT_ID, productId)
                values.put(TABLE_CART_DATA_COLUMN_QTY, qty)
                values.put(TABLE_CART_DATA_COLUMN_PARAMS, params)
                values.put(TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS, relatedProducts)

                val newRowId = db.insert(TABLE_OFFLINE_CART_DATA, null, values)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }


    fun getCartTableRowCount(): Long {
        val cartDb = this.readableDatabase
        return DatabaseUtils.queryNumEntries(cartDb, TABLE_OFFLINE_CART_DATA)
    }

    fun getCartTableData(): Cursor {
        val cartDb = this.readableDatabase
        return cartDb.rawQuery(
            "SELECT " + TABLE_CART_DATA_COLUMN_PRODUCT_ID + ", "
                    + TABLE_CART_DATA_COLUMN_QTY + ", "
                    + TABLE_CART_DATA_COLUMN_PARAMS + ", "
                    + TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS
                    + " FROM " + TABLE_OFFLINE_CART_DATA, null
        )
    }

    fun deleteCartEntry(productId: String, params: String, relatedProducts: String) {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT " + TABLE_CART_DATA_COLUMN_ID + " FROM " + TABLE_OFFLINE_CART_DATA + " WHERE "
                        + TABLE_CART_DATA_COLUMN_PRODUCT_ID + " = '" + productId + "' AND "
                        + TABLE_CART_DATA_COLUMN_PARAMS + " = '" + params + "' AND "
                        + TABLE_CART_DATA_COLUMN_RELATED_PRODUCTS + " = '" + relatedProducts + "'",
                null
            )
            if (cursor.count != 0) {
                cursor.moveToFirst()
                db.delete(
                    TABLE_OFFLINE_CART_DATA,
                    "$TABLE_CART_DATA_COLUMN_ID LIKE ?",
                    arrayOf(cursor.getString(0))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }

    fun clearCartTableData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_OFFLINE_CART_DATA")
    }


    /* Offline WishList Cart Functiosn */

    fun addWishListToCartOffline(productId: String, itemId: String, qty: String):Boolean {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        var updated: Boolean=false
        try {
            cursor = db.rawQuery(
                "SELECT " + TABLE_WISH_LIST_DATA_COLUMN_QTY + ", " + TABLE_WISH_LIST_DATA_COLUMN_ID + " FROM " + TABLE_OFFLINE_WISH_LIST_DATA + " WHERE "
                        + TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID + " = '" + productId + "' AND "
                        + TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID + " = '" + itemId + "'", null
            )

            if (cursor.count != 0) {
                cursor.moveToFirst()
                val previousQtyInDb = cursor.getString(0)
                val id = cursor.getString(1)

                val values = ContentValues()
                values.put(
                    TABLE_WISH_LIST_DATA_COLUMN_QTY,
                    (previousQtyInDb.toInt() + qty.toInt()).toString()
                )

                val selection = "$TABLE_WISH_LIST_DATA_COLUMN_ID LIKE ?"
                val selectionArgs = arrayOf(id.toString())

                val count =
                    db.update(TABLE_OFFLINE_WISH_LIST_DATA, values, selection, selectionArgs)
                updated= count>0
            } else {
                val values = ContentValues()
                values.put(TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID, productId)
                values.put(TABLE_WISH_LIST_DATA_COLUMN_QTY, qty)
                values.put(TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID, itemId)


                val newRowId = db.insert(TABLE_OFFLINE_WISH_LIST_DATA, null, values)
                updated= newRowId>0
            }

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
           return updated
        }
    }


    fun getWishListTableRowCount(): Long {
        val cartDb = this.readableDatabase
        return DatabaseUtils.queryNumEntries(cartDb, TABLE_OFFLINE_WISH_LIST_DATA)
    }

    fun getWishListTableData(): Cursor {
        val cartDb = this.readableDatabase
        return cartDb.rawQuery(
            "SELECT " + TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID + ", "
                    + TABLE_WISH_LIST_DATA_COLUMN_QTY + ", "
                    + TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID
                    + " FROM " + TABLE_OFFLINE_WISH_LIST_DATA, null
        )
    }

    fun deleteWishListCartEntry(productId: String, itemId: String, qty: String) {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT " + TABLE_WISH_LIST_DATA_COLUMN_ID + " FROM " + TABLE_OFFLINE_WISH_LIST_DATA + " WHERE "
                        + TABLE_WISH_LIST_DATA_COLUMN_PRODUCT_ID + " = '" + productId + "' AND "
                        + TABLE_WISH_LIST_DATA_COLUMN_ITEM_ID + " = '" + itemId + "' AND "
                        + TABLE_WISH_LIST_DATA_COLUMN_QTY + " = '" + qty + "'", null
            )
            if (cursor.count != 0) {
                cursor.moveToFirst()
                db.delete(
                    TABLE_OFFLINE_WISH_LIST_DATA,
                    "$TABLE_WISH_LIST_DATA_COLUMN_ID LIKE ?",
                    arrayOf(cursor.getString(0))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }

    fun clearWishListCartTableData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_OFFLINE_WISH_LIST_DATA")
    }

    /* Offline All WishList to Cart Functiosn */

    fun addWishListAllToCartOffline( qty: JSONObject):Boolean {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        var updated: Boolean=false
        try {
            cursor = db.rawQuery(
                "SELECT  * FROM $TABLE_OFFLINE_WISH_LIST_ALL_DATA", null
            )

            clearWishListAllCartTableData()
                val values = ContentValues()
                values.put(TABLE_ALL_WISH_LIST_DATA_COLUMN_PRODUCT_DATA, qty.toString())


                val newRowId = db.insert(TABLE_OFFLINE_WISH_LIST_ALL_DATA, null, values)
                updated= newRowId>0

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            return updated
        }
    }


    fun getWishListAllTableRowCount(): Long {
        val cartDb = this.readableDatabase
        return DatabaseUtils.queryNumEntries(cartDb, TABLE_OFFLINE_WISH_LIST_ALL_DATA)
    }

    fun getWishListAllTableData(): Cursor {
        val cartDb = this.readableDatabase
        return cartDb.rawQuery(
            "SELECT $TABLE_ALL_WISH_LIST_DATA_COLUMN_PRODUCT_DATA FROM $TABLE_OFFLINE_WISH_LIST_ALL_DATA", null
        )
    }



    fun clearWishListAllCartTableData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_OFFLINE_WISH_LIST_ALL_DATA")
    }



    /* Recently Viewed Products database related functions */

    fun getRecentlyViewedProducts(
        storeId: String,
        currencyCode: String
    ): ArrayList<ProductTileData> {
        val productsList = ArrayList<ProductTileData>()
        var cursor: Cursor? = null
        try {
            val selectQuery =
                "SELECT $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA FROM $TABLE_RECENTLY_VIEWED_PRODUCTS WHERE $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID = '$storeId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE = '$currencyCode'"

            val db = this.writableDatabase
            cursor = db.rawQuery(selectQuery, null)

            val objectMapper = ObjectMapper()
            if (cursor.moveToLast()) {
                do {
                    val productData = objectMapper.readValue(
                        cursor.getString(0),
                        ProductTileData::class.java
                    ) as ProductTileData
                    productsList.add(productData)
                } while (cursor.moveToPrevious())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return productsList
    }

    fun addRecentlyViewed(
        storeId: String,
        currencyCode: String,
        productId: String,
        productData: String
    ) {
        if (!checkIfAlreadyAvailable(storeId, currencyCode, productId)) {
            val db = this.writableDatabase

            val values = ContentValues()
            values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID, storeId)
            values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE, currencyCode)
            values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID, productId)
            values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA, productData)

            db.insert(TABLE_RECENTLY_VIEWED_PRODUCTS, null, values)
            db.close()
            if (getRecentlyViewedProducts(
                    storeId,
                    currencyCode
                ).size > DEFAULT_NUMBER_OF_RECENTLY_VIEWED_PRODUCTS
            ) {
                deleteLastRecentlyViewedTableRow(storeId, currencyCode)
            }
        } else {
            deleteRecentlyViewedProduct(productId)
            addRecentlyViewed(storeId, currencyCode, productId, productData)
        }
    }

    fun updateRecentlyViewedProduct(
        storeId: String,
        currencyCode: String,
        productId: String,
        productData: String
    ) {
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID, storeId)
        values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE, currencyCode)
        values.put(TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA, productData)

        // Define 'where' part of query.
        val selection = "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID LIKE ?"

        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(productId)

        // Issue SQL statement.
        writableDatabase.update(TABLE_RECENTLY_VIEWED_PRODUCTS, values, selection, selectionArgs)
    }

    private fun checkIfAlreadyAvailable(
        storeId: String,
        currencyCode: String,
        productId: String
    ): Boolean {
        var cursor: Cursor? = null
        try {
            val selectQuery =
                "SELECT $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID FROM $TABLE_RECENTLY_VIEWED_PRODUCTS WHERE $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID = '$productId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID = '$storeId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE = '$currencyCode'"

            val db = this.writableDatabase
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    if (productId == cursor.getString(0)) {
                        cursor.close()
                        return true
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return false
    }

    fun removeWishListRecentlyViewedProduct(
        storeId: String,
        currencyCode: String,
        productId: String
    ) {
        // Create a new map of values, where column names are the keys
        var cursor: Cursor? = null
        try {
            val selectQuery =
                "SELECT $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_DATA FROM $TABLE_RECENTLY_VIEWED_PRODUCTS WHERE $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID = '$productId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID = '$storeId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE = '$currencyCode'"

            val db = this.writableDatabase
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                val objectMapper = ObjectMapper()
                val productData = objectMapper.readValue(
                    cursor.getString(0),
                    ProductTileData::class.java
                ) as ProductTileData
                productData.isInWishList = false
                productData.wishListItemId = ""
                updateRecentlyViewedProduct(
                    storeId,
                    currencyCode,
                    productId,
                    ObjectMapper().writeValueAsString(productData)
                )
            }
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }

    fun deleteRecentlyViewedProduct(productId: String) {
        val db = this.writableDatabase
        db.delete(
            TABLE_RECENTLY_VIEWED_PRODUCTS,
            "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_PRODUCT_ID = ?",
            arrayOf(productId)
        )

    }

    private fun deleteLastRecentlyViewedTableRow(storeId: String, currencyCode: String) {
        val db = this.writableDatabase
        db.delete(
            TABLE_RECENTLY_VIEWED_PRODUCTS,
            "$TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_ID = (SELECT MIN($TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_ID) FROM $TABLE_RECENTLY_VIEWED_PRODUCTS WHERE $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_STORE_ID = '$storeId' AND $TABLE_RECENTLY_VIEWED_PRODUCTS_COLUMN_CURRENCY_CODE = '$currencyCode')",
            arrayOf()
        )

    }

    fun clearRecentlyViewedProductsTableData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_RECENTLY_VIEWED_PRODUCTS")
    }


    /* Recent Search Functions */

    fun getRecentSearchList(storeId: String): ArrayList<String> {
        var cursor: Cursor? = null
        val recentSearchList = ArrayList<String>()
        try {
            val selectQuery =
                "SELECT $TABLE_RECENT_SEARCH_COLUMN_QUERY FROM $TABLE_RECENT_SEARCH WHERE $TABLE_RECENT_SEARCH_COLUMN_STORE_ID = '$storeId'"

            val db = this.writableDatabase
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToLast()) {
                do {
                    recentSearchList.add(cursor.getString(0))
                } while (cursor.moveToPrevious())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return recentSearchList
    }

    fun addRecentSearchQuery(storeId: String, query: String) {
        if (!checkIfAlreadyAvailableInRecentSearch(storeId, query)) {
            val db = this.writableDatabase

            val values = ContentValues()
            values.put(TABLE_RECENT_SEARCH_COLUMN_STORE_ID, storeId)
            values.put(TABLE_RECENT_SEARCH_COLUMN_QUERY, query)

            db.insert(TABLE_RECENT_SEARCH, null, values)
            db.close()
        } else {
            deleteParticularRecentSearch(query)
            addRecentSearchQuery(storeId, query)
        }
    }

    private fun checkIfAlreadyAvailableInRecentSearch(storeId: String, query: String): Boolean {
        var cursor: Cursor? = null
        try {
            val selectQuery =
                "SELECT $TABLE_RECENT_SEARCH_COLUMN_QUERY FROM $TABLE_RECENT_SEARCH WHERE $TABLE_RECENT_SEARCH_COLUMN_STORE_ID = '$storeId' AND $TABLE_RECENT_SEARCH_COLUMN_QUERY = ?"

            val db = this.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(query))
            if (cursor.moveToFirst()) {
                do {
                    if (query == cursor.getString(0)) {
                        cursor.close()
                        return true
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return false
    }

    fun deleteParticularRecentSearch(query: String) {
        val db = this.writableDatabase
        db.delete(TABLE_RECENT_SEARCH, "$TABLE_RECENT_SEARCH_COLUMN_QUERY = ?", arrayOf(query))
        db.close()
    }

    fun clearRecentSearchData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_RECENT_SEARCH")
    }

    /* Offline database related functions */

    fun saveOfflineTable(
        hashIdentifier: String,
        eTag: String,
        responseData: String
    ): Observable<Boolean> {
        var cursor: Cursor? = null
        try {
            cursor = writableDatabase.rawQuery(
                "SELECT $TABLE_OFFLINE_DATA_COLUMN_ETAG, $TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA FROM $TABLE_OFFLINE_DATA WHERE $TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = '$hashIdentifier'",
                null
            )
            if (cursor.count == 0)
                insertIntoOfflineTable(hashIdentifier, eTag, responseData)
            else
                updateIntoOfflineTable(hashIdentifier, eTag, responseData)
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return Observable.just(true)
    }

    fun getLocalData(hashIdentifier: String): Observable<String> {
        return Observable.just(getResponseFromDatabase(hashIdentifier))
    }

    private fun getResponseFromDatabase(hashIdentifier: String): String {
        var cursor: Cursor? = null
        try {
            if (ENABLE_OFFLINE_MODE) {
                cursor = writableDatabase.rawQuery(
                    "SELECT $TABLE_OFFLINE_DATA_COLUMN_RESPONSE_DATA FROM $TABLE_OFFLINE_DATA WHERE $TABLE_OFFLINE_DATA_COLUMN_HASH_IDENTIFIER = '$hashIdentifier'",
                    null
                )
                if (cursor.count != 0) {
                    cursor.moveToFirst()
                    val response = cursor.getString(0)
                    cursor.close()
                    Log.d(TAG, "getResponseFromDatabase: $response")
                    return response
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return ""
    }
}