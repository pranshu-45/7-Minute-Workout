package com.example.a7minworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.a7minworkoutapp.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {
    private var binding: ActivityFinishBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.finishToolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        val historyDao = (application as WorkoutApp).db.historyDao()
        addDateToDatabase(historyDao)
        binding?.finishToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener{
            finish()
        }
    }

    private fun addDateToDatabase(historyDao: HistoryDao){
        val c=Calendar.getInstance()
        val dateTime = c.time
        Log.e("Date",""+dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e("Formatted Date : ",""+date)

        lifecycleScope.launch {
            if(date!=null){
                historyDao.insert(HistoryEntity(date))
                Log.e("Date ","Added...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}