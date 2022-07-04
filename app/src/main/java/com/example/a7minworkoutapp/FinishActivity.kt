package com.example.a7minworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.a7minworkoutapp.databinding.ActivityFinishBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null
    private var firebaseAuth : FirebaseAuth? =null
    private var database : FirebaseFirestore? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firebaseAuth =  FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser
        database = FirebaseFirestore.getInstance()

        setSupportActionBar(binding?.finishToolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        val historyDao = (application as WorkoutApp).db.historyDao()
        val historyChangeDao = (application as WorkoutApp).cdb.historyChangeDao()
        updateDatabase(historyDao,historyChangeDao)
        binding?.finishToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener{
            finish()
        }
    }

    private fun updateDatabase(historyDao: HistoryDao,historyChangeDao: HistoryChangeDao){
        val c=Calendar.getInstance()
        val dateTime = c.time
        Log.e("Date",""+dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e("Formatted Date : ",""+date)

        lifecycleScope.launch {
            if(date!=null){
                historyDao.insert(HistoryEntity(date))
                historyChangeDao.insert(HistoryChangeEntity(tokenId = date, action = 1))
                Log.e("Date ","Added...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}