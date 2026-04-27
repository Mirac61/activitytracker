package com.example.activitytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.activitytracker.data.local.entity.ActivityEntry
import kotlinx.coroutines.flow.Flow

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ActivityEntry>>

    @Query("SELECT * FROM activity_entries WHERE id = :id")
    suspend fun findById(id: Int): ActivityEntry?

    @Insert
    suspend fun insert(entry: ActivityEntry)

    suspend fun safeInsert(entry: ActivityEntry) {
        if (entry.name.isNotBlank()) {
            insert(entry)
        }
        else{
            android.util.Log.e("ActivityDao", "Name ist leer")
        }
    }

    @Delete
    suspend fun delete(activityEntry: ActivityEntry)
}