package com.lpgagency.accounting.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lpgagency.accounting.databinding.FragmentBackupBinding
import com.lpgagency.accounting.utils.DatabaseBackupUtil
import kotlinx.coroutines.launch

class BackupFragment : Fragment() {
    private var _binding: FragmentBackupBinding? = null
    private val binding get() = _binding!!
    private val PICK_DB_FILE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBackupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnExport.setOnClickListener { exportDatabase() }
        binding.btnImport.setOnClickListener { openFilePicker() }

        loadBackupList()
    }

    private fun exportDatabase() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val uri = DatabaseBackupUtil.exportDatabase(requireContext())
            binding.progressBar.visibility = View.GONE
            if (uri != null) {
                // Share the file
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/octet-stream"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "LPG Agency Database Backup")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Database Backup"))
                loadBackupList()
            } else {
                Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/octet-stream"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_DB_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_DB_FILE && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            confirmImport(uri)
        }
    }

    private fun confirmImport(uri: Uri) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Import Database?")
            .setMessage("This will REPLACE all current data with the selected backup. This cannot be undone. Continue?")
            .setPositiveButton("Import") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val success = DatabaseBackupUtil.importDatabase(requireContext(), uri)
                    binding.progressBar.visibility = View.GONE
                    if (success) {
                        Toast.makeText(requireContext(), "✓ Database imported successfully! Please restart the app.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Import failed. Make sure the file is a valid backup.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun loadBackupList() {
        val backups = DatabaseBackupUtil.listBackups(requireContext())
        binding.tvBackupList.text = if (backups.isEmpty()) {
            "No backups found."
        } else {
            "Saved backups:\n" + backups.joinToString("\n") { "• ${it.name}" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
