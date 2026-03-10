package com.lpgagency.accounting.ui.inventory

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.db.SettingCategory
import com.lpgagency.accounting.data.models.InventoryItem
import com.lpgagency.accounting.data.models.StockMovement
import com.lpgagency.accounting.databinding.FragmentInventoryBinding
import com.lpgagency.accounting.utils.FormatUtil
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        adapter = InventoryAdapter(emptyList()) { item -> showUpdateDialog(item) }
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter

        repo.getAllInventory().observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
            binding.tvEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddInventory.setOnClickListener { showAddCylinderTypeDialog() }
    }

    /** Load cylinder types dynamically from settings DB */
    private fun showAddCylinderTypeDialog() {
        lifecycleScope.launch {
            val types = repo.getSettingsSync(SettingCategory.CYLINDER_TYPE).map { it.value }.toTypedArray()
            if (types.isEmpty()) {
                Toast.makeText(requireContext(),
                    "No cylinder types configured. Add them in Settings.", Toast.LENGTH_LONG).show()
                return@launch
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Add Cylinder Type")
                .setItems(types) { _, which ->
                    showInventoryEditDialog(null, types[which])
                }.show()
        }
    }

    private fun showUpdateDialog(item: InventoryItem) {
        val options = arrayOf("Receive Stock from Supplier", "Edit Prices & Stock", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle("${item.cylinderType} — ${item.fullCylinders} full, ${item.emptyCylinders} empty")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showStockInDialog(item)
                    1 -> showInventoryEditDialog(item, item.cylinderType)
                    2 -> AlertDialog.Builder(requireContext())
                            .setTitle("Delete ${item.cylinderType}?")
                            .setMessage("This removes the inventory record. Sales history is not affected.")
                            .setPositiveButton("Delete") { _, _ ->
                                lifecycleScope.launch { repo.inventoryDao.delete(item) }
                            }
                            .setNegativeButton("Cancel", null).show()
                }
            }.show()
    }

    private fun showStockInDialog(item: InventoryItem) {
        val etFullIn  = EditText(requireContext()).apply { hint = "Full cylinders received";  inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val etEmptyOut= EditText(requireContext()).apply { hint = "Empty cylinders returned"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
            addView(TextView(requireContext()).apply { text = "Receive Stock — ${item.cylinderType}" })
            addView(etFullIn); addView(etEmptyOut)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Receive Stock")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val fullIn   = etFullIn.text.toString().toIntOrNull()   ?: 0
                val emptyOut = etEmptyOut.text.toString().toIntOrNull() ?: 0
                lifecycleScope.launch {
                    repo.updateInventory(item.copy(
                        fullCylinders  = item.fullCylinders + fullIn,
                        emptyCylinders = (item.emptyCylinders - emptyOut).coerceAtLeast(0),
                        lastUpdated    = System.currentTimeMillis()
                    ))
                    repo.insertStockMovement(StockMovement(
                        cylinderType = item.cylinderType,
                        movementType = "Stock In",
                        fullChange   = fullIn,
                        emptyChange  = -emptyOut,
                        notes        = "Received $fullIn full, returned $emptyOut empty"
                    ))
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showInventoryEditDialog(existing: InventoryItem?, cylinderType: String) {
        val etFull      = EditText(requireContext()).apply { hint = "Full cylinders";       inputType = android.text.InputType.TYPE_CLASS_NUMBER;  setText(existing?.fullCylinders?.toString()   ?: "0") }
        val etEmpty     = EditText(requireContext()).apply { hint = "Empty cylinders";      inputType = android.text.InputType.TYPE_CLASS_NUMBER;  setText(existing?.emptyCylinders?.toString()  ?: "0") }
        val etRefill    = EditText(requireContext()).apply { hint = "Price per refill";     inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL; setText(existing?.pricePerRefill?.toString()    ?: "") }
        val etNewCyl    = EditText(requireContext()).apply { hint = "Price for new cylinder"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL; setText(existing?.priceNewCylinder?.toString() ?: "") }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL; setPadding(48, 16, 48, 16)
            addView(TextView(requireContext()).apply { text = "Cylinder Type: $cylinderType"; setPadding(0,0,0,12) })
            addView(etFull); addView(etEmpty); addView(etRefill); addView(etNewCyl)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Add $cylinderType Inventory" else "Edit $cylinderType")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val full    = etFull.text.toString().toIntOrNull()    ?: 0
                val empty   = etEmpty.text.toString().toIntOrNull()   ?: 0
                val refill  = etRefill.text.toString().toDoubleOrNull()  ?: 0.0
                val newCyl  = etNewCyl.text.toString().toDoubleOrNull()  ?: 0.0
                lifecycleScope.launch {
                    val item = InventoryItem(
                        id               = existing?.id ?: 0,
                        cylinderType     = cylinderType,
                        fullCylinders    = full,
                        emptyCylinders   = empty,
                        totalCylinders   = full + empty,
                        pricePerRefill   = refill,
                        priceNewCylinder = newCyl
                    )
                    if (existing == null) repo.insertInventory(item) else repo.updateInventory(item)
                }
            }.setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
