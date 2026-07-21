package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.*

@Composable
fun SubjectsScreen(vm: MainViewModel, nav: NavController) {
    val subjects by vm.repo.subjects.collectAsState(emptyList())
    val settings by vm.settings.collectAsState()
    Column {
        ScreenTitle("Materie", "Terzo, quarto e quinto anno") {
            YearPicker(settings.selectedYear, vm::setYear)
        }
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(subjects, key = { it.id }) { subject ->
                Card(
                    Modifier.fillMaxWidth().clickable { nav.navigate(Routes.subject(subject.id)) },
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(subject.name, style = MaterialTheme.typography.titleMedium)
                        Text(subject.description)
                        Text(
                            if (subject.category == "TECHNICAL") "Materia tecnica" else "Materia generale",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearPicker(year: Int, onSelect: (Int) -> Unit) {
    Row {
        (3..5).forEach {
            FilterChip(selected = year == it, onClick = { onSelect(it) }, label = { Text("$it°") })
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun SubjectDetailScreen(vm: MainViewModel, id: String, nav: NavController) {
    val subject by vm.repo.subject(id).collectAsState(null)
    val settings by vm.settings.collectAsState()
    val lessons by vm.repo.lessons(id, settings.selectedYear).collectAsState(emptyList())
    Column {
        ScreenTitle(subject?.name ?: "Materia", "${lessons.size} lezioni • anno ${settings.selectedYear}")
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(lessons, key = { it.id }) { lesson ->
                Card(
                    Modifier.fillMaxWidth().clickable { nav.navigate(Routes.lesson(lesson.id)) },
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                        Text("${lesson.macroarea} › ${lesson.module}", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(onClick = {}, label = { Text("${lesson.durationMinutes} min") })
                            AssistChip(onClick = {}, label = { Text(lesson.status.replace('_', ' ')) })
                        }
                    }
                }
            }
        }
    }
}
