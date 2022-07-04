package com.example.a7minworkoutapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historyChange-table")
data class HistoryChangeEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val tokenId: String,
    val action: Int
)