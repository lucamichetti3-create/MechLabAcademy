package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.lucamichetti.mechlabacademy.ui.Loading
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

@Composable
fun LessonScreen(vm: MainViewModel, id: String) {
    val lessonState by vm.repo.lesson(id).collectAsState(null)
    val progress by vm.repo.progress(id).collectAsState(null)
    LaunchedEffect(id) { vm.openLesson(id) }
    val lesson = lessonState ?: return Loading("Apertura lezione")
    var notes by remember(progress?.personalNotes) { mutableStateOf(progress?.personalNotes.orEmpty()) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(lesson.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("${lesson.year}° anno • ${lesson.macroarea} • ${lesson.durationMinutes} min")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = progress?.completed == true,
                    onClick = { vm.complete(id, progress?.completed != true) },
                    label = { Text("Completata") },
                    leadingIcon = { Icon(Icons.Default.Check, null) },
                )
                FilterChip(
                    selected = progress?.favorite == true,
                    onClick = { vm.favorite(id, progress?.favorite != true) },
                    label = { Text("Preferita") },
                    leadingIcon = { Icon(Icons.Default.Star, null) },
                )
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
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                label = { Text("Appunti personali") },
            )
            Button(onClick = { vm.saveLessonNotes(id, notes) }) { Text("Salva appunti") }
        }
    }
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
