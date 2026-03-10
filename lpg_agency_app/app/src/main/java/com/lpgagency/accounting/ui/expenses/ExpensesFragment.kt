package com.lpgagency.accounting.ui.expenses

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.models.Expense
import com.lpgagency.accounting.databinding.FragmentExpensesBinding
import com.lpgagency.accounting.utils.FormatUtil
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository
    private lateinit var adapter: ExpensesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        adapter = ExpensesAdapter(emptyList()) { expense -> showOptions(expense) }
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        repo.getAllExpenses().observe(viewLifecycleOwner) { expenses ->
            adapter.updateData(expenses)
            val total = expenses.sumOf { it.amount }
            binding.tvTotal.text = "Total: ${FormatUtil.formatCurrency(total)}"
            binding.tvEmptyState.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_expenses_to_addExpense)
        }
    }

    private fun showOptions(expense: Expense) {
        val options = arrayOf("Delete")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(expense.description)
            .setItems(options) { _, which ->
                if (which == 0) {
                    lifecycleScope.launch { repo.deleteExpense(expense) }
                }
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
