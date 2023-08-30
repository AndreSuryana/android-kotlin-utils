package com.andresuryana.notifyme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andresuryana.notifyme.databinding.ActivityMainBinding
import com.andresuryana.notifyme.util.NotificationUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Notification Util
    private val notifyUtil = NotificationUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notification Util
        // Setup notification channel
        notifyUtil.setNotificationChannel(
            getString(R.string.notification_channel_name),
            getString(R.string.notification_channel_desc),
            PRIMARY_CHANNEL_ID
        )

        // Button listener
        binding.btnNotify.setOnClickListener { sendNotification() }
    }

    private fun sendNotification() {
        // Notification Util
        // Show notification with pending intent
        notifyUtil.showNotification(
            R.string.notification_target_title,
            R.string.notification_target_desc,
            R.drawable.ic_round_alarm,
            TARGET_NOTIFICATION_ID,
            NotificationUtil.createPendingIntent(
                this,
                MainActivity::class.java,
                TARGET_NOTIFICATION_ID
            )
        )
    }

    companion object {
        private const val PRIMARY_CHANNEL_ID = "target_notification_channel"
        private const val TARGET_NOTIFICATION_ID = 0
    }
}