package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth



//Reference Rochelles stuff for firebase stuff
class SignUpActivity: AppCompatActivity() {


    //do the sign up stuff
    //find out how to use room DB
    //cool I know how to use room and it sucks or maybe just SQL sucks either way I hate it


    private lateinit var etInputFullname: EditText

    private lateinit var etInputUserName: EditText
    private lateinit var etInputUserEmail: EditText

    private lateinit var etInputUserPassword: EditText

    private lateinit var etInputConfirmUserPassword: EditText

    private lateinit var btnSignUp: Button

    private lateinit var btnLoginRedirect: Button

    private lateinit var Authenticator: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Bind stuff


        etInputFullname = findViewById(R.id.etFullNameInput)
        etInputUserName = findViewById(R.id.etUserNameInput)
        etInputUserEmail =findViewById(R.id.etUserEmailInput)
        etInputUserPassword = findViewById(R.id.etUserPasswordInput)
        etInputConfirmUserPassword = findViewById(R.id.etConfirmUserPasswordInput)
        btnSignUp= findViewById(R.id.ConfirmSignUpBTN)
        btnLoginRedirect= findViewById(R.id.RedirectBTN)
        Authenticator = FirebaseAuth.getInstance()

        btnLoginRedirect.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener{
            SignUp()
        }
    }


    private fun SignUp()
    {
        val UserEmail = etInputUserEmail
        val UserPassword = etInputUserPassword
        val UserConfirmedPassword = etInputConfirmUserPassword



        //validate info before signing up
        if(UserEmail.text.isBlank()||UserPassword.text.isBlank()||UserConfirmedPassword.text.isBlank())
        {
            Toast.makeText(this,"All fields need to be filled in", Toast.LENGTH_SHORT).show()
            return
        }

        //Needs to be toString cause .text returns an object that can be edited
        // while toString gives us the actual string value

        if(UserPassword.text.toString()!=UserConfirmedPassword.text.toString())
        {
            Toast.makeText(this,"Confirm Password and password need to match", Toast.LENGTH_SHORT).show()
            return
        }

        //Now we sign up since we validated

        //for now we switch back to login screen later we'll go straight to the home menu
        //maybe instead of oncomplete use of success and on failure?
        //Add the user info we want to push to the database
        val intent = Intent(this, SetInitialBudgetActivity::class.java)
        intent.putExtra("FullName",etInputFullname.text.toString())
        intent.putExtra("UserName",etInputUserName.text.toString())
        intent.putExtra("UserPassword",UserPassword.text.toString())

        Authenticator.createUserWithEmailAndPassword(UserEmail.text.toString(),UserPassword.text.toString())
            .addOnCompleteListener(this)
            {
                if(it.isSuccessful)
                {
                    Toast.makeText(this,"Successfully Signed up", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }else
                {
                    Toast.makeText(this,"Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //References
    //Rochelle13. 2025. Roch Run. [Online] Accessed at: https://github.com/rochelle13/RochRun. [Accessed on: 28 September 2025]

}