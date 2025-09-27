package com.ae.studybuddytimer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ae.studybuddytimer.ui.screens.timer.TimerScreen
import com.ae.studybuddytimer.ui.screens.settings.SettingsScreen
import com.ae.studybuddytimer.ui.screens.statistics.StatisticsScreen
import com.ae.studybuddytimer.ui.screens.studySessions.StudySessionsScreen

@Composable
fun StudyBuddyNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "timer"
    ) {
        composable("timer") {
            TimerScreen(
                onNavigateToSessions = { navController.navigate("sessions") },
                onNavigateToStatistics = { navController.navigate("statistics") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("sessions") {
            StudySessionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("statistics") {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}