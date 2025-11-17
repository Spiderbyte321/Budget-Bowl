package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

private lateinit var btnRedirect: Button

lateinit var etUserEmail: EditText

private lateinit var etPassword: EditText

lateinit var btnLogin: Button

lateinit var authenticator: FirebaseAuth

    private lateinit var imgTopDecor: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom)
            insets
        }


        btnLogin = findViewById(R.id.btnLogin)
        btnRedirect= findViewById(R.id.btnBack)
        etUserEmail = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etUserPassword)
        imgTopDecor = findViewById(R.id.imgTopDecor)
        imgTopDecor.setImageResource(R.drawable.topimagestretched)

        authenticator = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
        login()
        }

        btnRedirect.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //after we click the button call our login method
    }

    private fun login(){

        if(etUserEmail.text.isBlank()||etPassword.text.isBlank())
        {
            Toast.makeText(this,"All fields need to be filled in", Toast.LENGTH_SHORT).show()
            return
        }

        val UsereEmail = etUserEmail.text.toString()
        val UserPassword = etPassword.text.toString()

        authenticator.signInWithEmailAndPassword(UsereEmail,UserPassword).addOnCompleteListener(this){
        if(it.isSuccessful) {
            Toast.makeText(this,"Logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
            else{
            Toast.makeText(this, "Log in Failed", Toast.LENGTH_SHORT).show()
        } }

        //call signin method on the authenticator and when it's done check if it's true or false
    }



}