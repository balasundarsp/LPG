package com.lpgagency.accounting.data.db

import android.content.Context
import com.lpgagency.accounting.data.models.*

class AppRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    val saleDao          = db.saleDao()
    val expenseDao       = db.expenseDao()
    val inventoryDao     = db.inventoryDao()
    val stockMovementDao = db.stockMovementDao()
    val settingDao       = db.appSettingDao()

    // Sales
    suspend fun insertSale(s: Sale)   = saleDao.insert(s)
    suspend fun updateSale(s: Sale)   = saleDao.update(s)
    suspend fun deleteSale(s: Sale)   = saleDao.delete(s)
    fun getAllSales()                  = saleDao.getAllSales()
    fun getRecentSales()              = saleDao.getRecentSales()
    fun searchSales(q: String)        = saleDao.searchSales(q)
    suspend fun getTodaySales(t: Long)= saleDao.getTodaySales(t) ?: 0.0
    suspend fun getTotalOutstanding() = saleDao.getTotalOutstanding() ?: 0.0

    // Expenses
    suspend fun insertExpense(e: Expense)  = expenseDao.insert(e)
    suspend fun updateExpense(e: Expense)  = expenseDao.update(e)
    suspend fun deleteExpense(e: Expense)  = expenseDao.delete(e)
    fun getAllExpenses()                    = expenseDao.getAllExpenses()
    fun getRecentExpenses()                = expenseDao.getRecentExpenses()
    suspend fun getTodayExpenses(t: Long)  = expenseDao.getTodayExpenses(t) ?: 0.0

    // Inventory
    suspend fun insertInventory(i: InventoryItem) = inventoryDao.insert(i)
    suspend fun updateInventory(i: InventoryItem) = inventoryDao.update(i)
    fun getAllInventory()                          = inventoryDao.getAllInventory()
    suspend fun getInventoryByType(t: String)     = inventoryDao.getByType(t)
    suspend fun getTotalFullCylinders()           = inventoryDao.getTotalFullCylinders() ?: 0
    suspend fun getTotalEmptyCylinders()          = inventoryDao.getTotalEmptyCylinders() ?: 0

    // Stock movements
    suspend fun insertStockMovement(m: StockMovement) = stockMovementDao.insert(m)
    fun getAllMovements()                               = stockMovementDao.getAllMovements()

    // Settings (customisable lists)
    fun getSettings(cat: String)              = settingDao.getByCategory(cat)
    suspend fun getSettingsSync(cat: String)  = settingDao.getByCategorySync(cat)
    suspend fun insertSetting(s: AppSetting)  = settingDao.insert(s)
    suspend fun updateSetting(s: AppSetting)  = settingDao.update(s)
    suspend fun deleteSetting(s: AppSetting)  = settingDao.delete(s)
}
