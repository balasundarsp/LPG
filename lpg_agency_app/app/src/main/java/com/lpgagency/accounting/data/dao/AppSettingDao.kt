package com.lpgagency.accounting.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lpgagency.accounting.data.models.AppSetting

@Dao
interface AppSettingDao {

    @Insert suspend fun insert(setting: AppSetting): Long
    @Update suspend fun update(setting: AppSetting)
    @Delete suspend fun delete(setting: AppSetting)

    @Query("SELECT * FROM app_settings WHERE category = :category ORDER BY sortOrder ASC, value ASC")
    fun getByCategory(category: String): LiveData<List<AppSetting>>

    @Query("SELECT * FROM app_settings WHERE category = :category ORDER BY sortOrder ASC, value ASC")
    suspend fun getByCategorySync(category: String): List<AppSetting>

    @Query("SELECT COUNT(*) FROM app_settings WHERE category = :category")
    suspend fun countByCategory(category: String): Int

    @Query("SELECT * FROM app_settings ORDER BY category ASC, sortOrder ASC")
    fun getAll(): LiveData<List<AppSetting>>
}
