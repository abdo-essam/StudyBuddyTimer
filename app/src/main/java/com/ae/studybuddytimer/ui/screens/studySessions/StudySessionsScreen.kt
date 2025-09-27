package com.ae.studybuddytimer.ui.screens.studySessions

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.ae.studybuddytimer.data.StudyRepository
import com.ae.studybuddytimer.data.StudySession
import com.ae.studybuddytimer.ui.screens.studySessions.components.SessionCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StudySessionsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { StudyRepository(context) }
    val sessions by repository.sessionsFlow.collectAsState(initial = emptyList())

    var selectedSession by remember { mutableStateOf<StudySession?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            selectedSession?.let { session ->
                val updatedSession = session.copy(notePhotoPath = photoUri.toString())
                val allSessions = sessions.toMutableList()
                val index = allSessions.indexOfFirst { it.id == session.id }
                if (index != -1) {
                    allSessions[index] = updatedSession
                    repository.saveSessions(allSessions)
                }
            }
        }
        selectedSession = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Sessions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No study sessions yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    SessionCard(
                        session = session,
                        onCapturePhoto = {
                            selectedSession = session
                            if (cameraPermissionState.status.isGranted) {
                                val photoFile = File(context.filesDir, "study_${session.id}.jpg")
                                photoUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    photoFile
                                )
                                photoUri?.let { takePictureLauncher.launch(it) }
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    )
                }
            }
        }
    }
}

