package com.lpgagency.accounting.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lpgagency.accounting.data.dao.*
import com.lpgagency.accounting.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Sale::class, Expense::class, InventoryItem::class,
                StockMovement::class, AppSetting::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saleDao(): SaleDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        const val DATABASE_NAME = "lpg_agency.db"
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `app_settings` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT NOT NULL,
                        `value` TEXT NOT NULL,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { seedDefaults(it) }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch { seedIfEmpty(instance) }
                instance
            }
        }

        private suspend fun seedIfEmpty(db: AppDatabase) {
            if (db.appSettingDao().countByCategory(SettingCategory.CYLINDER_TYPE) == 0)
                seedDefaults(db)
        }

        private suspend fun seedDefaults(db: AppDatabase) {
            val dao = db.appSettingDao()
            val defaults = mapOf(
                SettingCategory.CYLINDER_TYPE    to listOf("3kg","6kg","12kg","50kg"),
                SettingCategory.SALE_TYPE        to listOf("Refill","New Cylinder","Deposit Return","Exchange","Other"),
                SettingCategory.EXPENSE_CATEGORY to listOf("Delivery","Maintenance","Staff","Rent","Utilities","Purchases","Other"),
                SettingCategory.PAYMENT_METHOD   to listOf("Cash","Mobile Money","Bank Transfer","Cheque","Credit")
            )
            defaults.forEach { (cat, values) ->
                values.forEachIndexed { i, v -> dao.insert(AppSetting(category = cat, value = v, sortOrder = i)) }
            }
        }
    }
}

object SettingCategory {
    const val CYLINDER_TYPE     = "CYLINDER_TYPE"
    const val SALE_TYPE         = "SALE_TYPE"
    const val EXPENSE_CATEGORY  = "EXPENSE_CATEGORY"
    const val PAYMENT_METHOD    = "PAYMENT_METHOD"
}
