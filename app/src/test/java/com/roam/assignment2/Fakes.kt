package com.roam.assignment2

import com.roam.assignment2.data.remote.NitApiService
import com.roam.assignment2.data.remote.dto.DashboardResponse
import com.roam.assignment2.data.remote.dto.LoginRequest
import com.roam.assignment2.data.remote.dto.LoginResponse
import com.roam.assignment2.data.repository.AuthRepository
import com.roam.assignment2.data.repository.DashboardRepository
import com.roam.assignment2.domain.model.DashboardData
import com.roam.assignment2.util.ApiResult
import kotlinx.coroutines.CompletableDeferred
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeAuthRepository : AuthRepository {
    var result: ApiResult<String> = ApiResult.Success("footscray")
    /** When set, [login] suspends until the gate is completed (lets tests observe the loading state). */
    var gate: CompletableDeferred<Unit>? = null
    var callCount = 0
        private set
    var lastUsername: String? = null
        private set
    var lastPassword: String? = null
        private set

    override suspend fun login(username: String, password: String): ApiResult<String> {
        callCount++
        lastUsername = username
        lastPassword = password
        gate?.await()
        return result
    }
}

class FakeDashboardRepository : DashboardRepository {
    var result: ApiResult<DashboardData> = ApiResult.Success(DashboardData(emptyList(), 0))
    var callCount = 0
        private set
    var lastKeypass: String? = null
        private set

    override suspend fun getDashboard(keypass: String): ApiResult<DashboardData> {
        callCount++
        lastKeypass = keypass
        return result
    }
}

class FakeNitApiService : NitApiService {
    var loginHandler: suspend (LoginRequest) -> LoginResponse = { LoginResponse("footscray") }
    var dashboardHandler: suspend (String) -> DashboardResponse = { DashboardResponse(emptyList(), 0) }

    override suspend fun login(request: LoginRequest): LoginResponse = loginHandler(request)
    override suspend fun getDashboard(keypass: String): DashboardResponse = dashboardHandler(keypass)
}

fun httpException(code: Int): HttpException =
    HttpException(Response.error<Any>(code, "".toResponseBody()))