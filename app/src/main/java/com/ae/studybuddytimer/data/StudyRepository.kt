package com.ae.studybuddytimer.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyRepository(context: Context) {
    private val prefs = context.getSharedPreferences("study_buddy_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _sessionsFlow = MutableStateFlow<List<StudySession>>(emptyList())
    val sessionsFlow: Flow<List<StudySession>> = _sessionsFlow

    companion object {
        private const val KEY_SESSIONS = "study_sessions"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_TIMER_STATE = "timer_state"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LAST_STUDY_DATE = "last_study_date"
    }

    init {
        loadSessions()
    }

    private fun loadSessions() {
        _sessionsFlow.value = getAllSessions()
    }

    // Session management
    fun saveSessions(sessions: List<StudySession>) {
        val json = gson.toJson(sessions)
        prefs.edit { putString(KEY_SESSIONS, json) }
        _sessionsFlow.value = sessions
    }

    fun getAllSessions(): List<StudySession> {
        val json = prefs.getString(KEY_SESSIONS, "[]")
        val type = object : TypeToken<List<StudySession>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addSession(session: StudySession) {
        val sessions = getAllSessions().toMutableList()
        sessions.add(0, session) // Add to beginning
        saveSessions(sessions)
        updateStreak()
    }

    // Settings management
    fun saveSettings(settings: Settings) {
        val json = gson.toJson(settings)
        prefs.edit { putString(KEY_SETTINGS, json) }
    }

    fun getSettings(): Settings {
        val json = prefs.getString(KEY_SETTINGS, null)
        return if (json != null) {
            gson.fromJson(json, Settings::class.java)
        } else {
            Settings()
        }
    }

    // Timer state for persistence
    fun saveTimerState(state: TimerState?) {
        val json = if (state != null) gson.toJson(state) else null
        prefs.edit { putString(KEY_TIMER_STATE, json) }
    }

    fun getTimerState(): TimerState? {
        val json = prefs.getString(KEY_TIMER_STATE, null)
        return if (json != null) {
            gson.fromJson(json, TimerState::class.java)
        } else {
            null
        }
    }

    // Statistics
    fun getTodayStudyTime(): Int {
        val todayStart = getTodayStartMillis()
        return getAllSessions()
            .filter { it.type == SessionType.STUDY && it.timestamp >= todayStart }
            .sumOf { it.duration }
    }

    fun getWeeklyStudyTime(): Map<String, Int> {
        val weekData = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()

        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val dayStart = calendar.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000

            val dayName = SimpleDateFormat("EEE", Locale.getDefault())
                .format(calendar.time)

            val dayStudyTime = getAllSessions()
                .filter {
                    it.type == SessionType.STUDY &&
                            it.timestamp >= dayStart &&
                            it.timestamp < dayEnd
                }
                .sumOf { it.duration }

            weekData[dayName] = dayStudyTime
        }

        return weekData
    }

    fun getCurrentStreak(): Int {
        return prefs.getInt(KEY_CURRENT_STREAK, 0)
    }

    private fun updateStreak() {
        val lastStudyDate = prefs.getLong(KEY_LAST_STUDY_DATE, 0)
        val todayStart = getTodayStartMillis()
        val yesterdayStart = todayStart - 24 * 60 * 60 * 1000

        val currentStreak = when {
            lastStudyDate >= todayStart -> {
                prefs.getInt(KEY_CURRENT_STREAK, 0)
            }
            lastStudyDate >= yesterdayStart -> {
                prefs.getInt(KEY_CURRENT_STREAK, 0) + 1
            }
            else -> {
                1
            }
        }

        prefs.edit {
            putInt(KEY_CURRENT_STREAK, currentStreak)
                .putLong(KEY_LAST_STUDY_DATE, System.currentTimeMillis())
        }
    }

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}