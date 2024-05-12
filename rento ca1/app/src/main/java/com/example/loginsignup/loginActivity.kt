package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class loginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private fun isLoggedIn(): Boolean {
        // Implement your logic to check if the user is logged in, e.g., by checking shared preferences
        val sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)
        return sharedPreferences.getBoolean("logged_in", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        // Check if user is already logged in
        if (isLoggedIn()) {
            startActivity(Intent(this@loginActivity, RenterActivity::class.java))
            finish()
            return
        }

        // Set onClickListener for login button
        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()

            if (loginUsername.isNotEmpty() && loginPassword.isNotEmpty()) {
                loginUser(loginUsername, loginPassword)
            } else {
                Toast.makeText(this@loginActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        // Set onClickListener for signup redirect
        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this@loginActivity, SignupActivity::class.java))
            finish()
        }
    }

    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if (datasnapshot.exists()) {
                        for (userSnapshot in datasnapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)

                            if (userData != null && userData.password == password) {
                                setLoggedIn()
                                Toast.makeText(
                                    this@loginActivity,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@loginActivity, MainActivity::class.java))
                                finish()
                                return
                            }
                        }
                    }
                    Toast.makeText(this@loginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@loginActivity,
                        "Database Error :${databaseError.message} ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setLoggedIn() {
        // Implement your logic to set user as logged in, e.g., by updating shared preferences
        val sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("logged_in", true)
        editor.apply()
    }
}
