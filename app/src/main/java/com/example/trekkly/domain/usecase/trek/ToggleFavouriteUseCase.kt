package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.repository.TrekRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    suspend operator fun invoke(trekId: String, favourite: Boolean) =
        repository.setFavourite(trekId, favourite)
}
