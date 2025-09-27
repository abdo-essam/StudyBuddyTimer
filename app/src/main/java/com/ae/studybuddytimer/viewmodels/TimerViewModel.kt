package com.ae.studybuddytimer.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ae.studybuddytimer.data.SessionType
import com.ae.studybuddytimer.data.StudyRepository
import com.ae.studybuddytimer.data.StudySession
import com.ae.studybuddytimer.data.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudyRepository(application)
    private val settings = repository.getSettings()

    private val _timerState = MutableStateFlow<TimerState?>(null)
    val timerState: StateFlow<TimerState?> = _timerState.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _todayStudyTime = MutableStateFlow(0)
    val todayStudyTime: StateFlow<Int> = _todayStudyTime.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private var currentSessionType = SessionType.STUDY

    init {
        // Restore timer state if exists
        repository.getTimerState()?.let { state ->
            if (state.isRunning && state.secondsRemaining > 0) {
                resumeTimer(state.secondsRemaining, state.sessionType)
            }
        }
        updateStats()
    }

    fun startNewSession(type: SessionType = SessionType.STUDY) {
        currentSessionType = type
        val duration = when (type) {
            SessionType.STUDY -> repository.getSettings().studyDuration
            SessionType.SHORT_BREAK -> repository.getSettings().shortBreakDuration
            SessionType.LONG_BREAK -> repository.getSettings().longBreakDuration
        }
        startTimer(duration * 60, type)
    }

    fun toggleTimer() {
        _timerState.value?.let { state ->
            if (state.isRunning) {
                pauseTimer()
            } else {
                resumeTimer(state.secondsRemaining, state.sessionType)
            }
        } ?: startNewSession()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        _timerState.value = null
        repository.saveTimerState(null)
    }

    private fun startTimer(totalSeconds: Int, type: SessionType) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                val state = TimerState(
                    totalSeconds = totalSeconds,
                    secondsRemaining = secondsRemaining,
                    isRunning = true,
                    sessionType = type
                )
                _timerState.value = state
                repository.saveTimerState(state)
            }

            @RequiresPermission(Manifest.permission.VIBRATE)
            override fun onFinish() {
                onTimerComplete()
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        _timerState.value?.let { state ->
            _timerState.value = state.copy(isRunning = false)
            repository.saveTimerState(_timerState.value)
        }
    }

    private fun resumeTimer(secondsRemaining: Int, type: SessionType) {
        startTimer(secondsRemaining, type)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun onTimerComplete() {
        // Save completed session
        _timerState.value?.let { state ->
            if (state.sessionType == SessionType.STUDY) {
                val session = StudySession(
                    duration = state.totalSeconds / 60,
                    type = state.sessionType,
                    completed = true
                )
                repository.addSession(session)
                _sessionCount.value += 1
                updateStats()
            }
        }

        // Play sound/vibration based on settings
        playCompletionAlert()

        // Determine next session type
        val nextType = when (currentSessionType) {
            SessionType.STUDY -> {
                val count = _sessionCount.value
                if (count % settings.sessionsUntilLongBreak == 0 && count > 0) {
                    SessionType.LONG_BREAK
                } else {
                    SessionType.SHORT_BREAK
                }
            }

            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> SessionType.STUDY
        }

        // Auto-start next session after a brief delay
        Handler(Looper.getMainLooper()).postDelayed({
            startNewSession(nextType)
        }, 2000)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun playCompletionAlert() {
        if (settings.vibrationEnabled) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 200, 100, 200) // wait, vibrate, wait, vibrate

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }

    fun updateStats() {
        viewModelScope.launch {
            _todayStudyTime.value = repository.getTodayStudyTime()
            _currentStreak.value = repository.getCurrentStreak()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}