package com.example.a7minworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.a7minworkoutapp.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? =null
    private var database : FirebaseFirestore? = null
    private var firebaseAuth :FirebaseAuth? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setSupportActionBar(binding?.RAToolbar)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Register"
        }
        binding?.RAToolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        if(firebaseAuth==null){
            firebaseAuth =  FirebaseAuth.getInstance()
        }
        database = FirebaseFirestore.getInstance()
        setContentView(binding?.root)

        binding?.tvLoginRA?.setOnClickListener{
            onBackPressed()
        }

        binding?.btnRegisterRA?.setOnClickListener {
            when {
                TextUtils.isEmpty(binding?.etRegisterEmailRA?.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding?.etRegisterPasswordRA?.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val username: String = binding?.etRegisterUsernameRA?.text.toString().trim { it <= ' ' }
                    val email: String = binding?.etRegisterEmailRA?.text.toString().trim { it <= ' ' }
                    val password: String = binding?.etRegisterPasswordRA?.text.toString().trim { it <= ' ' }

                    // Create an instance and create a register a user with email and password.
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {

                                    var user = User(username,email,password,true)
                                    addUser(task.result!!.user!!.uid,user)

                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "You are registered successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // If the registering is not successful then show error message.
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                }
            }
        }

        binding?.tvLoginRA?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addUser(tokenId:String,user:User){
        var entry = hashMapOf<String,String>()
        entry["Username"] = user.username
        entry["Email"] = user.emailId
        entry["Password"] = user.password
        entry["LoggedIn"] = user.isLoggedIn.toString()
        database?.collection("Users")?.document(tokenId)?.set(entry)?.addOnCompleteListener { taskId ->
            if (taskId.isSuccessful) {
                //Toast.makeText(this, "User added to Database", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this,taskId.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}