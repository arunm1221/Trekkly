package com.example.trekkly.domain.usecase.home


import com.example.trekkly.domain.model.Trekk
import com.example.trekkly.domain.model.TrekkStatus
import com.example.trekkly.domain.repository.TrekkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUpcomingTrekksUseCase @Inject constructor(
    private val trekkRepository: TrekkRepository
) {
    operator fun invoke(): Flow<List<Trekk>> =
        trekkRepository.observeByStatus(TrekkStatus.UPCOMING)
}