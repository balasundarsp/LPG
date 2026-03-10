package com.lpgagency.accounting.ui.sales

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.models.Sale
import com.lpgagency.accounting.utils.FormatUtil

class SalesAdapter(
    private var sales: List<Sale>,
    private val onLongClick: (Sale) -> Unit
) : RecyclerView.Adapter<SalesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomer)
        val tvDetails: TextView = view.findViewById(R.id.tvSaleType)
        val tvTotal: TextView = view.findViewById(R.id.tvAmount)
        val tvPaid: TextView = view.findViewById(R.id.tvPaid)
        val tvBalance: TextView = view.findViewById(R.id.tvBalance)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    fun updateData(newSales: List<Sale>) {
        sales = newSales
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = sales[position]
        holder.tvCustomer.text = sale.customerName
        holder.tvDetails.text = "${sale.saleType} · ${sale.cylinderType} · Qty: ${sale.quantity}"
        holder.tvTotal.text = "Total: ${FormatUtil.formatCurrency(sale.totalAmount)}"
        holder.tvPaid.text = "Paid: ${FormatUtil.formatCurrency(sale.amountPaid)}"
        holder.tvBalance.text = if (sale.balance > 0) "Bal: ${FormatUtil.formatCurrency(sale.balance)}" else "✓ Paid"
        holder.tvDate.text = FormatUtil.formatDate(sale.date)
        holder.itemView.setOnLongClickListener { onLongClick(sale); true }
        holder.itemView.setOnClickListener { onLongClick(sale) }
    }

    override fun getItemCount() = sales.size
}
