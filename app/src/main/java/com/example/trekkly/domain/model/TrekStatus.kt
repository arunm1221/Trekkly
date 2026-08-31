package com.example.trekkly.domain.model

enum class TrekStatus {
    UPCOMING, ACTIVE, COMPLETED;
    companion object{
        fun fromString(value: String?): TrekStatus =
            entries.firstOrNull{it.name.equals(value?.trim(), ignoreCase = true)}?: TrekStatus.UPCOMING
    }
}