package com.restaurandes.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
     * Calcula si el restaurante está abierto comparando openingHours con la hora actual
     */
    fun isCurrentlyOpen(): Boolean {
        return try {
            // Parse openingHours format: "11:30 AM–6:30 PM" or "10 AM–7 PM"
            val hours = openingHours.trim()
            if (hours.isEmpty() || hours == "24 horas" || hours.contains("24/7", ignoreCase = true)) {
                return true
            }
            
            // Split by different dash types (–, -, —)
            val parts = hours.split("–", "-", "—").map { it.trim() }
            if (parts.size != 2) return isOpen // Fallback to isOpen field if format is unexpected
            
            val openTime = parts[0]
            val closeTime = parts[1]
            
            // Get current time
            val calendar = Calendar.getInstance()
            val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentTimeInMinutes = currentHour24 * 60 + currentMinute
            
            // Parse opening time
            val openTimeInMinutes = parseTimeToMinutes(openTime)
            val closeTimeInMinutes = parseTimeToMinutes(closeTime)
            
            if (openTimeInMinutes == -1 || closeTimeInMinutes == -1) {
                return isOpen // Fallback if parsing fails
            }
            
            // Check if currently open
            if (closeTimeInMinutes < openTimeInMinutes) {
                // Crosses midnight (e.g., 10 PM - 2 AM)
                return currentTimeInMinutes >= openTimeInMinutes || currentTimeInMinutes < closeTimeInMinutes
            } else {
                // Normal hours (e.g., 10 AM - 7 PM)
                return currentTimeInMinutes in openTimeInMinutes..<closeTimeInMinutes
            }
        } catch (e: Exception) {
            // If any parsing error, fallback to isOpen field
            return isOpen
        }
    }
    
    private fun parseTimeToMinutes(time: String): Int {
        return try {
            val cleanTime = time.trim()
            val isPM = cleanTime.contains("PM", ignoreCase = true)
            val isAM = cleanTime.contains("AM", ignoreCase = true)
            
            // Remove AM/PM
            val timePart = cleanTime.replace("AM", "", ignoreCase = true)
                .replace("PM", "", ignoreCase = true)
                .trim()
            
            val parts = timePart.split(":")
            val hour = parts[0].toInt()
            val minute = if (parts.size > 1) parts[1].toInt() else 0
            
            // Convert to 24-hour format
            var hour24 = when {
                !isAM && !isPM -> hour // Already 24-hour format
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
