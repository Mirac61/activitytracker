package com.example.activitytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.activitytracker.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY CASE dayOfWeek WHEN 1 THEN 8 ELSE dayOfWeek END, hour, minute")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY CASE dayOfWeek WHEN 1 THEN 8 ELSE dayOfWeek END, hour, minute")
    suspend fun getAllOnce(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE requestCode = :requestCode LIMIT 1")
    suspend fun getByRequestCode(requestCode: Int): ReminderEntity?

    @Query("SELECT COUNT(*) FROM reminders")
    suspend fun count(): Int

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
