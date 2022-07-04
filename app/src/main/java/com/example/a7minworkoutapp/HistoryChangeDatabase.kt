package com.example.a7minworkoutapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryChangeEntity::class],version = 1)
abstract class HistoryChangeDatabase:RoomDatabase() {

    abstract fun historyChangeDao():HistoryChangeDao

    companion object {
        private var INSTANCE: HistoryChangeDatabase? = null

        fun getInstance(context: Context) : HistoryChangeDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryChangeDatabase::class.java,
                        "historyChange_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}