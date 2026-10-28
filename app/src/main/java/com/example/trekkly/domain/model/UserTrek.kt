package com.example.trekkly.domain.model

data class UserTrek(
    val trek:Trek,
    val status: TrekStatus,
    val scheduleDate:Long? =null,
    val startedAt:Long?=null,
    val currentDay:Int=0,
    val completedAt:Long? = null,
    val updatedAt: Long =0L
){
    val progress: Float
        get() = if (trek.days>0)(currentDay.toFloat()/trek.days).coerceIn(0f,1f)else 0f
}
