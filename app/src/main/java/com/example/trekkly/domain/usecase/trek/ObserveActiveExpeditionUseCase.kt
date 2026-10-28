package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.model.UserTrek
import com.example.trekkly.domain.repository.TrekRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** The trek the user is currently on, or null when none is in progress. */
class ObserveActiveExpeditionUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    operator fun invoke(): Flow<UserTrek?> = repository.observeActiveExpedition()
}
