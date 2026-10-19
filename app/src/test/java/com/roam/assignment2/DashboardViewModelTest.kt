package com.roam.assignment2

import androidx.lifecycle.SavedStateHandle
import com.roam.assignment2.domain.model.DashboardData
import com.roam.assignment2.domain.model.Entity
import com.roam.assignment2.ui.dashboard.DashboardUiState
import com.roam.assignment2.ui.dashboard.DashboardViewModel
import com.roam.assignment2.util.ApiResult
import com.roam.assignment2.util.ErrorMessages
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeDashboardRepository

    private val entity = Entity(
        properties = linkedMapOf("bookTitle" to "Dune", "author" to "Frank Herbert"),
        description = "A desert planet epic."
    )

    @Before
    fun setUp() {
        repository = FakeDashboardRepository()
    }

    private fun createViewModel(keypass: String? = "footscray") = DashboardViewModel(
        repository = repository,
        savedStateHandle = SavedStateHandle(
            if (keypass == null) emptyMap() else mapOf(DashboardViewModel.KEY_KEYPASS to keypass)
        )
    )

    @Test
    fun `loads entities on creation using the keypass from the intent`() {
        repository.result = ApiResult.Success(DashboardData(listOf(entity), total = 1))

        val viewModel = createViewModel()

        assertEquals(DashboardUiState.Success(listOf(entity), 1), viewModel.uiState.value)
        assertEquals("footscray", repository.lastKeypass)
    }

    @Test
    fun `exposes an error when the repository fails`() {
        repository.result = ApiResult.Error(ErrorMessages.NETWORK)

        val viewModel = createViewModel()

        assertEquals(DashboardUiState.Error(ErrorMessages.NETWORK), viewModel.uiState.value)
    }

    @Test
    fun `missing keypass shows an error and does not call the repository`() {
        val viewModel = createViewModel(keypass = null)

        assertEquals(
            DashboardUiState.Error(DashboardViewModel.MISSING_KEYPASS_MESSAGE),
            viewModel.uiState.value
        )
        assertEquals(0, repository.callCount)
    }

    @Test
    fun `load retries after a failure`() {
        repository.result = ApiResult.Error(ErrorMessages.NETWORK)
        val viewModel = createViewModel()

        repository.result = ApiResult.Success(DashboardData(listOf(entity), total = 1))
        viewModel.load()

        assertEquals(DashboardUiState.Success(listOf(entity), 1), viewModel.uiState.value)
        assertEquals(2, repository.callCount)
    }

    @Test
    fun `empty list is still a success state`() {
        repository.result = ApiResult.Success(DashboardData(emptyList(), total = 0))

        val viewModel = createViewModel()

        assertEquals(DashboardUiState.Success(emptyList(), 0), viewModel.uiState.value)
    }
}