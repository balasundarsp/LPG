package com.lpgagency.accounting.ui.expenses

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.models.Expense
import com.lpgagency.accounting.utils.FormatUtil

class ExpensesAdapter(
    private var expenses: List<Expense>,
    private val onClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvPayment: TextView = view.findViewById(R.id.tvPaymentMethod)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val e = expenses[position]
        holder.tvCategory.text = e.category
        holder.tvDescription.text = e.description
        holder.tvAmount.text = FormatUtil.formatCurrency(e.amount)
        holder.tvPayment.text = e.paymentMethod
        holder.tvDate.text = FormatUtil.formatDate(e.date)
        holder.itemView.setOnClickListener { onClick(e) }
    }

    override fun getItemCount() = expenses.size
}
