package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.model.UserTrek
import com.example.trekkly.domain.repository.TrekRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Scheduled treks, soonest first. */
class ObserveUpcomingTreksUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    operator fun invoke(): Flow<List<UserTrek>> = repository.observeUpcomingTreks()
}
