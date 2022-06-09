package com.example.a7minworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.internal.view.SupportMenu
import com.example.a7minworkoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding : ActivityExerciseBinding? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)


        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}