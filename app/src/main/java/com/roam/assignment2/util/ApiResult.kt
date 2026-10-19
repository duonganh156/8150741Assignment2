package com.roam.assignment2.util

/** Result of an API call: either data or a user-friendly error message. */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String) : ApiResult<Nothing>
}