package com.example.a7minworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.internal.view.SupportMenu
import com.example.a7minworkoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding : ActivityExerciseBinding? = null
    private var restTimer:CountDownTimer?=null
    private var restProgress:Int=0
    private var maxRestTime:Int=3
    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress:Int=0
    private var maxExerciseTime:Int=30
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition : Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //customised action bar and back button enabled
        setSupportActionBar(binding?.toolbarExercise)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }

        exerciseList=Constants.defaultExerciseList()

        setupRestView()
        
    }

    private fun setUpExerciseView() {
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        binding?.tvRestTitle?.visibility=View.INVISIBLE
        binding?.flRestProgressBar?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseTitle?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.INVISIBLE
        binding?.flExerciseProgressBar?.visibility=View.VISIBLE
        binding?.ivExerciseImage?.visibility=View.VISIBLE
        binding?.tvExerciseTitle?.visibility=View.VISIBLE

        binding?.ivExerciseImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseTitle?.text=exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {
        exerciseProgress = 0
        binding?.exerciseProgressBar?.max=maxExerciseTime

        exerciseTimer = object:CountDownTimer((maxExerciseTime*1000).toLong(),1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.exerciseProgressBar?.progress=maxExerciseTime-exerciseProgress
                binding?.exerciseTvTimer?.text=(maxExerciseTime-exerciseProgress).toString()
            }

            override fun onFinish() {
                // Toast.makeText(this@ExerciseActivity,"Now we will rest",Toast.LENGTH_SHORT).show()
                if(currentExercisePosition<exerciseList!!.size-1){
                    setupRestView()
                }
                else{
                    Toast.makeText(this@ExerciseActivity,"Congratulations!!!, You made it to the end" +
                            " of this 7 minute workout. Cheers...",Toast.LENGTH_LONG).show()
                }

            }
        }.start()
    }

    private fun setupRestView() {
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }
        binding?.tvRestTitle?.visibility=View.VISIBLE
        binding?.flRestProgressBar?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseTitle?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.VISIBLE
        binding?.flExerciseProgressBar?.visibility=View.INVISIBLE
        binding?.ivExerciseImage?.visibility=View.INVISIBLE
        binding?.tvExerciseTitle?.visibility=View.INVISIBLE

        binding?.tvUpcomingExerciseName?.text=exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        restProgress=0
        binding?.restProgressBar?.max=maxRestTime
        restTimer = object: CountDownTimer((maxRestTime*1000).toLong(),1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.restProgressBar?.progress = maxRestTime-restProgress
                binding?.restTvTimer?.text = (maxRestTime-restProgress).toString()
            }

            override fun onFinish() {
                //Toast.makeText(this@ExerciseActivity,"Now we will start the exercise",Toast.LENGTH_SHORT).show()
                currentExercisePosition++
                setUpExerciseView()
            }

        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()

        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }

        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }

        binding = null
    }
}