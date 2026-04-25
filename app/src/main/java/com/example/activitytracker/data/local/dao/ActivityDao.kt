package com.example.activitytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.activitytracker.data.local.entity.ActivityEntry

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_entries")
    fun getAll(): List<ActivityEntry>

    @Query("SELECT * FROM activity_entries WHERE id = :id")
    fun findById(id: Int): ActivityEntry?

    @Insert
    fun insertAll(vararg activityEntry: ActivityEntry)

    @Delete
    fun delete(activityEntry: ActivityEntry)
}