package com.example.trekkly.domain.usecase.trek

import com.example.trekkly.domain.model.Trek
import com.example.trekkly.domain.model.TrekFilter
import com.example.trekkly.domain.repository.TrekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Observes the trek catalogue, re-filtered whenever either the catalogue or the
 * caller's criteria change.
 */
class ObserveTreksUseCase @Inject constructor(
    private val repository: TrekRepository
) {
    operator fun invoke(criteria: Flow<TrekFilter>): Flow<List<Trek>> =
        combine(repository.observeTreks(), criteria) { treks, filter ->
            treks.matching(filter)
        }
}

/**
 * Search matches the trek name, its location and its tags, so "Ladakh", "kedar" and
 * "waterfall" all lead somewhere sensible.
 */
private fun List<Trek>.matching(criteria: TrekFilter): List<Trek> {
    val term = criteria.query.trim()
    return filter { trek ->
        val difficultyMatches = criteria.difficulties.isEmpty() ||
            trek.difficultyLevel in criteria.difficulties

        val textMatches = term.isEmpty() ||
            trek.title.contains(term, ignoreCase = true) ||
            trek.location.contains(term, ignoreCase = true) ||
            trek.tags.any { it.contains(term, ignoreCase = true) }

        difficultyMatches && textMatches
    }
}
