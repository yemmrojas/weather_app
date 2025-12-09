package com.yei.dev.weather_app.util

/**
 * Formatea una fecha en formato ISO (YYYY-MM-DD) a formato corto (DD/MM)
 *
 * Ejemplo:
 * "2024-12-08".toShortDateFormat() -> "08/12"
 */
fun String.toShortDateFormat(): String {
    return try {
        val parts = this.split("-")
        if (parts.size == 3) {
            val day = parts[2]
            val month = parts[1]
            "$day/$month"
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }
}
