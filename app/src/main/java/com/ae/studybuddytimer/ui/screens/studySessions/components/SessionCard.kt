package com.ae.studybuddytimer.ui.screens.studySessions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ae.studybuddytimer.data.SessionType
import com.ae.studybuddytimer.data.StudySession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionCard(
    session: StudySession,
    onCapturePhoto: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session Type Icon
            Icon(
                imageVector = when (session.type) {
                    SessionType.STUDY -> Icons.Default.School
                    SessionType.SHORT_BREAK -> Icons.Default.Coffee
                    SessionType.LONG_BREAK -> Icons.Default.Weekend
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = when (session.type) {
                    SessionType.STUDY -> MaterialTheme.colorScheme.primary
                    SessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                    SessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Session Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (session.type) {
                        SessionType.STUDY -> "Study Session"
                        SessionType.SHORT_BREAK -> "Short Break"
                        SessionType.LONG_BREAK -> "Long Break"
                    },
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.duration} minutes",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(session.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Photo/Camera Button
            if (session.type == SessionType.STUDY) {
                if (session.notePhotoPath != null) {
                    AsyncImage(
                        model = session.notePhotoPath,
                        contentDescription = "Study notes",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { /* Show full image */ },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    IconButton(onClick = onCapturePhoto) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Capture notes"
                        )
                    }
                }
            }
        }
    }
}