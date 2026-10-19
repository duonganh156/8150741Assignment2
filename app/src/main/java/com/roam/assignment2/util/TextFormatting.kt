package com.roam.assignment2.util

private val CAMEL_CASE_BOUNDARY = Regex("(?<=[a-z0-9])(?=[A-Z])")

/** Turns an API property name such as `publicationYear` or `book_title` into "Publication Year". */
fun String.toDisplayLabel(): String =
    replace('_', ' ')
        .replace(CAMEL_CASE_BOUNDARY, " ")
        .split(' ')
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
