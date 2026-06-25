package com.example.activitytracker.data.repository

import com.example.activitytracker.data.local.dao.ReminderDao
import com.example.activitytracker.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {
    fun getAll(): Flow<List<ReminderEntity>> = dao.getAll()
    suspend fun getAllOnce(): List<ReminderEntity> = dao.getAllOnce()
    suspend fun getByRequestCode(requestCode: Int): ReminderEntity? = dao.getByRequestCode(requestCode)
    suspend fun count(): Int = dao.count()
    suspend fun insert(reminder: ReminderEntity): Long = dao.insert(reminder)
    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)
}
