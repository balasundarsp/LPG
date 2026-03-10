package com.lpgagency.accounting.ui.expenses

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.db.SettingCategory
import com.lpgagency.accounting.data.models.AppSetting
import com.lpgagency.accounting.data.models.Expense
import com.lpgagency.accounting.databinding.FragmentAddExpenseBinding
import kotlinx.coroutines.launch

class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository

    private var categories: List<AppSetting>     = emptyList()
    private var paymentMethods: List<AppSetting>  = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        lifecycleScope.launch {
            categories     = repo.getSettingsSync(SettingCategory.EXPENSE_CATEGORY)
            paymentMethods = repo.getSettingsSync(SettingCategory.PAYMENT_METHOD)

            val catAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, categories.map { it.value })
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = catAdapter

            val payAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, paymentMethods.map { it.value })
            payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPayment.adapter = payAdapter
        }

        binding.btnSave.setOnClickListener   { saveExpense() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun saveExpense() {
        val description = binding.etDescription.text.toString().trim()
        if (description.isEmpty()) { binding.etDescription.error = "Description required"; return }
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        if (amount == null || amount <= 0) { binding.etAmount.error = "Enter valid amount"; return }

        val expense = Expense(
            category      = categories.getOrNull(binding.spinnerCategory.selectedItemPosition)?.value ?: "",
            description   = description,
            amount        = amount,
            paymentMethod = paymentMethods.getOrNull(binding.spinnerPayment.selectedItemPosition)?.value ?: "",
            notes         = binding.etNotes.text.toString().trim()
        )
        lifecycleScope.launch { repo.insertExpense(expense); findNavController().popBackStack() }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
