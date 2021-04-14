package com.example.nilme

import android.app.ProgressDialog
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignUpActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signin_btn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
        signup_btn.setOnClickListener {
            CreateAccount()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun CreateAccount() {
        val fullName = full_name_signup.text.toString()
        val userName = username_signup.text.toString()
        val email = email_signup.text.toString()
        val pass = password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"full name is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"userName is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"email is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(pass) -> Toast.makeText(this,"pass is required",Toast.LENGTH_LONG).show()
            else ->{
                val progresDialog = ProgressDialog(this@SignUpActivity)

                progresDialog.setTitle("Sign up")
                progresDialog.setMessage("This may take a while")
                progresDialog.setCanceledOnTouchOutside(false)
                progresDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                    task -> if(task.isSuccessful){
                                saveUserInfo(fullName,userName,email,progresDialog)

                                }else{
                                val message = task.exception!!.toString()
                                Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                                mAuth.signOut()
                                progresDialog.dismiss()
                            }
                }
            }
        }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progresDialog:ProgressDialog ) {
        val currentUserID =FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"]= currentUserID
        userMap["fullname"]= fullName.toLowerCase()
        userMap["username"]= userName.toLowerCase()
        userMap["email"]= email
        userMap["bio"]= "Hey i m creating instagram clone app"
        userMap["image"]="https://firebasestorage.googleapis.com/v0/b/social-media-clone-app-aed2a.appspot.com/o/default%20image%2Favatar.png?alt=media&token=eb0dc87f-70e5-4b9c-97d4-f1fd23434b1d"


        userRef.child(currentUserID).setValue(userMap).addOnCompleteListener { task->if(task.isSuccessful){
                progresDialog.dismiss()
            Toast.makeText(this,"Account has been created successfully",Toast.LENGTH_LONG).show()

            FirebaseDatabase.getInstance().reference
                    //uzima uid od usera
                    .child("Follow").child(currentUserID)
                    // doda se u following list odgovarajuci user
                    .child("Following").child(currentUserID)
                    .setValue(true)

            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            }else
        {

            val message = task.exception!!.toString()
            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()
            progresDialog.dismiss()
        }
        }


    }
}