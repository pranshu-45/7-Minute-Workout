package com.example.a7minworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minworkoutapp.databinding.ActivityBmiBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round

class BMIActivity : AppCompatActivity() {
    private var binding:ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.BMIToolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }
        binding?.BMIToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        setUpInitialContentView()

        binding?.btnCalculate?.setOnClickListener{
            calculateBMI()
        }
    }

    private fun calculateBMI(){
        val weight = binding?.etWeight?.text.toString()
        val height = binding?.etHeight?.text.toString()

        if(weight.isEmpty()){
            Toast.makeText(this,"Please enter your weight",Toast.LENGTH_SHORT).show()
        }
        else if(weight.toDouble()==0.0){
            Toast.makeText(this,"Please enter valid weight",Toast.LENGTH_SHORT).show()
        }
        else if(height.isEmpty()){
            Toast.makeText(this,"Please enter your height",Toast.LENGTH_SHORT).show()
        }
        else if(height.toDouble()==0.0){
            Toast.makeText(this,"Please enter valid height",Toast.LENGTH_SHORT).show()
        }
        else{
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            var heightInM=height.toDouble()/100;
            var weightInKg=weight.toDouble()
            var bmiValue=df.format(weightInKg/(heightInM*heightInM))
            //Toast.makeText(this,"$bmiValue",Toast.LENGTH_SHORT).show()

            setBmiDisplay(bmiValue)
        }

    }

    private fun setBmiDisplay(bmiValue: String){
        binding?.tvBmiTitle?.visibility= View.VISIBLE
        binding?.tvBmiValue?.visibility= View.VISIBLE
        binding?.tvBmiStatus?.visibility= View.VISIBLE
        binding?.tvBmiAdvise?.visibility= View.VISIBLE

        lateinit var bmiStatus: String
        lateinit var bmiAdvise: String

        when(bmiValue.toDouble()){
            in 0.00..14.99->{
                bmiStatus="Very Severely Underweight"
                bmiAdvise="Oops! You really need to take care of yourself! Nutrition yourself properly!"
            }
            in 15.00..15.99->{
                bmiStatus="Severely Underweight"
                bmiAdvise="Oops! You really need to take care of yourself! Nutrition yourself properly!"
            }
            in 16.00..18.49->{
                bmiStatus="Underweight"
                bmiAdvise="Oops! You really need to take care of yourself! Nutrition yourself properly!"
            }
            in 18.50..24.99->{
                bmiStatus="Normal"
                bmiAdvise="Congratulations! You are in a good shape, theoretically!!!"
            }
            in 25.00..29.99->{
                bmiStatus="Overweight"
                bmiAdvise="Oops! You really need to take care of yourself! Workout with us!!!"
            }
            in 30.00..34.99->{
                bmiStatus="Obesity Class 1 (Moderately Obese)"
                bmiAdvise="Oops! You really need to take care of yourself! Workout with us!!!"
            }
            in 35.00..39.99->{
                bmiStatus="Obesity Class 2 (Severely Obese)"
                bmiAdvise="OMG! You are in a very dangerous condition! Act now!"
            }
            else->{
                bmiStatus="Obesity Class 3 (Very Severely Obese)"
                bmiAdvise="OMG! You are in a very dangerous condition! Act now!"
            }
        }
        binding?.tvBmiValue?.text = bmiValue
        binding?.tvBmiStatus?.text = bmiStatus
        binding?.tvBmiAdvise?.text = bmiAdvise
    }

    private fun setUpInitialContentView(){
        binding?.tvBmiTitle?.visibility= View.INVISIBLE
        binding?.tvBmiValue?.visibility= View.INVISIBLE
        binding?.tvBmiStatus?.visibility= View.INVISIBLE
        binding?.tvBmiAdvise?.visibility= View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}