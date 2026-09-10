package com.example.trekkly.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.usecase.trek.ObserveActiveExpeditionUseCase
import com.example.trekkly.domain.usecase.trek.ObserveUpcomingTreksUseCase
import com.example.trekkly.domain.usecase.trek.SyncTreksUseCase
import com.example.trekkly.domain.usecase.trek.SyncUserTreksUseCase
import com.example.trekkly.domain.usecase.trek.ToggleFavouriteUseCase
import com.example.trekkly.presentation.home.event.HomeUiState
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
class HomeViewModel @Inject constructor(
    private val observeActiveExpedition: ObserveActiveExpeditionUseCase,
    private val observeUpcomingTreks: ObserveUpcomingTreksUseCase,
    private val syncTreks: SyncTreksUseCase,
    private val syncUserTreks: SyncUserTreksUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeHomeData()
        startSync()
    }

    private fun observeHomeData() {
        combine(observeActiveExpedition(), observeUpcomingTreks()) { active, upcoming ->
            active to upcoming
        }.onEach { (active, upcoming) ->
            _uiState.update {
                it.copy(activeExpedition = active, upcoming = upcoming, isLoading = false)
            }
        }.launchIn(viewModelScope)
    }

    private fun startSync() {
        viewModelScope.launch {
            syncTreks().onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
        viewModelScope.launch {
            syncUserTreks().onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onFavouriteToggle(trekId: String, favourite: Boolean) {
        viewModelScope.launch { toggleFavourite(trekId, favourite) }
    }
}
