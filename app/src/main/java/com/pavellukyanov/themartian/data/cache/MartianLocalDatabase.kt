package com.pavellukyanov.themartian.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.domain.entity.Rover

@Database(
    entities = [
        Rover::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MartianLocalDatabase : RoomDatabase() {
    abstract fun roverInfo(): RoverInfoDao
}