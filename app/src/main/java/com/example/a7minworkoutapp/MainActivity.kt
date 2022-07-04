package com.example.a7minworkoutapp

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.a7minworkoutapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding?=null
    private var firebaseAuth : FirebaseAuth? =null
    private var database : FirebaseFirestore? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firebaseAuth =  FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser
        database = FirebaseFirestore.getInstance()
        val uid = firebaseUser?.uid
        val historyChangeDao = (application as WorkoutApp).cdb.historyChangeDao()

        setupUsernameDisplay(uid)

        binding?.btnLogout?.setOnClickListener {
            if(checkForInternet(this)){
                lifecycleScope.launch{
                    val changes = ArrayList(historyChangeDao.fetchAllChanges())
                    commitChanges(firebaseUser?.uid,changes)
                    historyChangeDao.clearAllChanges()
                }

                changeLoginStatusToFalse(firebaseUser?.uid)
            }
            else{
                Toast.makeText(this, "Please make sure you have network connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.flStart?.setOnClickListener{

            val intent = Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.flBmi?.setOnClickListener{

            val intent = Intent(this,BMIActivity::class.java)
            startActivity(intent)
        }
        binding?.flHistory?.setOnClickListener{

            val intent = Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun commitChanges(tokenId: String?, changes : ArrayList<HistoryChangeEntity>) {
        val ref = tokenId?.let { database?.collection("Users")?.document(it) }
        val historyRef = ref?.collection("history")?.document("records")

        for(change in changes){
            if(change.action==1){
                var entry = hashMapOf<String,String>()
                entry[change.tokenId]="true"
                historyRef?.set(entry, SetOptions.merge())?.addOnCompleteListener{
                    if(it.isSuccessful){
                        Log.e("Merge Successful","$change")
                    }
                    else{
                        Log.e("Merge Failed","$change")
                    }
                }
            }
            else if(change.action==0){
                historyRef?.update(change.tokenId, FieldValue.delete())?.addOnCompleteListener{
                    if(it.isSuccessful){
                        Log.e("Delete Successful","$change")
                    }
                    else{
                        Log.e("Delete Failed","$change")
                    }
                }
            }
        }
    }

    private fun setupUsernameDisplay(uid:String?) {
        uid?.let { database?.collection("Users")?.document(it)?.get() }?.addOnSuccessListener{ document->
            if(document!=null){
                binding?.tvUserName?.text = document["Username"].toString()
            }
        }
    }

    override fun onStart(){
        super.onStart()
        if(firebaseAuth==null){
            firebaseAuth =  FirebaseAuth.getInstance()
        }
        val firebaseUser = firebaseAuth?.currentUser
        if(firebaseUser==null){
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun changeLoginStatusToFalse(tokenId:String?){
        val ref = tokenId?.let { database?.collection("Users")?.document(it) }
        var status:Int = 0
        ref?.update("LoggedIn","false")?.addOnSuccessListener {
            logoutOfDatabase()
        }?.addOnFailureListener{
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutOfDatabase(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}