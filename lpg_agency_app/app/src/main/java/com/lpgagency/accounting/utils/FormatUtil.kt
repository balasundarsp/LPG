package com.lpgagency.accounting.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtil {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    private val dayStartFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatCurrency(amount: Double): String = currencyFormat.format(amount)
    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    fun todayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun monthStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val CYLINDER_TYPES = listOf("6kg", "12kg", "50kg", "Other")
    val SALE_TYPES = listOf("Refill", "New Cylinder", "Deposit Return", "Other")
    val EXPENSE_CATEGORIES = listOf("Delivery", "Maintenance", "Staff", "Rent", "Utilities", "Purchases", "Other")
    val PAYMENT_METHODS = listOf("Cash", "Transfer", "Cheque", "Credit")
}
