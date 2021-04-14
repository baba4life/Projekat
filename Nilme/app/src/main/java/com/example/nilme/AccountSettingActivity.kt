package com.example.nilme

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.nilme.Model.User
import com.example.nilme.fragments.ProfileFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var firebaseUser : FirebaseUser
    private var checher = ""
    private var myUrl=""
    private var imageUri: Uri? =null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")
        close_profile_btn.setOnClickListener{
            val intent = Intent (this@AccountSettingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        logout_btn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_btn.setOnClickListener{

            checher = "clicked"

            CropImage.activity().setAspectRatio(1,1).start(this@AccountSettingActivity)
        }

        save_info_profile_btn.setOnClickListener{
            // promena slike profila
            if(checher == "clicked")
            {
                uploadImageAndUpdateInfo()
            } //else - promena samo text filda
            else
            {
                updateUserInfoOnly()
            }
        }

        userInfo()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_avatar.setImageURI(imageUri)
        }

    }

    private fun updateUserInfoOnly(){

        when {
            full_name_profile.text.toString() == "" -> {
                Toast.makeText(this,"Please write full name first", Toast.LENGTH_LONG).show()

            }
            username_profile.text.toString() == "" -> {
                Toast.makeText(this,"Please write user name first", Toast.LENGTH_LONG).show()

            }
            bio_profile.text.toString() == "" -> {
                Toast.makeText(this,"Please write your bio first", Toast.LENGTH_LONG).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"]= full_name_profile.text.toString().toLowerCase()
                userMap["username"]= username_profile.text.toString().toLowerCase()
                userMap["bio"]= bio_profile.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this,"Account information has been updated successfully",Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile_icon).into(profile_avatar)
                    username_profile.setText(user!!.getUsername())
                    full_name_profile.setText(user!!.getFullname())
                    bio_profile.setText(user!!.getBio())


                }
            }

        })
    }

    private fun uploadImageAndUpdateInfo() {


        when {
            imageUri == null -> Toast.makeText(this, "Please select image first", Toast.LENGTH_LONG).show()

            full_name_profile.text.toString() == "" -> {
                Toast.makeText(this, "Please write full name first", Toast.LENGTH_LONG).show()

            }
            username_profile.text.toString() == "" -> {
                Toast.makeText(this, "Please write user name first", Toast.LENGTH_LONG).show()

            }
            bio_profile.text.toString() == "" -> {
                Toast.makeText(this, "Please write your bio first", Toast.LENGTH_LONG).show()
            }
            else->
            {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile ....")
                progressDialog.show()

                val fileref = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileref.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>>{task->
                    if(!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileref.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
                    if(task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl= downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"]= full_name_profile.text.toString().toLowerCase()
                        userMap["username"]= username_profile.text.toString().toLowerCase()
                        userMap["bio"]= bio_profile.text.toString().toLowerCase()
                        userMap["image"]=myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this,"Account information has been updated successfully",Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()

                    }
                    else{
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

}