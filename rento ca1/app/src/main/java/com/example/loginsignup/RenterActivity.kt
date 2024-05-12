package com.example.loginsignup

import com.example.loginsignup.add_item
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class RenterActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renter)

        // Initialize views
        drawerLayout = findViewById(R.id.drawerlayout)

        // Set up the AppBar with Toolbar
        setSupportActionBar(findViewById(R.id.mytoolbar))

        val toolbar = findViewById<Toolbar>(R.id.mytoolbar)
        toolbar.title = "Rento"
        setSupportActionBar(toolbar)
        mAuth=FirebaseAuth.getInstance()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawe_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val bottomNavigationView= findViewById<BottomNavigationView>(R.id.bottomNaviagatioBar)

        val frameLayout = findViewById<LinearLayout>(R.id.main_frame)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add_details ->{
                    startActivity(Intent(this, add_item::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history ->{
                    return@setOnNavigationItemSelectedListener true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation item clicks here
        when (item.itemId) {
            R.id.Profile -> {
                // Handle Profile item click
                return true
            }
            R.id.Signout -> {
                mAuth.signOut()
                val sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("logged_in", false)
                editor.apply()
                startActivity(Intent(this, loginActivity::class.java))
                finish()
                return true
            }
            R.id.Settings -> {
                // Handle Settings item click
                return true
            }
            R.id.About_Us -> {
                // Start the AboutUsActivity when "About Us" is clicked
                startActivity(Intent(this, AboutUsActivity::class.java))
                return true
            }
            else -> return false
        }
    }
}
