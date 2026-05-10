package com.example.activitytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.activitytracker.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT DISTINCT activityDate FROM activity_entries ORDER BY activityDate DESC")
    fun getDates(): Flow<List<LocalDate>>

    @Query("SELECT * FROM activity_entries WHERE id = :id")
    suspend fun findById(id: String): ActivityEntity?

    @Insert
    suspend fun insert(entry: ActivityEntity)

    @Delete
    suspend fun delete(activityEntity: ActivityEntity)
}