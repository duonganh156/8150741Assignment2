package com.roam.assignment2.data.repository

import com.roam.assignment2.data.remote.NitApiService
import com.roam.assignment2.data.remote.dto.LoginRequest
import com.roam.assignment2.util.ApiResult
import com.roam.assignment2.util.ErrorMessages
import com.roam.assignment2.util.safeApiCall
import javax.inject.Inject

interface AuthRepository {
    /** Returns the keypass on success. */
    suspend fun login(username: String, password: String): ApiResult<String>
}

class AuthRepositoryImpl @Inject constructor(
    private val api: NitApiService
) : AuthRepository {

    override suspend fun login(username: String, password: String): ApiResult<String> =
        safeApiCall(
            httpErrorMessage = { code ->
                if (code in CREDENTIAL_ERROR_CODES) ErrorMessages.INVALID_CREDENTIALS
                else ErrorMessages.server(code)
            }
        ) {
            requireNotNull(api.login(LoginRequest(username, password)).keypass) {
                "Login response did not contain a keypass"
            }
        }

    private companion object {
        val CREDENTIAL_ERROR_CODES = setOf(400, 401, 403, 404)
    }
}