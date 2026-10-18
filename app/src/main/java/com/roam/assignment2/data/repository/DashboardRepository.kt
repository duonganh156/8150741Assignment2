package com.roam.assignment2.data.repositories

import com.roam.assignment2.data.mapper.toDashboardData
import com.roam.assignment2.data.remote.NitApiService
import com.roam.assignment2.domain.model.DashboardData
import com.roam.assignment2.util.ApiResult
import com.roam.assignment2.util.ErrorMessages
import com.roam.assignment2.util.safeApiCall
import javax.inject.Inject

interface DashboardRepository {
    suspend fun getDashboard(keypass: String): ApiResult<DashboardData>
}

class DashboardRepositoryImpl @Inject constructor(
    private val api: NitApiService
) : DashboardRepository {

    override suspend fun getDashboard(keypass: String): ApiResult<DashboardData> =
        safeApiCall(
            httpErrorMessage = { code ->
                if (code in SESSION_ERROR_CODES) ErrorMessages.SESSION_INVALID
                else ErrorMessages.server(code)
            }
        ) {
            api.getDashboard(keypass).toDashboardData()
        }

    private companion object {
        val SESSION_ERROR_CODES = setOf(400, 401, 403, 404)
    }
}