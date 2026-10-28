package com.example.trekkly.presentation.home.event

import com.example.trekkly.domain.model.UserTrek

data class HomeUiState(
    val activeExpedition: UserTrek? = null,
    val upcoming: List<UserTrek> = emptyList(),
    val isLoading: Boolean =true,
    val error: String? = null
)