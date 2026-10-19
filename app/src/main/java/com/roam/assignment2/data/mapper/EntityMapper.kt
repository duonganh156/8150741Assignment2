package com.roam.assignment2.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.roam.assignment2.data.remote.dto.DashboardResponse
import com.roam.assignment2.domain.model.DashboardData
import com.roam.assignment2.domain.model.Entity
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map
import kotlin.collections.orEmpty

private const val DESCRIPTION_KEY = "description"

fun JsonObject.toEntity(): Entity {
    val properties = LinkedHashMap<String, String>()
    var description = ""
    for ((key, value) in entrySet()) {
        val text = value.asDisplayString()
        if (key.equals(DESCRIPTION_KEY, ignoreCase = true)) {
            description = text
        } else {
            properties[key] = text
        }
    }
    return Entity(properties = properties, description = description)
}

fun DashboardResponse.toDashboardData(): DashboardData {
    val items = entities.orEmpty().map { it.toEntity() }
    return DashboardData(entities = items, total = entityTotal ?: items.size)
}

private fun JsonElement.asDisplayString(): String = when {
    isJsonNull -> ""
    isJsonPrimitive -> asString
    else -> toString()
}