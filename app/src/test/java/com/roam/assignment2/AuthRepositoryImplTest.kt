package com.roam.assignment2

import com.roam.assignment2.data.remote.dto.LoginResponse
import com.roam.assignment2.data.repository.AuthRepositoryImpl
import com.roam.assignment2.util.ApiResult
import com.roam.assignment2.util.ErrorMessages
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AuthRepositoryImplTest {

    private lateinit var api: FakeNitApiService
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        api = FakeNitApiService()
        repository = AuthRepositoryImpl(api)
    }

    @Test
    fun `returns keypass on success and sends the credentials`() = runTest {
        var sentUsername: String? = null
        var sentPassword: String? = null
        api.loginHandler = {
            sentUsername = it.username
            sentPassword = it.password
            LoginResponse("footscray")
        }

        val result = repository.login("8150741", "Adam")

        assertEquals(ApiResult.Success("footscray"), result)
        assertEquals("8150741", sentUsername)
        assertEquals("Adam", sentPassword)
    }

    @Test
    fun `maps 401 to invalid credentials`() = runTest {
        api.loginHandler = { throw httpException(401) }

        assertEquals(ApiResult.Error(ErrorMessages.INVALID_CREDENTIALS), repository.login("a", "b"))
    }

    @Test
    fun `maps 404 to invalid credentials`() = runTest {
        api.loginHandler = { throw httpException(404) }

        assertEquals(ApiResult.Error(ErrorMessages.INVALID_CREDENTIALS), repository.login("a", "b"))
    }

    @Test
    fun `maps 500 to a server error message`() = runTest {
        api.loginHandler = { throw httpException(500) }

        assertEquals(ApiResult.Error(ErrorMessages.server(500)), repository.login("a", "b"))
    }

    @Test
    fun `maps IOException to a network error message`() = runTest {
        api.loginHandler = { throw IOException("timeout") }

        assertEquals(ApiResult.Error(ErrorMessages.NETWORK), repository.login("a", "b"))
    }

    @Test
    fun `missing keypass becomes an unknown error`() = runTest {
        api.loginHandler = { LoginResponse(null) }

        assertEquals(ApiResult.Error(ErrorMessages.UNKNOWN), repository.login("a", "b"))
    }
}