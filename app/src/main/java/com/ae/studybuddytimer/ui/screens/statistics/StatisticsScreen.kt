package com.ae.studybuddytimer.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.studybuddytimer.data.StudyRepository
import com.ae.studybuddytimer.ui.screens.statistics.components.StatisticCard
import com.ae.studybuddytimer.ui.screens.statistics.components.WeekdayBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { StudyRepository(context) }

    var weeklyData by remember { mutableStateOf(repository.getWeeklyStudyTime()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Weekly Summary Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Weekly Summary",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simple bar chart
                    weeklyData.forEach { (day, minutes) ->
                        WeekdayBar(day = day, minutes = minutes)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticCard(
                    title = "Total This Week",
                    value = "${weeklyData.values.sum() / 60}h ${weeklyData.values.sum() % 60}m"
                )

                StatisticCard(
                    title = "Daily Average",
                    value = "${
                        weeklyData.values.average().toInt() / 60
                    }h ${weeklyData.values.average().toInt() % 60}m"
                )
            }
        }
    }
}



