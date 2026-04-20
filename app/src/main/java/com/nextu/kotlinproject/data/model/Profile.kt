package com.nextu.kotlinproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val id: Int = 1,
    val prenom: String,
    val nom: String,
    val telephone: String,
    val email: String
)

