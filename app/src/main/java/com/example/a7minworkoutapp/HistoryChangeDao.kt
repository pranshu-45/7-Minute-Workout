package com.example.a7minworkoutapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryChangeDao {
    @Insert
    suspend fun insert(historyChangeEntity: HistoryChangeEntity)

    @Query("select * from `historyChange-table`")
    suspend fun fetchAllChanges(): List<HistoryChangeEntity>

    @Query("delete from `historyChange-table`")
    suspend fun clearAllChanges()
}