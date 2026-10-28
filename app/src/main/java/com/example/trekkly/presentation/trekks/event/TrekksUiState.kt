package com.example.trekkly.presentation.trekks.event

import com.example.trekkly.domain.model.DifficultyLevel
import com.example.trekkly.domain.model.Trek

/**
 * The difficulty buckets shown as chips.
 *
 * The catalogue has four levels but the chip row shows three: DIFFICULT and EXPERT are
 * both surfaced as "Hard", since users think of them as one bucket. [levels] is how a
 * chip is handed to the domain layer — an empty set means "no restriction".
 */
enum class DifficultyFilter(val label: String, val levels: Set<DifficultyLevel>) {
    ALL("All", emptySet()),
    EASY("Easy", setOf(DifficultyLevel.EASY)),
    MODERATE("Moderate", setOf(DifficultyLevel.MODERATE)),
    HARD("Hard", setOf(DifficultyLevel.DIFFICULT, DifficultyLevel.EXPERT))
}

data class TrekksUiState(
    /** Treks after search + difficulty filtering — this is what the grid renders. */
    val treks: List<Trek> = emptyList(),
    val query: String = "",
    val selectedDifficulty: DifficultyFilter = DifficultyFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    /** Catalogue loaded, but the current filters match nothing. */
    val isEmptyResult: Boolean
        get() = !isLoading && treks.isEmpty()

    val hasActiveFilters: Boolean
        get() = query.isNotBlank() || selectedDifficulty != DifficultyFilter.ALL
}
