package com.roam.assignment2.domain.model

import android.os.Parcelable
import com.roam.assignment2.util.toDisplayLabel
import kotlinx.parcelize.Parcelize

/*
 * A single item returned by the dashboard endpoint.
 *
 * Property names differ between API topics, so every property except
 * `description` is kept as an ordered key -> value map.
 */
@Parcelize
data class Entity(
    val properties: Map<String, String>,
    val description: String
) : Parcelable {

    /** The first property doubles as the headline of the entity. */
    val title: String
        get() = properties.values.firstOrNull().orEmpty()

    /** Label/value pairs shown in the list: everything except the headline and description. */
    fun summary(): List<Pair<String, String>> =
        properties.entries.drop(1).map { it.key.toDisplayLabel() to it.value }
}