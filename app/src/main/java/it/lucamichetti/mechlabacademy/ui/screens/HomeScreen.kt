package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.MetricCard
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.formatMinutes

@Composable
fun HomeScreen(vm: MainViewModel, nav: NavController) {
    val h by vm.home.collectAsState()
    val s by vm.settings.collectAsState()
    val session by vm.studySession.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 155.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text("MechLab Academy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
        item { MetricCard("Video visti", "${h.videosWatched}/${h.videoTotal}", Modifier.fillMaxWidth()) }
        item { MetricCard("Flashcard da ripassare", "${h.dueFlashcards}", Modifier.fillMaxWidth()) }
        item { MetricCard("Esercizi completati", "${h.exercisesDone}", Modifier.fillMaxWidth()) }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Button(
                onClick = { nav.navigate(h.latestLessonId?.let(Routes::lesson) ?: Routes.SUBJECTS) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("Continua a studiare", Modifier.padding(start = 8.dp))
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            StudyTodayCard(
                lessonTitle = session?.lessonTitle,
                macroarea = session?.macroarea,
                onOpen = { nav.navigate(Routes.TODAY) },
            )
        }

        val shortcuts = listOf(
            "Video Academy" to Routes.VIDEOS,
            "Mappe" to Routes.MAPS,
            "Flashcard" to Routes.FLASHCARDS,
            "Laboratorio" to Routes.LABS,
            "Simulatori" to Routes.SIMULATORS,
            "Glossario" to Routes.GLOSSARY,
            "Appunti" to Routes.NOTES,
            "Strumenti" to Routes.TOOLS,
            "Ricerca globale" to Routes.SEARCH,
        )
        items(shortcuts) { (label, route) ->
            OutlinedButton(onClick = { nav.navigate(route) }, modifier = Modifier.fillMaxWidth()) {
                Text(label)
            }
        }
    }
}

@Composable
private fun StudyTodayCard(
    lessonTitle: String?,
    macroarea: String?,
    onOpen: () -> Unit,
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Studio di oggi — percorso 360°", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(6.dp))
            Text(lessonTitle ?: "Preparazione della prossima sessione")
            if (!macroarea.isNullOrBlank()) {
                Text(macroarea, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Text("Teoria → video → esercizi → flashcard → quiz → laboratorio")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AssistChip(onClick = {}, label = { Text("55 min") })
                AssistChip(onClick = {}, label = { Text("Adattiva") })
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
                Text("Avvia sessione")
            }
        }
    }
}
