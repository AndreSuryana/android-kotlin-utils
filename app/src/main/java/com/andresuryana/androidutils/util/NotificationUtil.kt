package com.andresuryana.androidutils.util

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.FragmentActivity
import com.andresuryana.androidutils.R

class NotificationUtil(
    private val activity: FragmentActivity,
    private val isLightsEnabled: Boolean = true,
    private val isVibrationEnabled: Boolean = true,
    private val lightsColor: Int = Color.RED
) {
    // Notification Manager
    private lateinit var notifyManager: NotificationManager

    // Channel
    private var channelId: String = DEFAULT_CHANNEL_ID

    // Notification
    private var title: Int = 0
    private var text: Int = 0
    private var icon: Int = 0
    private var notificationId: Int = DEFAULT_NOTIFICATION_ID

    // Permission
    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) showNotification(title, text, icon, notificationId)
            else Toast.makeText(
                activity,
                activity.getString(R.string.permission_notification_request),
                Toast.LENGTH_SHORT
            ).show()
        }

    fun setNotificationChannel(
        name: String,
        description: String,
        channelId: String = DEFAULT_CHANNEL_ID,
    ) {
        // Init notification manager
        notifyManager = activity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Set channelId
        this.channelId = channelId

        // Create notification channel for API 26 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                name,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(isLightsEnabled)
                enableVibration(isVibrationEnabled)
                setDescription(description)
                lightColor = lightsColor
            }
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    fun showNotification(
        @StringRes title: Int,
        @StringRes text: Int,
        @DrawableRes icon: Int,
        notificationId: Int,
        pendingIntent: PendingIntent? = null
    ) {
        // Set current notificationId
        this.title = title
        this.text = text
        this.icon = icon
        this.notificationId = notificationId

        // Check for permission POST_NOTIFICATION
        checkNotificationPermission()

        // Create and show notification
        val notification = createNotification(title, text, icon, this.channelId, pendingIntent)
        notifyManager.notify(notificationId, notification)
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PermissionChecker.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotification(
        @StringRes title: Int,
        @StringRes text: Int,
        @DrawableRes icon: Int,
        channelId: String,
        pendingIntent: PendingIntent? = null,
    ): Notification {
        // Build notification
        val notifyBuilder = NotificationCompat.Builder(activity, channelId).apply {
            // Set content
            setContentTitle(activity.getString(title))
            setContentText(activity.getString(text))
            setSmallIcon(icon)

            // Set notification intent
            setContentIntent(pendingIntent)
            setAutoCancel(true)

            // Set priority
            priority = NotificationCompat.PRIORITY_HIGH
        }

        return notifyBuilder.build()
    }

    companion object {
        const val DEFAULT_CHANNEL_ID = "default_channel"
        const val DEFAULT_NOTIFICATION_ID = -1

        fun createPendingIntent(
            activity: Activity,
            activityClass: Class<*>,
            requestCode: Int = DEFAULT_NOTIFICATION_ID
        ): PendingIntent {
            // Create pending intent
            return PendingIntent.getActivity(
                activity,
                requestCode,
                Intent(activity, activityClass),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}