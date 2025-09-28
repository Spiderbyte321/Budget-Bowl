package com.jacob.budgetbowl

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
// when button is pressed take in values and authenticate for now toast when failed



private lateinit var btnRedirect: Button

lateinit var etUserEmail: EditText

private lateinit var etPassword: EditText

lateinit var btnLogin: Button

lateinit var authenticator: FirebaseAuth

//we store the authenticator as an object so that we can easily acces it
//I'm starting to dislike how android studio operates
//for now do the basic login functionality we'll add anims and stuff later
//ok the listener stuff is actually pretty cool maybe I like it a bit more
//setup to make switching between firebase and room db better
//nope just use firebase to login and room for the entries


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom)
            insets
        }

        //apply a new inset which used to control UI layout from the layout we open I think

        //get the references to our components and assign them to our fields
        //later
        // later is now :{

        btnLogin = findViewById(R.id.brnLogin)//misspelled btn ..... dammit
        etUserEmail = findViewById(R.id.etUserEmail)
        etPassword = findViewById(R.id.etUserPassword)



        authenticator = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
        login()
        }
        //after we click the button call our login method
    }

    //method to redirect to sign up screen



    private fun login(){
        val UsereEmail = etUserEmail.text.toString()
        val UserPassword = etPassword.text.toString()

        authenticator.signInWithEmailAndPassword(UsereEmail,UserPassword).addOnCompleteListener(this){
        if(it.isSuccessful) {
            Toast.makeText(this,"Logged in", Toast.LENGTH_SHORT).show()
        }
            else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        } }

        //call signin method on the authenticator and when it's done check if it's true or false
    }



}