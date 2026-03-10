package com.lpgagency.accounting.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.databinding.FragmentDashboardBinding
import com.lpgagency.accounting.utils.FormatUtil
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        loadStats()

        binding.btnNewSale.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_addSale) }
        binding.btnNewExpense.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_addExpense) }
        binding.btnInventory.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_inventory) }
        binding.btnBackup.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_backup) }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val todayStart = FormatUtil.todayStart()
            val monthStart = FormatUtil.monthStart()

            val todaySales = repo.getTodaySales(todayStart)
            val todayExpenses = repo.getTodayExpenses(todayStart)
            val outstanding = repo.getTotalOutstanding()
            val fullCylinders = repo.getTotalFullCylinders()
            val emptyCylinders = repo.getTotalEmptyCylinders()

            binding.tvTodaySales.text = FormatUtil.formatCurrency(todaySales)
            binding.tvTodayExpenses.text = FormatUtil.formatCurrency(todayExpenses)
            binding.tvOutstanding.text = FormatUtil.formatCurrency(outstanding)
            binding.tvTodayProfit.text = FormatUtil.formatCurrency(todaySales - todayExpenses)
            binding.tvFullCylinders.text = fullCylinders.toString()
            binding.tvEmptyCylinders.text = emptyCylinders.toString()
        }

        // Recent sales list
        repo.getRecentSales().observe(viewLifecycleOwner) { sales ->
            val adapter = RecentSalesAdapter(sales)
            binding.rvRecentSales.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
