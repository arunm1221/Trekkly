package com.example.trekkly.domain.usecase.home


import com.example.trekkly.domain.model.Trekk
import com.example.trekkly.domain.model.TrekkStatus
import com.example.trekkly.domain.repository.TrekkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveActiveTrekkUseCase @Inject constructor(
    private val trekkRepository: TrekkRepository
) {
    operator fun invoke(): Flow<Trekk?> =
        trekkRepository.observeByStatus(TrekkStatus.ACTIVE).map { it.firstOrNull() }
}