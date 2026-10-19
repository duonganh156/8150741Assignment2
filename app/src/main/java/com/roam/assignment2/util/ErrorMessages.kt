package com.roam.assignment2.util

/** User-facing error messages shown by the login and dashboard screens. */
object ErrorMessages {
    const val INVALID_CREDENTIALS = "Invalid username or password. Please check your details and try again."
    const val SESSION_INVALID = "Your session is no longer valid. Please log in again."
    const val NETWORK =
        "Couldn't reach the server. Check your connection and try again. " +
                "The server can take up to a minute to wake up."
    const val UNKNOWN = "Something went wrong. Please try again."

    fun server(code: Int): String = "The server returned an error (HTTP $code). Please try again later."
}