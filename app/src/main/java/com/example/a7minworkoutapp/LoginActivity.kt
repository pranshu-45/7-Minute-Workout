package com.example.a7minworkoutapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a7minworkoutapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity() {

    private var firebaseAuth :FirebaseAuth? =null
    private var database : FirebaseFirestore? = null
    private var binding: ActivityLoginBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(firebaseAuth==null){
            firebaseAuth =  FirebaseAuth.getInstance()
        }
        database = FirebaseFirestore.getInstance()
        val historyDao = (application as WorkoutApp).db.historyDao()

        setSupportActionBar(binding?.LAToolbar)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Login"
        }
        binding?.LAToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.tvRegisterLA?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding?.tvForgotPasswordLA?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        binding?.btnLoginLA?.setOnClickListener {
            userLogin(it,historyDao)
        }
    }

    private fun changeLoginStatusToTrue(tokenId:String?){
        val ref = tokenId?.let { database?.collection("Users")?.document(it) }
        //Toast.makeText(this,"$ref",Toast.LENGTH_SHORT).show()
        ref?.update("LoggedIn","true")?.addOnCompleteListener { taskId ->
            if (taskId.isSuccessful) {
                //Toast.makeText(this, "User logged in Database", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, taskId.exception!!.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun userLogin(view: View,historyDao : HistoryDao) {
        when {
            TextUtils.isEmpty(binding?.etLoginEmailLA?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(binding?.etLoginPasswordLA?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {

                val email: String = binding?.etLoginEmailLA?.text.toString().trim { it <= ' ' }
                val password: String = binding?.etLoginPasswordLA?.text.toString().trim { it <= ' ' }

                // Log-In using FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val uid = FirebaseAuth.getInstance().uid

                            val docRef = database?.collection("Users")?.document(uid!!)
                            docRef?.get()
                                ?.addOnSuccessListener { document ->
                                    if (document != null && document["LoggedIn"]=="true") {
                                        //Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                        val msg1="User is already logged in from some other device."
                                        val msg2 = "To login in from here, " + "ensure user is logged out from other device"
                                        Toast.makeText(this@LoginActivity,msg1,Toast.LENGTH_SHORT).show()
                                        Toast.makeText(this@LoginActivity,msg2,Toast.LENGTH_SHORT).show()
                                        firebaseAuth?.signOut()
                                    }
                                    else{
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "You are logged in successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        /**
                                         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                                         * and send him to Main Screen with user id and email that user have used for registration.
                                         */

                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra(
                                            "user_id",
                                            FirebaseAuth.getInstance().currentUser!!.uid
                                        )
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        changeLoginStatusToTrue(uid)
                                        setupHistoryDatabase(uid,historyDao)
                                        finish()
                                    }
                                }
                        } else {

                            // If the login is not successful then show error message.
                            Toast.makeText(
                                this@LoginActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun setupHistoryDatabase(tokenId: String?,historyDao: HistoryDao) {
        val ref = tokenId?.let { database?.collection("Users")?.document(it) }
        val historyRef = ref?.collection("history")?.document("records")

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.getDefault())
        val records = ArrayList<Date>()
        historyRef?.get()?.addOnSuccessListener {
            val data = it.data
            if (data != null) {
                for(record in data){
                    val date = sdf.parse(record.key)
                    if (date != null) {
                        records.add(date)
                        Log.e("Date", "$date")
                    }
                }
                Log.e("size","${records.size}")
            }
            records.sort()
            lifecycleScope.launch{
                Log.e("Check","in lifecycle scope")
                for(record in records){
                    Log.e("Check", "$record")
                    val date = sdf.format(record)
                    historyDao.insert(HistoryEntity(date))
                }
            }
        }


    }
}