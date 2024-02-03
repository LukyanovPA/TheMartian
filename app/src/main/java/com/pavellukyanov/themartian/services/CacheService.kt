package com.pavellukyanov.themartian.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.pavellukyanov.themartian.MainActivity
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import com.pavellukyanov.themartian.utils.C.CACHE_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.C.INT_ONE
import com.pavellukyanov.themartian.utils.C.INT_ZERO
import com.pavellukyanov.themartian.utils.C.OK_RESULT
import com.pavellukyanov.themartian.utils.ext.checkSdkVersion
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.suspendDebugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CacheService : LifecycleService() {
    private val updateRoverInfoCache: UpdateRoverInfoCache by inject()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        log.w("onStartCommand")
        createNotificationChannel()

        checkSdkVersion(
            less33 = {
                startForeground(INT_ONE, getNotification())
                log.w("startForeground API < 33")
            },
            more33 = {
                startForeground(INT_ONE, getNotification(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                log.w("startForeground API > 33")
            }
        )

        lifecycleScope.launch(Dispatchers.IO) {
            updateRoverInfoCache().also {
                sendBroadcast(Intent(CACHE_BROADCAST_ACTION).putExtra(OK_RESULT, true))
                stopSelf()
                suspendDebugLog(tag = this@CacheService::class.java.simpleName) { "updateRoverInfoCache" }
            }
        }
        return START_STICKY
    }

    private fun getNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, INT_ZERO, notificationIntent, PendingIntent.FLAG_MUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.cache_service_title))
            .setContentText(getString(R.string.cache_service_content))
            .setSmallIcon(R.drawable.ic_mars)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        log.w("createNotificationChannel")
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundService Update cache"
        private const val CHANNEL_NAME = "Foreground Service Channel"
    }
}