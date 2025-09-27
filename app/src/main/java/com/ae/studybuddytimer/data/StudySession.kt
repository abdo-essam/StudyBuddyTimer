package com.ae.studybuddytimer.data

data class StudySession(
    val id: Long = System.currentTimeMillis(),
    val subject: String = "General Study",
    val duration: Int, // in minutes
    val type: SessionType,
    val timestamp: Long = System.currentTimeMillis(),
    val notePhotoPath: String? = null,
    val completed: Boolean = true
)

enum class SessionType {
    STUDY, SHORT_BREAK, LONG_BREAK
}

data class TimerState(
    val totalSeconds: Int,
    val secondsRemaining: Int,
    val isRunning: Boolean,
    val sessionType: SessionType
)

data class Settings(
    val studyDuration: Int = 25, // minutes
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val sessionsUntilLongBreak: Int = 4,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyGoalHours: Int = 4
)