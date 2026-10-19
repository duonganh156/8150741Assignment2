package com.roam.assignment2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roam.assignment2.data.repository.AuthRepository
import com.roam.assignment2.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (_uiState.value is LoginUiState.Loading) return

        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error(EMPTY_FIELDS_MESSAGE)
            return
        }

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = authRepository.login(username.trim(), password)) {
                is ApiResult.Success -> LoginUiState.Success(result.data)
                is ApiResult.Error -> LoginUiState.Error(result.message)
            }
        }
    }

    companion object {
        const val EMPTY_FIELDS_MESSAGE = "Please enter both your username and password."
    }
}