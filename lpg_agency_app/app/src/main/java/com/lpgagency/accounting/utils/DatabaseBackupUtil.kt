package com.lpgagency.accounting.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.lpgagency.accounting.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object DatabaseBackupUtil {

    private const val BACKUP_FILE_PREFIX = "lpg_agency_backup"

    /**
     * Export the Room DB file to external Downloads folder.
     * Returns the Uri of the exported file (shareable).
     */
    suspend fun exportDatabase(context: Context): Uri? = withContext(Dispatchers.IO) {
        try {
            // Close all connections first
            AppDatabase.getInstance(context).close()

            val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            if (!dbFile.exists()) return@withContext null

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "${BACKUP_FILE_PREFIX}_$timestamp.db"

            // Save to app's external files dir (no permissions needed on API 29+)
            val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: return@withContext null
            externalDir.mkdirs()

            val backupFile = File(externalDir, backupFileName)
            FileInputStream(dbFile).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            // Return a shareable URI via FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Import a .db file from the given URI and replace the current database.
     */
    suspend fun importDatabase(context: Context, sourceUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            // Close DB
            AppDatabase.getInstance(context).close()

            val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            dbFile.parentFile?.mkdirs()

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            } ?: return@withContext false

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * List previously exported backup files.
     */
    fun listBackups(context: Context): List<File> {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: return emptyList()
        return externalDir.listFiles { file ->
            file.name.startsWith(BACKUP_FILE_PREFIX) && file.name.endsWith(".db")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}
