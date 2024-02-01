package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pavellukyanov.themartian.domain.entity.Rover
import kotlinx.coroutines.flow.Flow

@Dao
interface RoverInfoDao {
    @Query("SELECT * FROM rover_info")
    fun subscribeAll(): Flow<List<Rover>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rovers: List<Rover>)
}