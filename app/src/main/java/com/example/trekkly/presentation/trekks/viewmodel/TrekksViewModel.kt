package com.example.trekkly.presentation.trekks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.model.TrekFilter
import com.example.trekkly.domain.usecase.trek.ObserveTreksUseCase
import com.example.trekkly.domain.usecase.trek.SyncTreksUseCase
import com.example.trekkly.domain.usecase.trek.ToggleFavouriteUseCase
import com.example.trekkly.presentation.trekks.event.DifficultyFilter
import com.example.trekkly.presentation.trekks.event.TrekksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrekksViewModel @Inject constructor(
    private val observeTreks: ObserveTreksUseCase,
    private val syncTreks: SyncTreksUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrekksUiState())
    val uiState = _uiState.asStateFlow()

    // User input lives in its own flows so the use case can react to it.
    private val query = MutableStateFlow("")
    private val difficulty = MutableStateFlow(DifficultyFilter.ALL)

    private val criteria = combine(query, difficulty) { q, level ->
        TrekFilter(query = q, difficulties = level.levels)
    }

    init {
        observeCatalog()
        startSync()
    }

    private fun observeCatalog() {
        observeTreks(criteria)
            .onEach { treks ->
                _uiState.update { it.copy(treks = treks, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun startSync() {
        viewModelScope.launch {
            syncTreks().onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
        // Echo into the state immediately so the text field never lags a frame behind.
        _uiState.update { it.copy(query = value) }
    }

    fun onClearQuery() = onQueryChange("")

    fun onDifficultySelected(value: DifficultyFilter) {
        difficulty.value = value
        _uiState.update { it.copy(selectedDifficulty = value) }
    }

    fun onFavouriteToggle(trekId: String, favourite: Boolean) {
        viewModelScope.launch { toggleFavourite(trekId, favourite) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
