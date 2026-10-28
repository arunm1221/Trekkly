package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.model.TrekStatus
import com.example.trekkly.domain.model.UserTrekStats
import com.example.trekkly.domain.repository.TrekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveTrekStatsUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    operator fun invoke(): Flow<UserTrekStats> =
        repository.observeUserTreks().map {
            userTreks ->
            val completed = userTreks.filter { it.status == TrekStatus.COMPLETED }
            UserTrekStats(completedTreks = completed.size,
                kilometersHiked = completed.sumOf { it.trek.distance },
                summits = completed.size)
        }
}