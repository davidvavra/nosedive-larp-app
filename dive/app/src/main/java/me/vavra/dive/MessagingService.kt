package me.vavra.dive

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class MessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        for (stars in 1..5) {
            val channel = NotificationChannel(
                stars.toString(),
                "Hodnocení " + "⭐".repeat(stars),
                NotificationManager.IMPORTANCE_HIGH
            )
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val sound = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/raw/star$stars"
            )
            channel.setSound(sound, audioAttributes)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Database.updateNotificationsToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Dive", message.data.toString())
        val nameGenitiv = message.data["fromNameGenitiv"]
        val stars = message.data["stars"]?.toInt() ?: 0
        val starsEmoji = "⭐".repeat(stars)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, stars.toString())
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Hodnocení od $nameGenitiv")
            .setContentText(starsEmoji)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val notificationId = Random.Default.nextInt()
            notify(notificationId, builder.build())
        }
    }
}