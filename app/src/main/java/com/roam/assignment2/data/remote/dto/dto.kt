package com.roam.assignment2.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("keypass") val keypass: String?
)

/*
 * Entities are kept as raw [JsonObject]s because their property names vary
 * between topics. They are converted to domain models by the mapper.
 */
data class DashboardResponse(
    @SerializedName("entities") val entities: List<JsonObject>?,
    @SerializedName("entityTotal") val entityTotal: Int?
)