package com.lpgagency.accounting.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores all user-customizable list items.
 * category: "CYLINDER_TYPE" | "SALE_TYPE" | "EXPENSE_CATEGORY" | "PAYMENT_METHOD"
 */
@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val value: String,
    val sortOrder: Int = 0
)
