package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.EmptyState
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle

private data class StudyStep(
    val title: String,
    val subtitle: String,
    val minutes: Int,
    val destination: String,
)

@Composable
fun StudyTodayScreen(vm: MainViewModel, nav: NavController) {
    val session by vm.studySession.collectAsState()
    val current = session
    if (current == null) {
        EmptyState("Sessione non disponibile", "Apri una materia per iniziare il percorso")
        return
    }

    val steps = remember(current.lessonId, current.videoId) {
        listOf(
            StudyStep("Teoria", current.lessonTitle, 20, Routes.lesson(current.lessonId)),
            StudyStep(
                "Videolezione",
                if (current.videoId != null) "Video collegato alla lezione" else "Catalogo Video Academy",
                10,
                current.videoId?.let(Routes::video) ?: Routes.VIDEOS,
            ),
            StudyStep(
                "Esercizi",
                "${current.resourceCounts.exercises} attività collegate",
                15,
                Routes.lessonExercises(current.lessonId),
            ),
            StudyStep(
                "Flashcard",
                "${current.resourceCounts.flashcards} carte collegate",
                5,
                Routes.lessonFlashcards(current.lessonId),
            ),
            StudyStep(
                "Quiz finale",
                "${current.quizCount} domande disponibili",
                5,
                Routes.lessonQuiz(current.lessonId),
            ),
        )
    }
    val completed = remember(current.lessonId) { mutableStateListOf<Boolean>().apply { repeat(steps.size) { add(false) } } }
    val completedCount = completed.count { it }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ScreenTitle("Studio di oggi", "${current.macroarea} • ${steps.sumOf { it.minutes }} minuti")
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(current.lessonTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { completedCount / steps.size.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("$completedCount/${steps.size} tappe completate")
            }
        }
        steps.forEachIndexed { index, step ->
            item(key = step.title) {
                Card(Modifier.padding(horizontal = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                if (completed[index]) Icons.Default.CheckCircle else Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Column(Modifier.weight(1f)) {
                                Text(step.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(step.subtitle)
                                Text("${step.minutes} min", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    completed[index] = true
                                    nav.navigate(step.destination)
                                },
                                modifier = Modifier.weight(1f),
                            ) { Text("Apri") }
                            OutlinedButton(
                                onClick = { completed[index] = !completed[index] },
                            ) { Text(if (completed[index]) "Annulla" else "Fatto") }
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                if (completedCount == steps.size) {
                    Button(
                        onClick = { vm.complete(current.lessonId, true) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Completa la lezione e chiudi la sessione") }
                } else {
                    Text(
                        "La sessione alterna comprensione, visione, applicazione e richiamo attivo. Puoi aprire le singole tappe nell’ordine più utile per la sessione.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
