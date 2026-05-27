package com.muscu.app.data.repository

import com.muscu.app.data.model.WorkoutSession
import com.muscu.app.data.model.WorkoutSessionDao
import java.util.UUID

class WorkoutSessionRepository(private val sessionDao: WorkoutSessionDao) {

    suspend fun startSession(dayOfWeek: Int): WorkoutSession {
        val session = WorkoutSession(
            id = UUID.randomUUID().toString(),
            dayOfWeek = dayOfWeek,
            dateTimestamp = System.currentTimeMillis()
        )
        sessionDao.insert(session)
        return session
    }

    suspend fun getLastForDay(day: Int): WorkoutSession? = sessionDao.getLastSessionForDay(day)

    suspend fun update(session: WorkoutSession) = sessionDao.update(session)
}
