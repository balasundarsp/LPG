package com.lpgagency.accounting.ui.sales

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.db.SettingCategory
import com.lpgagency.accounting.data.models.AppSetting
import com.lpgagency.accounting.data.models.Sale
import com.lpgagency.accounting.databinding.FragmentAddSaleBinding
import com.lpgagency.accounting.utils.FormatUtil
import kotlinx.coroutines.launch

class AddSaleFragment : Fragment() {
    private var _binding: FragmentAddSaleBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository
    private var editingSaleId: Long = -1L

    // Dynamic lists loaded from DB
    private var cylinderTypes: List<AppSetting> = emptyList()
    private var saleTypes: List<AppSetting>     = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        // Load dynamic spinner options
        lifecycleScope.launch {
            cylinderTypes = repo.getSettingsSync(SettingCategory.CYLINDER_TYPE)
            saleTypes     = repo.getSettingsSync(SettingCategory.SALE_TYPE)

            setupSpinners()

            editingSaleId = arguments?.getLong("saleId", -1L) ?: -1L
            if (editingSaleId != -1L) {
                loadSaleForEdit(editingSaleId)
                binding.btnSave.text = "Update Sale"
            }
        }

        // Auto-calculate total
        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { calculateTotal() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etQuantity.addTextChangedListener(watcher)
        binding.etUnitPrice.addTextChangedListener(watcher)
        binding.etAmountPaid.addTextChangedListener(watcher)

        binding.btnSave.setOnClickListener { saveSale() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupSpinners() {
        val cylLabels = cylinderTypes.map { it.value }
        val saleLabels = saleTypes.map { it.value }

        val cylAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cylLabels)
        cylAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCylinderType.adapter = cylAdapter

        val saleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, saleLabels)
        saleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSaleType.adapter = saleAdapter

        // Auto-fill unit price when cylinder type changes
        binding.spinnerCylinderType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                lifecycleScope.launch {
                    val type = cylLabels.getOrNull(pos) ?: return@launch
                    val inv  = repo.getInventoryByType(type)
                    if (inv != null && binding.etUnitPrice.text.isNullOrEmpty()) {
                        val saleType = saleLabels.getOrNull(binding.spinnerSaleType.selectedItemPosition) ?: ""
                        val price = if (saleType.contains("New", ignoreCase = true)) inv.priceNewCylinder else inv.pricePerRefill
                        if (price > 0) binding.etUnitPrice.setText(price.toString())
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun calculateTotal() {
        val qty   = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        val price = binding.etUnitPrice.text.toString().toDoubleOrNull() ?: 0.0
        val paid  = binding.etAmountPaid.text.toString().toDoubleOrNull() ?: 0.0
        val total   = qty * price
        val balance = (total - paid).coerceAtLeast(0.0)
        binding.tvTotal.text   = "Total: ${FormatUtil.formatCurrency(total)}"
        binding.tvBalance.text = "Balance: ${FormatUtil.formatCurrency(balance)}"
    }

    private fun loadSaleForEdit(saleId: Long) {
        repo.getAllSales().observe(viewLifecycleOwner) { sales ->
            val sale = sales.find { it.id == saleId } ?: return@observe
            binding.etCustomerName.setText(sale.customerName)
            binding.etCustomerPhone.setText(sale.customerPhone)
            binding.etQuantity.setText(sale.quantity.toString())
            binding.etUnitPrice.setText(sale.unitPrice.toString())
            binding.etAmountPaid.setText(sale.amountPaid.toString())
            binding.etNotes.setText(sale.notes)
            val cylIdx = cylinderTypes.indexOfFirst { it.value == sale.cylinderType }
            if (cylIdx >= 0) binding.spinnerCylinderType.setSelection(cylIdx)
            val saleIdx = saleTypes.indexOfFirst { it.value == sale.saleType }
            if (saleIdx >= 0) binding.spinnerSaleType.setSelection(saleIdx)
        }
    }

    private fun saveSale() {
        val customerName = binding.etCustomerName.text.toString().trim()
        if (customerName.isEmpty()) { binding.etCustomerName.error = "Customer name required"; return }
        val qty = binding.etQuantity.text.toString().toIntOrNull()
        if (qty == null || qty <= 0) { binding.etQuantity.error = "Enter valid quantity"; return }
        val unitPrice = binding.etUnitPrice.text.toString().toDoubleOrNull()
        if (unitPrice == null || unitPrice < 0) { binding.etUnitPrice.error = "Enter valid price"; return }

        val amountPaid  = binding.etAmountPaid.text.toString().toDoubleOrNull() ?: 0.0
        val total       = qty * unitPrice
        val balance     = (total - amountPaid).coerceAtLeast(0.0)
        val cylinderType = cylinderTypes.getOrNull(binding.spinnerCylinderType.selectedItemPosition)?.value ?: ""
        val saleType     = saleTypes.getOrNull(binding.spinnerSaleType.selectedItemPosition)?.value ?: ""

        val sale = Sale(
            id           = if (editingSaleId != -1L) editingSaleId else 0,
            customerName = customerName,
            customerPhone= binding.etCustomerPhone.text.toString().trim(),
            cylinderType = cylinderType,
            saleType     = saleType,
            quantity     = qty,
            unitPrice    = unitPrice,
            totalAmount  = total,
            amountPaid   = amountPaid,
            balance      = balance,
            notes        = binding.etNotes.text.toString().trim()
        )

        lifecycleScope.launch {
            if (editingSaleId != -1L) repo.updateSale(sale)
            else {
                repo.insertSale(sale)
                updateInventoryAfterSale(sale)
            }
            findNavController().popBackStack()
        }
    }

    private suspend fun updateInventoryAfterSale(sale: Sale) {
        val saleTypeLower = sale.saleType.lowercase()
        if (saleTypeLower.contains("deposit") || saleTypeLower.contains("return")) return
        val inv = repo.getInventoryByType(sale.cylinderType) ?: return
        repo.updateInventory(inv.copy(
            fullCylinders  = (inv.fullCylinders - sale.quantity).coerceAtLeast(0),
            emptyCylinders = inv.emptyCylinders + sale.quantity,
            lastUpdated    = System.currentTimeMillis()
        ))
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
