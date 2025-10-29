package com.example.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entity(
    @SerialName("id") val id: Int? = null,
    @SerialName("note") val note: String
)