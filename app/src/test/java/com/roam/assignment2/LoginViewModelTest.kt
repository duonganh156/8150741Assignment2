package com.roam.assignment2

import com.roam.assignment2.ui.login.LoginUiState
import com.roam.assignment2.ui.login.LoginViewModel
import com.roam.assignment2.util.ApiResult
import com.roam.assignment2.util.ErrorMessages
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `initial state is idle`() {
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `blank username shows validation error without calling the repository`() {
        viewModel.login("", "Adam")

        assertEquals(LoginUiState.Error(LoginViewModel.EMPTY_FIELDS_MESSAGE), viewModel.uiState.value)
        assertEquals(0, repository.callCount)
    }

    @Test
    fun `blank password shows validation error without calling the repository`() {
        viewModel.login("8150741", "   ")

        assertEquals(LoginUiState.Error(LoginViewModel.EMPTY_FIELDS_MESSAGE), viewModel.uiState.value)
        assertEquals(0, repository.callCount)
    }

    @Test
    fun `successful login exposes the keypass`() {
        repository.result = ApiResult.Success("footscray")

        viewModel.login("8150741", "Adam")

        assertEquals(LoginUiState.Success("footscray"), viewModel.uiState.value)
        assertEquals("8150741", repository.lastUsername)
        assertEquals("Adam", repository.lastPassword)
    }

    @Test
    fun `username is trimmed but password is sent as typed`() {
        viewModel.login("  8150741  ", " Adam ")

        assertEquals("8150741", repository.lastUsername)
        assertEquals(" Adam ", repository.lastPassword)
    }

    @Test
    fun `failed login exposes the error message`() {
        repository.result = ApiResult.Error(ErrorMessages.INVALID_CREDENTIALS)

        viewModel.login("8150741", "wrong")

        assertEquals(LoginUiState.Error(ErrorMessages.INVALID_CREDENTIALS), viewModel.uiState.value)
    }

    @Test
    fun `state is loading while the request is in flight`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.login("8150741", "Adam")
        assertEquals(LoginUiState.Loading, viewModel.uiState.value)

        gate.complete(Unit)
        assertEquals(LoginUiState.Success("footscray"), viewModel.uiState.value)
    }

    @Test
    fun `second login is ignored while one is already loading`() {
        repository.gate = CompletableDeferred()

        viewModel.login("8150741", "Adam")
        viewModel.login("8150741", "Adam")

        assertEquals(1, repository.callCount)
    }

    @Test
    fun `user can retry after a failure`() {
        repository.result = ApiResult.Error(ErrorMessages.NETWORK)
        viewModel.login("8150741", "Adam")

        repository.result = ApiResult.Success("footscray")
        viewModel.login("8150741", "Adam")

        assertEquals(LoginUiState.Success("footscray"), viewModel.uiState.value)
    }
}