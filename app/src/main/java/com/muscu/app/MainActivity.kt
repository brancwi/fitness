package com.muscu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import com.muscu.app.ui.screens.DashboardScreen
import com.muscu.app.ui.screens.ExerciseDetailScreen
import com.muscu.app.ui.screens.ExercisePerformanceScreen
import com.muscu.app.ui.screens.MeasurementHistoryScreen
import com.muscu.app.ui.screens.MeasurementScreen
import com.muscu.app.ui.screens.ProgramScreen
import com.muscu.app.ui.screens.SessionHistoryScreen
import com.muscu.app.ui.screens.SessionWizardScreen
import com.muscu.app.ui.screens.SettingsScreen
import com.muscu.app.ui.screens.WorkoutScreen
import com.muscu.app.ui.screens.CreditsScreen
import com.muscu.app.ui.screens.WorkoutTemplateListScreen
import com.muscu.app.ui.theme.MuscuTheme
import com.muscu.app.viewmodel.DashboardViewModel
import com.muscu.app.viewmodel.ExercisePerformanceViewModel
import com.muscu.app.viewmodel.MeasurementViewModel
import com.muscu.app.viewmodel.ProgramViewModel
import com.muscu.app.viewmodel.SessionHistoryViewModel
import com.muscu.app.viewmodel.SettingsViewModel
import com.muscu.app.viewmodel.WorkoutTemplateViewModel
import com.muscu.app.viewmodel.WorkoutViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Accueil", Icons.Default.Home)
    object Program : Screen("program", "Programme", Icons.Default.List)
    object Measurement : Screen("measurement", "Mensurations", Icons.Default.Straighten)
    object Settings : Screen("settings", "Réglages", Icons.Default.Settings)
    object Workout : Screen("workout/{day}", "Séance", Icons.Default.FitnessCenter)
}

val bottomNavItems = listOf(Screen.Dashboard, Screen.Program, Screen.Measurement, Screen.Settings)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as MuscuApplication
        val repository = app.repository
        val appSettingsRepository = app.appSettingsRepository
        setContent {
            MuscuTheme {
                MuscuApp(repository, appSettingsRepository)
            }
        }
    }
}

@Composable
fun MuscuApp(repository: WorkoutRepository, appSettingsRepository: AppSettingsRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(repository, appSettingsRepository))
                DashboardScreen(
                    viewModel = viewModel,
                    onStartWorkout = { day ->
                        navController.navigate("workout/$day")
                    },
                    onViewTemplates = {
                        navController.navigate("workout_templates")
                    },
                    onViewSessionHistory = {
                        navController.navigate("session_history")
                    }
                )
            }
            composable(Screen.Program.route) {
                val viewModel: ProgramViewModel = viewModel(factory = ProgramViewModel.Factory(repository, appSettingsRepository))
                ProgramScreen(
                    viewModel = viewModel,
                    onStartWorkout = { day ->
                        navController.navigate("workout/$day")
                    },
                    onExerciseInfo = { exId, exName ->
                        navController.navigate("exercise_detail/$exId/${java.net.URLEncoder.encode(exName, "UTF-8")}")
                    }
                )
            }
            composable(Screen.Measurement.route) {
                val viewModel: MeasurementViewModel = viewModel(factory = MeasurementViewModel.Factory(repository, appSettingsRepository))
                MeasurementScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { navController.navigate("measurement_history") }
                )
            }
            composable("measurement_history") {
                val viewModel: MeasurementViewModel = viewModel(factory = MeasurementViewModel.Factory(repository, appSettingsRepository))
                MeasurementHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository, appSettingsRepository))
                SettingsScreen(
                    viewModel = viewModel,
                    onCreditsClick = { navController.navigate("credits") }
                )
            }
            composable("credits") {
                CreditsScreen(onBack = { navController.popBackStack() })
            }
            composable("workout/{day}") { backStackEntry ->
                val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 2
                val templateId = backStackEntry.arguments?.getString("templateId")
                val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(repository, appSettingsRepository))
                WorkoutScreen(
                    viewModel = viewModel,
                    dayOfWeek = day,
                    templateId = templateId,
                    onBack = { navController.popBackStack() },
                    onExerciseInfo = { exId, exName ->
                        navController.navigate("exercise_detail/$exId/${java.net.URLEncoder.encode(exName, "UTF-8")}")
                    }
                )
            }
            composable("exercise_detail/{exerciseId}/{exerciseName}") { backStackEntry ->
                val exId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val exName = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("exerciseName") ?: "",
                    "UTF-8"
                )
                ExerciseDetailScreen(
                    exerciseId = exId,
                    exerciseName = exName,
                    onBack = { navController.popBackStack() },
                    onViewHistory = { id, name ->
                        navController.navigate("exercise_performance/$id/${java.net.URLEncoder.encode(name, "UTF-8")}")
                    }
                )
            }
            composable("exercise_performance/{exerciseId}/{exerciseName}") { backStackEntry ->
                val exId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val exName = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("exerciseName") ?: "",
                    "UTF-8"
                )
                val viewModel: ExercisePerformanceViewModel = viewModel(
                    factory = ExercisePerformanceViewModel.Factory(repository)
                )
                ExercisePerformanceScreen(
                    exerciseName = exName,
                    history = viewModel.uiState.collectAsState().value.history,
                    onBack = { navController.popBackStack() },
                    onDeleteSet = { viewModel.deleteSet(it, exId) }
                )
                LaunchedEffect(exId) {
                    viewModel.loadHistory(exId)
                }
            }
            composable("workout_templates") {
                val viewModel: WorkoutTemplateViewModel = viewModel(factory = WorkoutTemplateViewModel.Factory(repository))
                WorkoutTemplateListScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCreateTemplate = {
                        navController.navigate("session_wizard")
                    },
                    onEditTemplate = { template ->
                        navController.navigate("session_wizard/${template.id}")
                    }
                )
            }
            composable("session_wizard") {
                val viewModel: WorkoutTemplateViewModel = viewModel(factory = WorkoutTemplateViewModel.Factory(repository))
                SessionWizardScreen(
                    viewModel = viewModel,
                    templateId = null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("session_wizard/{templateId}") { backStackEntry ->
                val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                val viewModel: WorkoutTemplateViewModel = viewModel(factory = WorkoutTemplateViewModel.Factory(repository))
                SessionWizardScreen(
                    viewModel = viewModel,
                    templateId = templateId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("session_history") {
                val viewModel: SessionHistoryViewModel = viewModel(factory = SessionHistoryViewModel.Factory(repository))
                SessionHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
