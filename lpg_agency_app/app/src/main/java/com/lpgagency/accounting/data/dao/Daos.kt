package com.lpgagency.accounting.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lpgagency.accounting.data.models.*

// ─── SALE DAO ────────────────────────────────────────────────────────────────
@Dao
interface SaleDao {
    @Insert suspend fun insert(sale: Sale): Long
    @Update suspend fun update(sale: Sale)
    @Delete suspend fun delete(sale: Sale)

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): LiveData<List<Sale>>

    @Query("SELECT * FROM sales WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getSalesByDateRange(startDate: Long, endDate: Long): LiveData<List<Sale>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalSalesAmount(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(balance) FROM sales WHERE balance > 0")
    suspend fun getTotalOutstanding(): Double?

    @Query("SELECT * FROM sales WHERE customerName LIKE '%' || :query || '%'")
    fun searchSales(query: String): LiveData<List<Sale>>

    @Query("SELECT * FROM sales ORDER BY date DESC LIMIT 10")
    fun getRecentSales(): LiveData<List<Sale>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE date >= :today")
    suspend fun getTodaySales(today: Long): Double?
}

// ─── EXPENSE DAO ─────────────────────────────────────────────────────────────
@Dao
interface ExpenseDao {
    @Insert suspend fun insert(expense: Expense): Long
    @Update suspend fun update(expense: Expense)
    @Delete suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalExpenses(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :today")
    suspend fun getTodayExpenses(today: Long): Double?

    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT 10")
    fun getRecentExpenses(): LiveData<List<Expense>>
}

// ─── INVENTORY DAO ────────────────────────────────────────────────────────────
@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: InventoryItem): Long
    @Update suspend fun update(item: InventoryItem)
    @Delete suspend fun delete(item: InventoryItem)

    @Query("SELECT * FROM inventory ORDER BY cylinderType ASC")
    fun getAllInventory(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE cylinderType = :type LIMIT 1")
    suspend fun getByType(type: String): InventoryItem?

    @Query("SELECT SUM(fullCylinders) FROM inventory")
    suspend fun getTotalFullCylinders(): Int?

    @Query("SELECT SUM(emptyCylinders) FROM inventory")
    suspend fun getTotalEmptyCylinders(): Int?
}

// ─── STOCK MOVEMENT DAO ───────────────────────────────────────────────────────
@Dao
interface StockMovementDao {
    @Insert suspend fun insert(movement: StockMovement): Long

    @Query("SELECT * FROM stock_movements ORDER BY date DESC")
    fun getAllMovements(): LiveData<List<StockMovement>>

    @Query("SELECT * FROM stock_movements WHERE cylinderType = :type ORDER BY date DESC")
    fun getMovementsByType(type: String): LiveData<List<StockMovement>>
}
