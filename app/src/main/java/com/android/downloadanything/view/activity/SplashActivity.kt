package com.android.downloadanything.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.android.downloadanything.R
import com.android.downloadanything.utils.Defaults

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        createNotificationChannel()

        Handler().postDelayed({
            intent = Intent(this@SplashActivity, FeedsActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun createNotificationChannel(){
        val name = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel
                    = NotificationChannel(name, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.setSound(null, null)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
