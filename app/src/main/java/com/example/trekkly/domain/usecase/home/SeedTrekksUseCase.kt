package com.example.trekkly.domain.usecase.home


import com.example.trekkly.domain.repository.TrekkRepository
import javax.inject.Inject

class SeedTrekksUseCase @Inject constructor(
    private val trekkRepository: TrekkRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        trekkRepository.seedIfEmpty()
    }
}