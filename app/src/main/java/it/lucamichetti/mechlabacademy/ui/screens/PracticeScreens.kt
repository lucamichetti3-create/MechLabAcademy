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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.data.local.QuizQuestionEntity
import it.lucamichetti.mechlabacademy.ui.EmptyState
import it.lucamichetti.mechlabacademy.ui.Loading
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun PlanScreen(vm: MainViewModel, nav: NavController) {
    val plan by vm.repo.plan.collectAsState(emptyList())
    Column {
        ScreenTitle("Piano di studi", "Percorso completo • intensivo • libero • prerequisiti")
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(plan, key = { it.id }) { planItem ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(planItem.date, style = MaterialTheme.typography.labelLarge)
                        Text("${planItem.plannedMinutes} min • ${planItem.mode}")
                        Button(onClick = { nav.navigate(Routes.lesson(planItem.lessonId)) }) { Text("Apri lezione") }
                    }
                }
            }
            item {
                Text("Il piano dimostrativo alterna materie tecniche e generali. La modalità Studio di oggi costruisce una sessione completa sulla prossima lezione.")
            }
        }
    }
}

@Composable
fun ExercisesScreen(vm: MainViewModel, nav: NavController, lessonId: String = "") {
    val exercises by vm.repo.exercisesForLesson(lessonId).collectAsState(emptyList())
    val progress by vm.repo.exerciseProgress.collectAsState(emptyList())
    Column {
        ScreenTitle(
            "Esercizi",
            if (lessonId.isBlank()) "Numerici, tavole, automazione e inglese tecnico" else "Esercizi collegati alla lezione",
        )
        if (exercises.isEmpty()) {
            EmptyState("Nessun esercizio", "Per questa lezione gli esercizi sono da sviluppare")
            return@Column
        }
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(exercises, key = { it.id }) { exercise ->
                var expanded by remember(exercise.id) { mutableStateOf(false) }
                val completed = progress.any { it.exerciseId == exercise.id && it.completed }
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(exercise.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(exercise.prompt)
                        Spacer(Modifier.height(6.dp))
                        Text("${exercise.category} • ${exercise.difficulty} • ${exercise.score} punti", color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "Nascondi soluzione" else "Mostra soluzione guidata")
                        }
                        if (expanded) {
                            Text(exercise.solution)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.completeExercise(exercise.id, "Consultata e svolta") }) {
                                Text(if (completed) "Completato" else "Segna completato")
                            }
                            if (exercise.lessonId.isNotBlank()) {
                                TextButton(onClick = { nav.navigate(Routes.lesson(exercise.lessonId)) }) {
                                    Text("Ripassa la teoria")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(vm: MainViewModel, lessonId: String = "") {
    var questions by remember(lessonId) { mutableStateOf<List<QuizQuestionEntity>>(emptyList()) }
    var index by remember(lessonId) { mutableIntStateOf(0) }
    var selected by remember(lessonId) { mutableStateOf("") }
    var checked by remember(lessonId) { mutableStateOf(false) }
    var correctAnswers by remember(lessonId) { mutableIntStateOf(0) }

    LaunchedEffect(lessonId) {
        questions = vm.repo.quiz(lessonId = lessonId, limit = 20)
        index = 0
        selected = ""
        checked = false
        correctAnswers = 0
    }

    Column(Modifier.padding(16.dp)) {
        ScreenTitle(
            "Quiz adattivo",
            if (lessonId.isBlank()) "20 domande dal dataset locale" else "Verifica collegata alla lezione",
        )
        if (questions.isEmpty()) {
            Loading("Preparazione quiz")
            return@Column
        }
        val safeIndex = index.coerceIn(0, questions.lastIndex)
        val question = questions[safeIndex]
        Text("${safeIndex + 1}/${questions.size} • corrette: $correctAnswers")
        LinearProgressIndicator(
            progress = { (safeIndex + 1) / questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text(question.prompt, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        parseOptions(question.optionsJson).forEach { option ->
            Row {
                RadioButton(selected = selected == option, onClick = { if (!checked) selected = option })
                Text(option, Modifier.padding(top = 12.dp))
            }
        }
        if (checked) {
            val isCorrect = selected.equals(question.correctAnswer, ignoreCase = true)
            Text(
                if (isCorrect) "Corretto" else "Risposta corretta: ${question.correctAnswer}",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
            Text(question.explanation)
            if (question.typicalError.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("Errore tipico: ${question.typicalError}", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                if (!checked) {
                    val isCorrect = selected.equals(question.correctAnswer, ignoreCase = true)
                    if (isCorrect) correctAnswers++
                    checked = true
                    vm.answer(question, selected, 0)
                } else if (safeIndex < questions.lastIndex) {
                    index++
                    selected = ""
                    checked = false
                } else {
                    index = 0
                    selected = ""
                    checked = false
                    correctAnswers = 0
                }
            },
            enabled = selected.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (!checked) "Verifica" else if (safeIndex < questions.lastIndex) "Prossima" else "Ricomincia")
        }
    }
}

private fun parseOptions(raw: String): List<String> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { it.jsonPrimitive.content }
}.getOrDefault(emptyList())
