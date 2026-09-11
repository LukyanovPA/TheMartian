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
import com.pavellukyanov.themartian.common.NetworkStateException
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.ext.ApiException
import com.pavellukyanov.themartian.utils.ext.log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class UpdateRoverInfoCacheWork(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val updateRoverInfoCache: UpdateRoverInfoCache by inject()
    private val errorQueue: ErrorQueue by inject()

    override suspend fun doWork(): Result =
        try {
            updateRoverInfoCache()
            log.v("UpdateRoverInfoCacheWork -> success")
            Result.success()
        } catch (e: Throwable) {
            log.e(e)
            // This work is what fills the rover cache, and the splash screen stays up until
            // the outcome is known — so every failure has to be reported, not only
            // NetworkStateException. Reporting just that one meant a dropped connection left
            // the user on an endless splash with nothing to explain it.
            errorQueue.add(e.asReportableError())
            Result.failure()
        }

    /**
     * A transport problem is worth the plain "no internet" wording; anything else keeps its
     * own message so it can be told apart from a connectivity issue.
     */
    private fun Throwable.asReportableError(): Throwable =
        when (this) {
            is NetworkStateException -> this
            is ApiException.ConnectionException -> NetworkStateException(
                message = applicationContext.getString(R.string.bad_internet_connection_error_message)
            )
            else -> this
        }

    override suspend fun getForegroundInfo(): ForegroundInfo = ForegroundInfo(Random.nextInt(), getNotification())

    private fun getNotification(): Notification =
        NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_mars)
            .setContentTitle(applicationContext.getString(R.string.cache_service_title))
            .setTicker(applicationContext.getString(R.string.cache_service_title))
            .setContentText(applicationContext.getString(R.string.cache_service_content))
            .setColor(applicationContext.getColor(R.color.icon_red))
            .setOngoing(false)
            .apply {
                createNotificationChannel().also {
                    setChannelId(it.id)
                }
            }
            .build()

    private fun createNotificationChannel(): NotificationChannel {
        val roverInfoUpdateChannel = NotificationChannel(
            applicationContext.getString(R.string.channel_id),
            applicationContext.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(roverInfoUpdateChannel)
        return roverInfoUpdateChannel
    }
}