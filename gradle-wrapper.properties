package it.lucamichetti.mechlabacademy.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.lucamichetti.mechlabacademy.ui.screens.ExercisesScreen
import it.lucamichetti.mechlabacademy.ui.screens.FlashcardsScreen
import it.lucamichetti.mechlabacademy.ui.screens.GlossaryScreen
import it.lucamichetti.mechlabacademy.ui.screens.HomeScreen
import it.lucamichetti.mechlabacademy.ui.screens.LabDetailScreen
import it.lucamichetti.mechlabacademy.ui.screens.LabsScreen
import it.lucamichetti.mechlabacademy.ui.screens.LessonScreen
import it.lucamichetti.mechlabacademy.ui.screens.MapDetailScreen
import it.lucamichetti.mechlabacademy.ui.screens.MapsScreen
import it.lucamichetti.mechlabacademy.ui.screens.NotesScreen
import it.lucamichetti.mechlabacademy.ui.screens.PlanScreen
import it.lucamichetti.mechlabacademy.ui.screens.ProfileScreen
import it.lucamichetti.mechlabacademy.ui.screens.QuizScreen
import it.lucamichetti.mechlabacademy.ui.screens.SearchScreen
import it.lucamichetti.mechlabacademy.ui.screens.SubjectDetailScreen
import it.lucamichetti.mechlabacademy.ui.screens.SubjectsScreen
import it.lucamichetti.mechlabacademy.ui.screens.ToolsScreen
import it.lucamichetti.mechlabacademy.ui.screens.VideosScreen
import it.lucamichetti.mechlabacademy.ui.theme.MechLabTheme

data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MechLabApp(vm: MainViewModel = viewModel()) {
    val settings by vm.settings.collectAsState()
    val seed by vm.seed.collectAsState()

    MechLabTheme(settings.theme, settings.textScale) {
        when {
            seed.loading -> Loading(seed.stage)
            seed.error != null -> EmptyState("Importazione non riuscita", seed.error.orEmpty())
            else -> MainNavigation(vm)
        }
    }
}

@Composable
private fun MainNavigation(vm: MainViewModel) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomItem(Routes.HOME, "Home", Icons.Default.Home),
        BottomItem(Routes.SUBJECTS, "Materie", Icons.Default.MenuBook),
        BottomItem(Routes.PLAN, "Piano", Icons.Default.CalendarMonth),
        BottomItem(Routes.EXERCISES, "Esercizi", Icons.Default.EditNote),
        BottomItem(Routes.PROFILE, "Profilo", Icons.Default.Person),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val destination = navController.currentBackStackEntryAsState().value?.destination
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = destination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) { HomeScreen(vm, navController) }
            composable(Routes.SUBJECTS) { SubjectsScreen(vm, navController) }
            composable(Routes.PLAN) { PlanScreen(vm, navController) }
            composable(Routes.EXERCISES) { ExercisesScreen(vm, navController) }
            composable(Routes.PROFILE) { ProfileScreen(vm, navController) }
            composable(Routes.SEARCH) { SearchScreen(vm, navController) }
            composable(Routes.VIDEOS) { VideosScreen(vm) }
            composable(Routes.MAPS) { MapsScreen(vm, navController) }
            composable(Routes.FLASHCARDS) { FlashcardsScreen(vm) }
            composable(Routes.LABS) { LabsScreen(vm, navController) }
            composable(Routes.GLOSSARY) { GlossaryScreen(vm) }
            composable(Routes.NOTES) { NotesScreen(vm) }
            composable(Routes.TOOLS) { ToolsScreen(vm) }
            composable(Routes.QUIZ) { QuizScreen(vm) }
            composable("subject/{id}") { entry ->
                SubjectDetailScreen(vm, entry.arguments?.getString("id").orEmpty(), navController)
            }
            composable("lesson/{id}") { entry ->
                LessonScreen(vm, entry.arguments?.getString("id").orEmpty())
            }
            composable("map/{id}") { entry ->
                MapDetailScreen(vm, entry.arguments?.getString("id").orEmpty())
            }
            composable("lab/{id}") { entry ->
                LabDetailScreen(vm, entry.arguments?.getString("id").orEmpty())
            }
        }
    }
}
