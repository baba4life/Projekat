package com.example.nilme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_up.*

class SigninActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin2)
        signup_btn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }
}