package com.lpgagency.accounting.ui.settings

import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.models.AppSetting

class SettingItemAdapter(
    private var items: List<AppSetting>,
    private val onEdit: (AppSetting) -> Unit,
    private val onDelete: (AppSetting) -> Unit
) : RecyclerView.Adapter<SettingItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValue: TextView      = view.findViewById(R.id.tv_setting_value)
        val btnEdit: ImageButton   = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    fun updateData(newItems: List<AppSetting>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvValue.text = item.value
        holder.btnEdit.setOnClickListener   { onEdit(item)   }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = items.size
}
