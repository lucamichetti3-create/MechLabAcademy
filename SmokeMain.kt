package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.*

@Composable
fun HomeScreen(vm: MainViewModel, nav: NavController) {
    val h by vm.home.collectAsState()
    val s by vm.settings.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Text("MechLab Academy", style = MaterialTheme.typography.headlineMedium)
                Text("Studia con metodo. Ragiona da tecnico. Costruisci il futuro.")
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(progress = { h.percent / 100f }, modifier = Modifier.fillMaxWidth())
                Text("${h.percent}% del percorso • anno ${s.selectedYear}")
            }
        }
        item { MetricCard("Lezioni completate", "${h.completed}/${h.total}", Modifier.fillMaxWidth()) }
        item { MetricCard("Tempo studiato", formatMinutes(h.studiedSeconds), Modifier.fillMaxWidth()) }
        item { MetricCard("Serie attuale", "${h.streak} giorni", Modifier.fillMaxWidth()) }
        item { MetricCard("Obiettivo", "${s.weeklyHours} h/settimana", Modifier.fillMaxWidth()) }
        item(span = { GridItemSpan(2) }) {
            Button(
                onClick = { nav.navigate(h.latestLessonId?.let(Routes::lesson) ?: Routes.SUBJECTS) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Text("Continua a studiare")
            }
        }
        item(span = { GridItemSpan(2) }) { StudyTodayCard(nav) }
        val shortcuts = listOf(
            "Video" to Routes.VIDEOS,
            "Mappe" to Routes.MAPS,
            "Flashcard" to Routes.FLASHCARDS,
            "Laboratorio" to Routes.LABS,
            "Glossario" to Routes.GLOSSARY,
            "Appunti" to Routes.NOTES,
            "Strumenti" to Routes.TOOLS,
            "Ricerca" to Routes.SEARCH,
        )
        items(shortcuts) { (label, route) ->
            OutlinedButton(onClick = { nav.navigate(route) }, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }
    }
}

@Composable
private fun StudyTodayCard(nav: NavController) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text("Studio di oggi", style = MaterialTheme.typography.titleLarge)
            Text("20 min teoria • 15 min esercizi • 10 min flashcard • 10 min video • quiz finale")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { nav.navigate(Routes.SUBJECTS) }, label = { Text("Teoria") })
                AssistChip(onClick = { nav.navigate(Routes.EXERCISES) }, label = { Text("Esercizi") })
                AssistChip(onClick = { nav.navigate(Routes.QUIZ) }, label = { Text("Quiz") })
            }
        }
    }
}
