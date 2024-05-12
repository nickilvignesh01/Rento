package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.databinding.ActivitySignupBinding
import com.google.firebase.database.*

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()

            val usernameLengthValid = signupUsername.length >= 8
            val passwordLengthValid = signupPassword.length >= 6

            if (usernameLengthValid && passwordLengthValid) {
                signupUser(signupUsername, signupPassword)
            } else {
                val errorMessage = when {
                    !usernameLengthValid -> "Username should be at least 8 characters long"
                    else -> "Password should be at least 6 characters long"
                }
                Toast.makeText(
                    this@SignupActivity,
                    errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this@SignupActivity, loginActivity::class.java))
            finish()
        }
    }

    private fun signupUser(username: String, password: String) {
        Log.d("SignupActivity", "Attempting to sign up user: $username")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var userExists = false
                for (snapshot in dataSnapshot.children) {
                    val userData = snapshot.getValue(UserData::class.java)
                    if (userData?.username == username) {
                        userExists = true
                        break
                    }
                }

                if (!userExists) {
                    val id = databaseReference.push().key
                    val userData = UserData(id, username, password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(
                        this@SignupActivity,
                        "Signup Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@SignupActivity, loginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "User already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SignupActivity", "Database Error: ${databaseError.message}")
                Toast.makeText(
                    this@SignupActivity,
                    "Database Error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

data class UserData(val id: String? = null, val username: String? = null, val password: String? = null)
