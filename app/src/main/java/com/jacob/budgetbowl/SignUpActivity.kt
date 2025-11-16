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
import com.google.firebase.firestore.FirebaseFirestore


//https://github.com/rochelle13/RochRun
//Firebase Authentication
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

    private lateinit var imgTopDecor : ImageView

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
        imgTopDecor = findViewById(R.id.imgTopDecor)
        imgTopDecor.setImageResource(R.drawable.topimagestretched)

        Authenticator = FirebaseAuth.getInstance()//(Moodley,2025)

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
        val userEmail = etInputUserEmail.text.toString()
        val userPassword = etInputUserPassword.text.toString()
        val userConfirmedPassword = etInputConfirmUserPassword.text.toString()
        val fullName = etInputFullname.text.toString()
        val userName = etInputUserName.text.toString()


        //validate info before signing up
        if(userEmail.isBlank()||userPassword.isBlank()||userConfirmedPassword.isBlank()||fullName.isBlank()||userName.isBlank())
        {
            Toast.makeText(this,"All fields need to be filled in", Toast.LENGTH_SHORT).show()
            return
        }

        //Needs to be toString cause .text returns an object that can be edited
        // while toString gives us the actual string value

        if(userPassword!=userConfirmedPassword)
        {
            Toast.makeText(this,"Confirm Password and password need to match", Toast.LENGTH_SHORT).show()
            return
        }

        //Now we sign up since we validated
        Authenticator.createUserWithEmailAndPassword(userEmail,userPassword)
            .addOnCompleteListener(this)
            {
                if(it.isSuccessful)
                {
                    val firebaseUser = Authenticator.currentUser
                    val uid = firebaseUser!!.uid
                    val db = FirebaseFirestore.getInstance()

                    val user = hashMapOf(
                        "fullName" to fullName,
                        "userName" to userName,
                        "email" to userEmail
                    )

                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Successfully Signed up", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SetInitialBudgetActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }else
                {
                    Toast.makeText(this,"Sign up failed: ${it.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }


    //References
    //Rochelle13. 2025. Roch Run. [Online] Accessed at: https://github.com/rochelle13/RochRun. [Accessed on: 28 September 2025]

}