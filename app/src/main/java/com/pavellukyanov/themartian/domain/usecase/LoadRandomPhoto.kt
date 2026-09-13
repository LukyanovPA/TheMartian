package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.utils.ext.onIo
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

private const val MAX_ATTEMPTS = 4

class LoadRandomPhoto(
    private val apiDataSource: ApiDataSource
) {
    suspend operator fun invoke(rovers: List<Rover>): Photo? = onIo {
        val candidates = rovers.filter { it.landingDate.isNotBlank() && it.maxDate.isNotBlank() }
        if (candidates.isEmpty()) return@onIo null

        repeat(MAX_ATTEMPTS) {
            try {
                val rover = candidates.random()
                val photos = apiDataSource.getPhotosByEarthDate(
                    roverName = rover.roverName,
                    earthDate = randomDate(from = rover.landingDate, to = rover.maxDate).toString()
                )
                if (photos.isNotEmpty()) return@onIo photos.random()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }

        null
    }

    private fun randomDate(from: String, to: String): LocalDate {
        val start = LocalDate.parse(from).toEpochDay()
        val end = LocalDate.parse(to).toEpochDay()
        return LocalDate.ofEpochDay(if (end > start) Random.nextLong(start, end + 1) else start)
    }
}
