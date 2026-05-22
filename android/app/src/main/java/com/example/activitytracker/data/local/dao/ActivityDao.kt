package com.example.activitytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT DISTINCT activityDate FROM activity_entries ORDER BY activityDate DESC")
    fun getDates(): Flow<List<LocalDate>>

    @Query("SELECT * FROM activity_entries WHERE status IN ('PENDING') ORDER BY createdAt DESC")
    suspend fun getSyncWorkQue(): List<ActivityEntity>

    @Query("UPDATE activity_entries SET status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT * FROM activity_entries WHERE id = :id")
    suspend fun findById(id: String): ActivityEntity?

    @Insert
    suspend fun insert(entry: ActivityEntity)

    @Delete
    suspend fun delete(activityEntity: ActivityEntity)
}