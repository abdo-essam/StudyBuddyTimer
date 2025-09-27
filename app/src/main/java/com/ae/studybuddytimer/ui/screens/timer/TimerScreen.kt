package com.ae.studybuddytimer.ui.screens.timer


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ae.studybuddytimer.data.SessionType
import com.ae.studybuddytimer.ui.screens.timer.components.StatCard
import com.ae.studybuddytimer.ui.screens.timer.components.TimerCircle
import com.ae.studybuddytimer.viewmodels.TimerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigateToSessions: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: TimerViewModel = viewModel()
    val timerState by viewModel.timerState.collectAsState()
    val todayStudyTime by viewModel.todayStudyTime.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()

    var showGestureHint by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Buddy Timer") },
                actions = {
                    IconButton(onClick = onNavigateToSessions) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Sessions")
                    }
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            viewModel.toggleTimer()
                            showGestureHint = "Timer toggled!"
                        },
                        onLongPress = {
                            viewModel.resetTimer()
                            showGestureHint = "Timer reset!"
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Session Type
            Text(
                text = when (timerState?.sessionType) {
                    SessionType.STUDY -> "Study Time"
                    SessionType.SHORT_BREAK -> "Short Break"
                    SessionType.LONG_BREAK -> "Long Break"
                    null -> "Ready to Study"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Timer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                TimerCircle(
                    progress = timerState?.let {
                        1f - (it.secondsRemaining.toFloat() / it.totalSeconds)
                    } ?: 0f,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = timerState?.let {
                        val minutes = it.secondsRemaining / 60
                        val seconds = it.secondsRemaining % 60
                        String.format("%02d:%02d", minutes, seconds)
                    } ?: "00:00",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleTimer() },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = when {
                            timerState == null -> "Start"
                            timerState!!.isRunning -> "Pause"
                            else -> "Resume"
                        }
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Reset")
                }
            }

            // Gesture hint
            showGestureHint?.let { hint ->
                LaunchedEffect(hint) {
                    delay(2000)
                    showGestureHint = null
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = hint,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gesture Instructions
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Gesture Controls:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Double tap: Start/Pause",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Long press: Reset",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Today",
                    value = "${todayStudyTime / 60}h ${todayStudyTime % 60}m"
                )
                StatCard(
                    label = "Streak",
                    value = "$currentStreak days"
                )
                StatCard(
                    label = "Sessions",
                    value = sessionCount.toString()
                )
            }
        }
    }
}

