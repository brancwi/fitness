package com.muscu.app.domain.calculator

import java.util.Calendar

object NextWorkoutDayCalculator {

    fun calculate(workoutDays: List<Int>, dayNames: Map<Int, String>): Pair<Int, String> {
        val todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        // Calendar.DAY_OF_WEEK : 1=Dimanche, 2=Lundi... on convertit en ISO (1=Lundi, 7=Dimanche)
        val isoDay = if (todayIndex == Calendar.SUNDAY) 7 else todayIndex - 1

        val sortedDays = workoutDays.sorted()
        val nextDayIndex = sortedDays.firstOrNull { it >= isoDay } ?: sortedDays.first()

        val dayName = dayNames[nextDayIndex] ?: "Jour $nextDayIndex"

        return nextDayIndex to dayName
    }
}
