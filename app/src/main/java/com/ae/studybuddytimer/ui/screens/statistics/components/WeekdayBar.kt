package com.ae.studybuddytimer.ui.screens.statistics.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeekdayBar(
    day: String,
    minutes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.weight(1f)
        ) {
            // Background bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {}

            // Progress bar
            val maxMinutes = 240f // 4 hours max for scaling
            val progress = (minutes / maxMinutes).coerceIn(0f, 1f)

            Surface(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(24.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {}

            // Time text
            Text(
                text = "${minutes / 60}h ${minutes % 60}m",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (progress > 0.3f) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}