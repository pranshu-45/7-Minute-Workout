package com.example.a7minworkoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkoutapp.databinding.ActivityExerciseBinding
import com.example.a7minworkoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExerciseBinding? = null
    private var restTimer:CountDownTimer?=null
    private var restProgress:Int=0
    private var maxRestTime:Int=1
    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress:Int=0
    private var maxExerciseTime:Int=1
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition : Int=-1
    private var timeBeforeUpcomingExerciseAlert : Int=7
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null

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
            customDialogForBackButton()
        }

        exerciseList=Constants.defaultExerciseList()

        setupRestView()

        tts= TextToSpeech(this,this)

        setUpExerciseStatusRV()
        
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this@ExerciseActivity)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
//        super.onBackPressed()
    }

    private fun setUpExerciseStatusRV() {
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setUpExerciseView() {
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        //toSpeak(exerciseList!![currentExercisePosition].getName())

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
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter?.notifyItemChanged(currentExercisePosition)

                val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                startActivity(intent)
                finish()

                if(currentExercisePosition<exerciseList!!.size-1){
                    setupRestView()
                }
                else{
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    private fun setupRestView() {

        if(currentExercisePosition>=0){
            try {
                val soundURI = Uri.parse(
                    "android.resource://com.example.a7minworkoutapp/" +
                            R.raw.exercise_end_notification
                )
                player = MediaPlayer.create(applicationContext, soundURI)
                player?.isLooping = false
                player?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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
                voiceAlerts(millisUntilFinished)
            }

            override fun onFinish() {
                //Toast.makeText(this@ExerciseActivity,"Now we will start the exercise",Toast.LENGTH_SHORT).show()
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter?.notifyItemChanged(currentExercisePosition)
                setUpExerciseView()
            }

        }.start()
    }

    private fun voiceAlerts(millisUntilFinished:Long){
        // Toast.makeText(this,"in voice alerts",Toast.LENGTH_SHORT).show()
        if(millisUntilFinished<=(timeBeforeUpcomingExerciseAlert*1000).toLong() &&
            millisUntilFinished>=((timeBeforeUpcomingExerciseAlert-1)*1000).toLong()){
            toSpeak(binding?.tvUpcomingExerciseTitle?.text.toString())
            toSpeak(binding?.tvUpcomingExerciseName?.text.toString())
        }
        if(millisUntilFinished<=4*1000){
            var lastCall: Int = (millisUntilFinished/1000).toInt()
            if(lastCall==0){
                toSpeak("Start")
            }
            else{
                toSpeak(lastCall.toString())
            }
        }
    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS){
            var result = tts?.setLanguage(Locale.US)
            if(result==TextToSpeech.LANG_NOT_SUPPORTED ||
                result==TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS","The language specified is not supported")
            }
        }
        else{
            Log.e("TTS","Initialization Failed!")
        }
    }

    private fun toSpeak(text:String){
        tts?.speak(text,TextToSpeech.QUEUE_ADD,null,"")
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

        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player!=null){
            player!!.stop()
        }

        binding = null
    }


}