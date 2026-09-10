package com.example.trekkly.domain.model

/**
 * A catalogue query: free text plus the difficulty levels to keep.
 *
 * An empty [difficulties] set means "no difficulty restriction" — the domain deals in
 * real [DifficultyLevel] values, so how many chips the UI chooses to show, and which
 * levels each chip stands for, stays a presentation decision.
 */
data class TrekFilter(
    val query: String = "",
    val difficulties: Set<DifficultyLevel> = emptySet()
) {
    val isEmpty: Boolean
        get() = query.isBlank() && difficulties.isEmpty()
}
