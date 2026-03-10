package com.lpgagency.accounting.ui.sales

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lpgagency.accounting.R
import com.lpgagency.accounting.data.db.AppRepository
import com.lpgagency.accounting.data.models.Sale
import com.lpgagency.accounting.databinding.FragmentSalesBinding
import com.lpgagency.accounting.utils.FormatUtil
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: AppRepository
    private lateinit var adapter: SalesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = AppRepository(requireContext())

        adapter = SalesAdapter(emptyList()) { sale -> showSaleOptions(sale) }
        binding.rvSales.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSales.adapter = adapter

        repo.getAllSales().observe(viewLifecycleOwner) { sales ->
            adapter.updateData(sales)
            binding.tvEmptyState.visibility = if (sales.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddSale.setOnClickListener {
            findNavController().navigate(R.id.action_sales_to_addSale)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    repo.searchSales(newText).observe(viewLifecycleOwner) { adapter.updateData(it) }
                } else {
                    repo.getAllSales().observe(viewLifecycleOwner) { adapter.updateData(it) }
                }
                return true
            }
        })
    }

    private fun showSaleOptions(sale: Sale) {
        val options = arrayOf("Edit", "Delete")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("${sale.customerName} - ${FormatUtil.formatCurrency(sale.totalAmount)}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val action = SalesFragmentDirections.actionSalesToAddSale(sale.id)
                        findNavController().navigate(action)
                    }
                    1 -> confirmDelete(sale)
                }
            }.show()
    }

    private fun confirmDelete(sale: Sale) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Sale?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch { repo.deleteSale(sale) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
