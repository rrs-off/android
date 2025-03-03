package com.example.lab1

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import android.os.Binder
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicServiceChannel"
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer.create(this, R.raw.keipker)
        mediaPlayer?.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startMusic()
            "PAUSE" -> pauseMusic()
            "STOP" -> stopMusic()
        }
        return START_STICKY
    }

    private fun startMusic() {
        mediaPlayer?.start()
        startForeground(1, createNotification("Музыка ойнап тұр"))
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        startForeground(1, createNotification("Музыка үзіліс жасауда"))
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
        stopForeground(true)
        stopSelf()
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    private fun createNotification(content: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Музыка ойнатқыш")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26+
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

}