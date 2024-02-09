package com.pavellukyanov.themartian.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cameras")
data class Camera(
    @PrimaryKey(autoGenerate = true)
    val entityId: Long = 0L,
    val roverName: String,
    val name: String,
    val cameraFullName: String? = null
)
