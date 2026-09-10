package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.repository.TrekRepository
import javax.inject.Inject

/**
 * Starts the catalogue sync. This does not return: the repository collects a live
 * remote snapshot flow and writes every change into the local database.
 */
class SyncTreksUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        runCatching { repository.syncTrek() }
}
