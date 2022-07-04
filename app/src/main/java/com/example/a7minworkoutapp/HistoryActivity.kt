package com.example.a7minworkoutapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkoutapp.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding : ActivityHistoryBinding? = null
    private var firebaseAuth : FirebaseAuth? =null
    private var database : FirebaseFirestore? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firebaseAuth =  FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser
        database = FirebaseFirestore.getInstance()

        setSupportActionBar(binding?.historyToolbar)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "History"
        }
        binding?.historyToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        val historyChangeDao = (application as WorkoutApp).cdb.historyChangeDao()


        lifecycleScope.launch{
            historyDao.fetchAllRecords().collect {
                val records = ArrayList(it)
//                Toast.makeText(this@HistoryActivity,"${records.size}",Toast.LENGTH_SHORT).show()
//                for(i in records) {
//                    Log.e("record", "" + i)
                setupRecyclerViewRecords(records,historyDao,historyChangeDao)
                }
            }
        }

    private fun setupRecyclerViewRecords(records : ArrayList<HistoryEntity>,historyDao: HistoryDao,historyChangeDao: HistoryChangeDao) {
        val adapter = HistoryItemAdaptor(records
        ) { deleteDate -> deleteRecord(deleteDate, historyDao, historyChangeDao) }
        binding?.rvHistoryRecords?.adapter = adapter
        binding?.rvHistoryRecords?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }

    private fun deleteRecord(date:String,historyDao: HistoryDao,historyChangeDao: HistoryChangeDao){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record?")
        builder.setIcon(R.drawable.ic_alert)

        lifecycleScope.launch {
            historyDao.fetchRecord(date).collect{
                if(it != null){
                    //Toast.makeText(this@MainActivity,"setting builder message",Toast.LENGTH_SHORT).show()
                    builder.setMessage("Are you sure you want to delete this entry.")
                }
            }
        }

        builder.setPositiveButton("Yes"){dialogInterface,_->
            lifecycleScope.launch{
                historyDao.delete(HistoryEntity(date))
                Toast.makeText(applicationContext,"Record deleted successfully",Toast.LENGTH_SHORT).show()
                historyChangeDao.insert(HistoryChangeEntity(tokenId = date,action = 0))
                dialogInterface.dismiss()
            }
        }
        builder.setNegativeButton("No"){dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}