package com.example.a7minworkoutapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding : ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.historyToolbar)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "History"
        }
        binding?.historyToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()

        lifecycleScope.launch{
            historyDao.fetchAllRecords().collect {
                val records = ArrayList(it)
//                Toast.makeText(this@HistoryActivity,"${records.size}",Toast.LENGTH_SHORT).show()
//                for(i in records) {
//                    Log.e("record", "" + i)
                setupRecyclerViewRecords(records,historyDao)
                }
            }
        }

    private fun setupRecyclerViewRecords(records : ArrayList<HistoryEntity>,historyDao: HistoryDao) {
        val adapter = HistoryItemAdaptor(records
        ) { deleteDate -> deleteRecord(deleteDate, historyDao) }
        binding?.rvHistoryRecords?.adapter = adapter
        binding?.rvHistoryRecords?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }

    private fun deleteRecord(date:String,historyDao: HistoryDao){
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