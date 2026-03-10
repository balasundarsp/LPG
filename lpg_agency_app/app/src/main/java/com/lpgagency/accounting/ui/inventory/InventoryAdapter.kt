package com.lpgagency.accounting.ui.inventory

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.models.InventoryItem
import com.lpgagency.accounting.utils.FormatUtil

class InventoryAdapter(
    private var items: List<InventoryItem>,
    private val onClick: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvCylinderType)
        val tvFull: TextView = view.findViewById(R.id.tvFullCylinders)
        val tvEmpty: TextView = view.findViewById(R.id.tvEmptyCylinders)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvPriceRefill: TextView = view.findViewById(R.id.tvPriceRefill)
        val tvPriceNew: TextView = view.findViewById(R.id.tvPriceNew)
        val tvUpdated: TextView = view.findViewById(R.id.tvLastUpdated)
    }

    fun updateData(newItems: List<InventoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvType.text = item.cylinderType
        holder.tvFull.text = "Full: ${item.fullCylinders}"
        holder.tvEmpty.text = "Empty: ${item.emptyCylinders}"
        holder.tvTotal.text = "Total: ${item.fullCylinders + item.emptyCylinders}"
        holder.tvPriceRefill.text = "Refill: ${FormatUtil.formatCurrency(item.pricePerRefill)}"
        holder.tvPriceNew.text = "New: ${FormatUtil.formatCurrency(item.priceNewCylinder)}"
        holder.tvUpdated.text = "Updated: ${FormatUtil.formatDate(item.lastUpdated)}"
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
