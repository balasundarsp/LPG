package com.lpgagency.accounting.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.db.SettingCategory
import com.lpgagency.accounting.data.models.AppSetting
import com.lpgagency.accounting.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository

    // One adapter per list
    private val adapters = mutableMapOf<String, SettingItemAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        setupSection(
            binding.rvCylinderTypes, binding.btnAddCylinder,
            SettingCategory.CYLINDER_TYPE, "Cylinder Type"
        )
        setupSection(
            binding.rvSaleTypes, binding.btnAddSaleType,
            SettingCategory.SALE_TYPE, "Sale Type"
        )
        setupSection(
            binding.rvExpenseCategories, binding.btnAddExpenseCat,
            SettingCategory.EXPENSE_CATEGORY, "Expense Category"
        )
        setupSection(
            binding.rvPaymentMethods, binding.btnAddPayment,
            SettingCategory.PAYMENT_METHOD, "Payment Method"
        )
    }

    private fun setupSection(
        rv: RecyclerView,
        addBtn: View,
        category: String,
        label: String
    ) {
        val adapter = SettingItemAdapter(emptyList(),
            onEdit   = { showEditDialog(it, label) },
            onDelete = { confirmDelete(it, label) }
        )
        adapters[category] = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        rv.isNestedScrollingEnabled = false

        repo.getSettings(category).observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
        }

        addBtn.setOnClickListener { showAddDialog(category, label) }
    }

    private fun showAddDialog(category: String, label: String) {
        val et = EditText(requireContext()).apply {
            hint = "Enter $label name"
            setPadding(48, 24, 48, 24)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Add $label")
            .setView(et)
            .setPositiveButton("Add") { _, _ ->
                val value = et.text.toString().trim()
                if (value.isNotEmpty()) {
                    lifecycleScope.launch {
                        repo.insertSetting(AppSetting(category = category, value = value))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(setting: AppSetting, label: String) {
        val et = EditText(requireContext()).apply {
            setText(setting.value)
            setPadding(48, 24, 48, 24)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Edit $label")
            .setView(et)
            .setPositiveButton("Save") { _, _ ->
                val value = et.text.toString().trim()
                if (value.isNotEmpty()) {
                    lifecycleScope.launch {
                        repo.updateSetting(setting.copy(value = value))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(setting: AppSetting, label: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete \"${setting.value}\"?")
            .setMessage("This $label will no longer appear in dropdowns. Existing records are not affected.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch { repo.deleteSetting(setting) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
