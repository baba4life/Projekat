package com.example.nilme

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        sign_btn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        login_btn.setOnClickListener {
            loginUser()
        }

        fun onStart() {
            super.onStart()
            if (FirebaseAuth.getInstance().currentUser != null){
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loginUser() {
        val email = email_login.text.toString()
        val pass = password_login.text.toString()

        when {

            TextUtils.isEmpty(email) -> Toast.makeText(this,"email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(pass) -> Toast.makeText(this,"pass is required", Toast.LENGTH_LONG).show()
         else -> {
             val progresDialog = ProgressDialog(this@SignInActivity)

             progresDialog.setTitle("Log in")
             progresDialog.setMessage("This may take a while")
             progresDialog.setCanceledOnTouchOutside(false)
             progresDialog.show()

             val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

             mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener { task->
                 if(task.isSuccessful){
                     progresDialog.dismiss()

                     val intent = Intent(this@SignInActivity, MainActivity::class.java)
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                     startActivity(intent)
                     finish()
                 }else{
                     val message = task.exception!!.toString()
                     Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                     FirebaseAuth.getInstance().signOut()
                     progresDialog.dismiss()
                 }
             }
         }
        }
    }
}