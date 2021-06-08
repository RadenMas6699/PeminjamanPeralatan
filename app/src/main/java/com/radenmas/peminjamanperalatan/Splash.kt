package com.radenmas.peminjamanperalatan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this@Splash, Login::class.java))
            finish()
        }, 2500)
    }
}