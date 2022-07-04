package com.example.a7minworkoutapp

import android.app.Application

class WorkoutApp: Application() {
    // For History Database
    val db by lazy{
        HistoryDatabase.getInstance(this)
    }
    // For History Change Database
    val cdb by lazy{
        HistoryChangeDatabase.getInstance(this)
    }
}