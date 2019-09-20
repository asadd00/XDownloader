package com.android.downloadanything.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.android.downloadanything.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            intent = Intent(this@SplashActivity, FeedsActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
