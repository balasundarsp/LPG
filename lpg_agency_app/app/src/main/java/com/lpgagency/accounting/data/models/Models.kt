package com.lpgagency.accounting.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// ─── SALE ───────────────────────────────────────────────────────────────────
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String = "",
    val cylinderType: String,       // e.g. "6kg", "12kg", "50kg"
    val saleType: String,           // "Refill" | "New Cylinder" | "Deposit Return"
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val amountPaid: Double,
    val balance: Double,            // outstanding
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)

// ─── EXPENSE ─────────────────────────────────────────────────────────────────
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,           // "Delivery", "Maintenance", "Staff", "Rent", "Other"
    val description: String,
    val amount: Double,
    val paymentMethod: String = "Cash",
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)

// ─── INVENTORY ───────────────────────────────────────────────────────────────
@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cylinderType: String,       // "6kg", "12kg", "50kg"
    val fullCylinders: Int,
    val emptyCylinders: Int,
    val totalCylinders: Int,
    val pricePerRefill: Double,
    val priceNewCylinder: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ─── STOCK MOVEMENT ──────────────────────────────────────────────────────────
@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cylinderType: String,
    val movementType: String,       // "Stock In", "Sale", "Return", "Adjustment"
    val fullChange: Int,            // positive = added, negative = removed
    val emptyChange: Int,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)
