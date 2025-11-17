package com.jacob.budgetbowl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class activity_profile : BaseActivity() { // Changed to BaseActivity

    private lateinit var etProfileFullname: EditText
    private lateinit var etProfileUsername: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnDiscardChanges: Button
    private lateinit var btnEditProfileIcon: Button
    private lateinit var profilePic: CircleImageView
    private lateinit var homeButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etProfileFullname = findViewById(R.id.etProfileFullname)
        etProfileUsername = findViewById(R.id.etProfileUsername)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        btnSetBudget = findViewById(R.id.btnSetBudget)
        btnDiscardChanges = findViewById(R.id.btnDiscardChanges)
        btnEditProfileIcon = findViewById(R.id.btnEditProfileIcon)
        profilePic = findViewById(R.id.ProfilePic)
        homeButton = findViewById(R.id.homeButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        btnSetBudget.setOnClickListener {
            val intent = Intent(this, SetInitialBudgetActivity::class.java)
            startActivity(intent)
        }

        btnSaveChanges.setOnClickListener {
            saveUserProfile()
        }

        btnDiscardChanges.setOnClickListener {
            loadUserProfile()
            Toast.makeText(this, "Changes have been reverted", Toast.LENGTH_SHORT).show()
        }

        btnEditProfileIcon.setOnClickListener { view ->
            showIconPopup(view)
        }
    }

    private fun showIconPopup(anchorView: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_select_icon, null)

        val popupWindow = PopupWindow(
            popupView,
            700, // You can adjust the width
            700, // You can adjust the height
            true // Focusable
        )

        val iconGridView = popupView.findViewById<GridView>(R.id.iconGridView)

        val iconList = listOf(
            R.drawable.clownfishplain,
            R.drawable.clownfishdecor1,
            R.drawable.clownfishdecor2,
            R.drawable.clownfishdecor3,
            R.drawable.redfishplain,
            R.drawable.redfishdecor1,
            R.drawable.redfishdecor2,
            R.drawable.redfishdecor3,
            R.drawable.purplefishplain,
            R.drawable.purplefishdecor1,
            R.drawable.purplefishdecor2,
            R.drawable.purplefishdecor3,
            R.drawable.sharkdecor2,
            R.drawable.sharkdecor3
        )

        val adapter = IconAdapter(this, iconList)
        iconGridView.adapter = adapter

        iconGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedIcon = iconList[position]
            profilePic.setImageResource(selectedIcon)
            profilePic.tag = selectedIcon // Tag the ImageView with the resource ID
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etProfileFullname.setText(document.getString("fullName"))
                        etProfileUsername.setText(document.getString("userName"))

                        val profileIconResId = document.getLong("profileIcon")?.toInt()

                        if (profileIconResId != null) {
                            profilePic.setImageResource(profileIconResId)
                            profilePic.tag = profileIconResId
                        } else {
                            val defaultIcon = R.drawable.pfpwithborder__3_
                            profilePic.setImageResource(defaultIcon)
                            profilePic.tag = defaultIcon
                        }

                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            val fullName = etProfileFullname.text.toString().trim()
            val userName = etProfileUsername.text.toString().trim()
            val profileIcon = profilePic.tag as? Int

            if (fullName.isNotEmpty() && userName.isNotEmpty()) {
                val userProfile = mutableMapOf<String, Any>(
                    "fullName" to fullName,
                    "userName" to userName
                )
                if (profileIcon != null) {
                    userProfile["profileIcon"] = profileIcon
                }

                db.collection("users").document(uid).update(userProfile)
                    .addOnSuccessListener {
                        val userRef = db.collection("users").document(uid)
                        userRef.update("points", FieldValue.increment(10))
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Full name and username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
