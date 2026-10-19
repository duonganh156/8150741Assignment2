package com.roam.assignment2.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.roam.assignment2.data.remote.dto.DashboardResponse
import com.roam.assignment2.data.remote.dto.LoginRequest
import com.roam.assignment2.data.remote.dto.LoginResponse

interface NitApiService {

    @POST("footscray/auth")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponse
}