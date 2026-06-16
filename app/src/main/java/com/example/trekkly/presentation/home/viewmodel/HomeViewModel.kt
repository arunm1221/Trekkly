package com.example.trekkly.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.presentation.home.event.HomeEvent
import com.example.trekkly.presentation.home.event.HomeUiState
import com.example.trekkly.domain.usecase.home.ObserveActiveTrekkUseCase
import com.example.trekkly.domain.usecase.home.ObserveUpcomingTrekksUseCase
import com.example.trekkly.domain.usecase.home.SeedTrekksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seedTrekksUseCase: SeedTrekksUseCase,
    private val observeActiveTrekkUseCase: ObserveActiveTrekkUseCase,
    private val observeUpcomingTrekksUseCase: ObserveUpcomingTrekksUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<HomeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        seed()
        observeFeed()
    }

    private fun seed() {
        viewModelScope.launch {
            seedTrekksUseCase().onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Failed to load trekks")
                }
            }
        }
    }

    private fun observeFeed() {
        viewModelScope.launch {
            combine(
                observeActiveTrekkUseCase(),
                observeUpcomingTrekksUseCase()
            ) { active, upcoming ->
                active to upcoming
            }.collect { (active, upcoming) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeTrekk = active,
                        upcomingTrekks = upcoming
                    )
                }
            }
        }
    }

    fun onTrekkClicked(trekkId: String) {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToTrekkDetail(trekkId)) }
    }
}