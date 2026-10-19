package com.roam.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roam.assignment2.data.repository.DashboardRepository
import com.roam.assignment2.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Hilt supplies the [SavedStateHandle] pre-populated with the launching Intent's extras.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val keypass: String = savedStateHandle[KEY_KEYPASS] ?: ""

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (keypass.isBlank()) {
            _uiState.value = DashboardUiState.Error(MISSING_KEYPASS_MESSAGE)
            return
        }

        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = repository.getDashboard(keypass)) {
                is ApiResult.Success ->
                    DashboardUiState.Success(result.data.entities, result.data.total)
                is ApiResult.Error -> DashboardUiState.Error(result.message)
            }
        }
    }

    companion object {
        const val KEY_KEYPASS = "keypass"
        const val MISSING_KEYPASS_MESSAGE = "Missing keypass. Please log in again."
    }
}