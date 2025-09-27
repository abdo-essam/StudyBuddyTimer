package com.ae.studybuddytimer.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ae.studybuddytimer.data.StudyRepository
import com.ae.studybuddytimer.ui.screens.settings.components.DurationSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { StudyRepository(context) }
    var settings by remember { mutableStateOf(repository.getSettings()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Timer Durations
            Text(
                text = "Timer Durations",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DurationSetting(
                label = "Study Duration",
                value = settings.studyDuration,
                onValueChange = {
                    settings = settings.copy(studyDuration = it)
                    repository.saveSettings(settings)
                }
            )

            DurationSetting(
                label = "Short Break Duration",
                value = settings.shortBreakDuration,
                onValueChange = {
                    settings = settings.copy(shortBreakDuration = it)
                    repository.saveSettings(settings)
                }
            )

            DurationSetting(
                label = "Long Break Duration",
                value = settings.longBreakDuration,
                onValueChange = {
                    settings = settings.copy(longBreakDuration = it)
                    repository.saveSettings(settings)
                }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Notifications
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sound Enabled")
                Switch(
                    checked = settings.soundEnabled,
                    onCheckedChange = {
                        settings = settings.copy(soundEnabled = it)
                        repository.saveSettings(settings)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vibration Enabled")
                Switch(
                    checked = settings.vibrationEnabled,
                    onCheckedChange = {
                        settings = settings.copy(vibrationEnabled = it)
                        repository.saveSettings(settings)
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Goals
            Text(
                text = "Goals",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DurationSetting(
                label = "Daily Goal (hours)",
                value = settings.dailyGoalHours,
                onValueChange = {
                    settings = settings.copy(dailyGoalHours = it)
                    repository.saveSettings(settings)
                },
                suffix = "hours"
            )
        }
    }
}

