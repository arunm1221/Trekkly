package com.example.trekkly.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.repository.TrekRepository
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
    val repository: TrekRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeHomeData()
        startSync()
    }

    private fun startSync() {
        viewModelScope.launch {
            runCatching { repository.syncTrek() }
                .onFailure { e-> _uiState.update { it.copy(error=e.message) } }
        }
        viewModelScope.launch {
            runCatching { repository.syncUserTreks() }
                .onFailure { e-> _uiState.update { it.copy(error=e.message) } }
        }

    }

    private fun observeHomeData() {

        combine(repository.observeActiveExpedition(),repository.observeUpcomingTreks()){
            active,upcoming-> active to upcoming
        }.onEach { (active,upcoming)->
            _uiState.update {
                it.copy(activeExpedition = active,upcoming=upcoming, isLoading = false)
            }
        }.launchIn(viewModelScope)

    }

    fun onFavouriteToggle(trekId: String,favourite: Boolean){
        viewModelScope.launch { repository.setFavourite(trekId,favourite) }
    }
}