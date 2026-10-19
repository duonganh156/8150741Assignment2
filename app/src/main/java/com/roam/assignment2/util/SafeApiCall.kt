package com.roam.assignment2.util

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/*
 * Runs [block] and converts any failure into an [ApiResult.Error] with a friendly message.
 * Coroutine cancellation is always rethrown so structured concurrency keeps working.
 */
suspend fun <T> safeApiCall(
    httpErrorMessage: (code: Int) -> String = ErrorMessages::server,
    block: suspend () -> T
): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    ApiResult.Error(httpErrorMessage(e.code()))
} catch (e: IOException) {
    ApiResult.Error(ErrorMessages.NETWORK)
} catch (e: Exception) {
    ApiResult.Error(ErrorMessages.UNKNOWN)
}