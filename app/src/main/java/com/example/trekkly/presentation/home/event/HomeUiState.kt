package com.example.trekkly.presentation.home.event



import com.example.trekkly.domain.model.Trekk

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeTrekk: Trekk? = null,
    val upcomingTrekks: List<Trekk> = emptyList(),
    val errorMessage: String? = null
) {
    val hasActiveExpedition: Boolean get() = activeTrekk != null
}

sealed interface HomeEvent {
    data class NavigateToTrekkDetail(val trekkId: String) : HomeEvent
}