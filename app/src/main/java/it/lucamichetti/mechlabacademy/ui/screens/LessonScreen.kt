package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.data.repository.LessonResourceCounts
import it.lucamichetti.mechlabacademy.ui.Loading
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

@Composable
fun LessonScreen(vm: MainViewModel, id: String, nav: NavController) {
    val lessonState by vm.repo.lesson(id).collectAsState(null)
    val progress by vm.repo.progress(id).collectAsState(null)
    val resources by vm.repo.resourceCounts(id).collectAsState(initial = LessonResourceCounts())
    LaunchedEffect(id) { vm.openLesson(id) }
    DisposableEffect(id) {
        val openedAt = System.currentTimeMillis()
        onDispose {
            vm.addStudySeconds(id, ((System.currentTimeMillis() - openedAt) / 1_000L).coerceAtLeast(0L))
        }
    }

    val lesson = lessonState ?: return Loading("Apertura lezione")
    var notes by remember(progress?.personalNotes) { mutableStateOf(progress?.personalNotes.orEmpty()) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(lesson.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("${lesson.year}° anno • ${lesson.macroarea} • ${lesson.durationMinutes} min")
            Text(
                lesson.status.replace('_', ' '),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = progress?.completed == true,
                    onClick = { vm.complete(id, progress?.completed != true) },
                    label = { Text("Completata") },
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                )
                FilterChip(
                    selected = progress?.favorite == true,
                    onClick = { vm.favorite(id, progress?.favorite != true) },
                    label = { Text("Preferita") },
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                )
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Percorso interattivo della lezione", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Leggi, guarda, applica, richiama e verifica.")
                    Spacer(Modifier.height(10.dp))
                    ResourceButton("Videolezioni (${resources.videos})", Icons.Default.PlayCircle) {
                        nav.navigate(Routes.lessonVideos(id))
                    }
                    ResourceButton("Esercizi (${resources.exercises})", Icons.Default.Straighten) {
                        nav.navigate(Routes.lessonExercises(id))
                    }
                    ResourceButton("Flashcard (${resources.flashcards})", Icons.Default.Style) {
                        nav.navigate(Routes.lessonFlashcards(id))
                    }
                    ResourceButton("Quiz della lezione", Icons.Default.Quiz) {
                        nav.navigate(Routes.lessonQuiz(id))
                    }
                    ResourceButton("Simulatore interattivo", Icons.Default.Science) {
                        nav.navigate(Routes.SIMULATORS)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { nav.navigate(Routes.MAPS) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Map, contentDescription = null)
                            Text(" Mappe ${resources.maps}")
                        }
                        OutlinedButton(onClick = { nav.navigate(Routes.LABS) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Science, contentDescription = null)
                            Text(" Lab ${resources.labs}")
                        }
                    }
                }
            }
        }
        item { LessonSection("Prerequisiti", jsonList(lesson.prerequisitesJson)) }
        item { LessonSection("Obiettivi", jsonList(lesson.objectivesJson)) }
        item { LessonSection("Introduzione", listOf(lesson.introduction)) }
        item { LessonSection("Spiegazione", listOf(lesson.explanation)) }
        item { LessonSection("Formule", jsonList(lesson.formulasJson)) }
        item { LessonSection("Simboli e unità", jsonList(lesson.symbolsJson)) }
        item { LessonSection("Dimostrazioni intuitive", jsonList(lesson.intuitiveDemoJson)) }
        item { LessonSection("Esempi numerici", jsonList(lesson.numericExamplesJson)) }
        item { LessonSection("Applicazioni industriali", jsonList(lesson.industrialExamplesJson)) }
        item { LessonSection("Esempi pratici", jsonList(lesson.practicalExamplesJson)) }
        item { LessonSection("Collegamenti", jsonList(lesson.crossLinksJson)) }
        item { LessonSection("Errori frequenti", jsonList(lesson.commonErrorsJson)) }
        item { LessonSection("Riepilogo", listOf(lesson.summary)) }
        item { LessonSection("Autoverifica", jsonList(lesson.selfCheckJson)) }
        item { LessonSection("Esercizi guidati", jsonList(lesson.guidedExercisesJson)) }
        item { LessonSection("Esercizi autonomi", jsonList(lesson.autonomousExercisesJson)) }
        item { LessonSection("Soluzioni", jsonList(lesson.solutionsJson)) }
        item { LessonSection("Approfondimenti", jsonList(lesson.approfondimentiJson)) }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = null)
                        Text("Quaderno personale", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        label = { Text("Appunti personali") },
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.saveLessonNotes(id, notes) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Salva appunti")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null)
        Text(" $label")
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun LessonSection(title: String, values: List<String>) {
    val visible = values.filter { it.isNotBlank() }
    if (visible.isEmpty()) return
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            visible.forEach {
                Text("• $it")
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

private fun jsonList(raw: String): List<String> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { element ->
        if (element is JsonPrimitive) element.content else element.toString()
    }
}.getOrElse { listOf(raw) }
