package com.roam.assignment2.ui.dashboard

import com.roam.assignment2.domain.model.Entity

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val entities: List<Entity>, val total: Int) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}