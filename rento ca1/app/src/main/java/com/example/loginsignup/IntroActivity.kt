package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.R

class IntroActivity : AppCompatActivity() {
    private val delayMillis: Long = 4000 // 4 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Delayed redirection using Handler
        Handler().postDelayed({
            startActivity(Intent(this, loginActivity::class.java))
            finish() // Finish current activity so user cannot go back to it using back button
        }, delayMillis)

        // Initialize ImageView
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Define the animation
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        animation.duration = 3000 // Duration in milliseconds
        animation.fillAfter = true // Keeps the final state of the animation

        // Set animation listener if needed
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Animation ended, do something if needed
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Start animation
        imageView.startAnimation(animation)
    }
}