package com.ae.studybuddytimer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost


@Composable
fun StudyBuddyNavigation (
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = "timer"
    ){

    }
}