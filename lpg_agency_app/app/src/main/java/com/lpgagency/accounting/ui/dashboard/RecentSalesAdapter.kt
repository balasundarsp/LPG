package com.lpgagency.accounting.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.models.Sale
import com.lpgagency.accounting.utils.FormatUtil

class RecentSalesAdapter(private val sales: List<Sale>) :
    RecyclerView.Adapter<RecentSalesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomer)
        val tvType: TextView = view.findViewById(R.id.tvSaleType)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvBalance: TextView = view.findViewById(R.id.tvBalance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = sales[position]
        holder.tvCustomer.text = sale.customerName
        holder.tvType.text = "${sale.saleType} - ${sale.cylinderType} x${sale.quantity}"
        holder.tvAmount.text = FormatUtil.formatCurrency(sale.totalAmount)
        holder.tvDate.text = FormatUtil.formatDate(sale.date)
        if (sale.balance > 0) {
            holder.tvBalance.text = "Owes: ${FormatUtil.formatCurrency(sale.balance)}"
            holder.tvBalance.visibility = View.VISIBLE
        } else {
            holder.tvBalance.visibility = View.GONE
        }
    }

    override fun getItemCount() = sales.size
}
