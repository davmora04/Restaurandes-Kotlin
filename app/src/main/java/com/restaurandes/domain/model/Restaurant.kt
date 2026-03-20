package com.restaurandes.domain.model

import java.util.Calendar

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val priceRange: String, // "$", "$$", "$$$"
    val rating: Double,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    val openingHours: String,
    val isOpen: Boolean,
    val lastUpdated: Long,
    val tags: List<String> = emptyList(), // vegetarian, vegan, gluten-free, etc.
    val reviewCount: Int = 0
) {
    /**
     * Calcula si el restaurante está abierto comparando openingHours con la hora actual.
     */
    fun isCurrentlyOpen(): Boolean {
        return try {
            val hours = openingHours.trim()

            if (hours.isEmpty() || hours == "24 horas" || hours.contains("24/7", ignoreCase = true)) {
                return true
            }

            // Soporta varios tipos de guion: –, -, —
            val parts = hours.split("–", "-", "—").map { it.trim() }
            if (parts.size != 2) return isOpen

            val openTime = parts[0]
            val closeTime = parts[1]

            val calendar = Calendar.getInstance()
            val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentTimeInMinutes = currentHour24 * 60 + currentMinute

            val openTimeInMinutes = parseTimeToMinutes(openTime)
            val closeTimeInMinutes = parseTimeToMinutes(closeTime)

            if (openTimeInMinutes == -1 || closeTimeInMinutes == -1) {
                return isOpen
            }

            return if (closeTimeInMinutes < openTimeInMinutes) {
                // Cruza medianoche, ej: 10 PM - 2 AM
                currentTimeInMinutes >= openTimeInMinutes || currentTimeInMinutes < closeTimeInMinutes
            } else {
                // Horario normal, ej: 10 AM - 7 PM
                currentTimeInMinutes in openTimeInMinutes until closeTimeInMinutes
            }
        } catch (e: Exception) {
            isOpen
        }
    }

    private fun parseTimeToMinutes(time: String): Int {
        return try {
            val cleanTime = time.trim()
            val isPM = cleanTime.contains("PM", ignoreCase = true)
            val isAM = cleanTime.contains("AM", ignoreCase = true)

            val timePart = cleanTime
                .replace("AM", "", ignoreCase = true)
                .replace("PM", "", ignoreCase = true)
                .trim()

            val parts = timePart.split(":")
            val hour = parts[0].toInt()
            val minute = if (parts.size > 1) parts[1].toInt() else 0

            val hour24 = when {
                !isAM && !isPM -> hour
                isPM && hour != 12 -> hour + 12
                isAM && hour == 12 -> 0
                else -> hour
            }

            hour24 * 60 + minute
        } catch (e: Exception) {
            -1
        }
    }
}