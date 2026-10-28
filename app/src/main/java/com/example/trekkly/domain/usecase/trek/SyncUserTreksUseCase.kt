package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.repository.TrekRepository
import javax.inject.Inject

/**
 * Starts the per-user expedition sync. Like [SyncTreksUseCase] this does not return:
 * the repository collects a live remote snapshot flow into the local database.
 */
class SyncUserTreksUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        runCatching { repository.syncUserTreks() }
}
