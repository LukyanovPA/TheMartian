package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.utils.ext.onIo

class DeleteCameraCache(
    private val camerasDao: CamerasDao
) {
    suspend operator fun invoke() = onIo {
        camerasDao.deleteAll()
    }
}