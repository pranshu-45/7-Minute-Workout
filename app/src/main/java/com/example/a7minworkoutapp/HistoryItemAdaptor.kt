package com.example.a7minworkoutapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minworkoutapp.databinding.ItemHistoryRecordBinding

class HistoryItemAdaptor(private val items: ArrayList<HistoryEntity>,
                         private val deleteRecord:(date:String)->Unit

):RecyclerView.Adapter<HistoryItemAdaptor.ViewHolder>() {

    class ViewHolder(binding: ItemHistoryRecordBinding):RecyclerView.ViewHolder(binding.root){
        val recordNumber = binding.tvRecordNumber
        val recordDate = binding.tvRecordDate
        val clMain = binding.clMain
        val ivDelete = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryRecordBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = items[position]
        holder.recordNumber.text = (position+1).toString()
        holder.recordDate.text = record.date
        val context = holder.itemView.context

        holder.ivDelete.setOnClickListener{
            deleteRecord.invoke(record.date)
        }

        if(position%2==1){
            holder.clMain.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }
        else{
            holder.clMain.setBackgroundColor(ContextCompat.getColor(context, R.color.history_records_gray))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}