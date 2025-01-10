package com.pavellukyanov.themartian.utils.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.onIo
import com.pavellukyanov.themartian.utils.helpers.DatabaseHelper
import com.pavellukyanov.themartian.utils.helpers.SharedPreferencesHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

private const val FIRST_START_KEY = "FIRST_START_KEY"

class DebugCheckFirstStartWork(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val databaseHelper: DatabaseHelper by inject()
    private val sharedPreferencesHelper: SharedPreferencesHelper by inject()

    override suspend fun doWork(): Result = onIo {
        try {
            if (!sharedPreferencesHelper.get(FIRST_START_KEY, false)) {
                if (databaseHelper.canRead())
                    databaseHelper.delete()

                sharedPreferencesHelper.put(FIRST_START_KEY, true)
            }
            log.v("CheckFirstStartWork -> success")
            Result.success()
        } catch (e: Throwable) {
            log.e(e)
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = ForegroundInfo(Random.nextInt(), getNotification())

    private fun getNotification(): Notification =
        NotificationCompat.Builder(applicationContext, "First start channel")
            .setSmallIcon(R.drawable.ic_mars)
            .setContentTitle("Проверяем первый запуск")
            .setTicker("Проверяем первый запуск")
            .setContentText("Первый запуск проверен")
            .setColor(applicationContext.getColor(R.color.teal_700))
            .setOngoing(false)
            .apply {
                createNotificationChannel().also {
                    setChannelId(it.id)
                }
            }
            .build()

    private fun createNotificationChannel(): NotificationChannel {
        val roverInfoUpdateChannel = NotificationChannel(
            "First start channel",
            "Проверяем первый запуск",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(roverInfoUpdateChannel)
        return roverInfoUpdateChannel
    }
}