package com.example.a7minworkoutapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(historyEntity: HistoryEntity)

    @Delete
    suspend fun delete(historyEntity: HistoryEntity)

    @Query("select * from `history-table` where date=:date")
    fun fetchRecord(date:String): Flow<HistoryEntity>

    @Query("select * from `history-table`")
    fun fetchAllRecords(): Flow<List<HistoryEntity>>

    @Query("delete from `history-table`")
    suspend fun clearAllRecords()
}