package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.data.local.QuizQuestionEntity
import it.lucamichetti.mechlabacademy.ui.*
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
                Text("Il piano dimostrativo alterna materie tecniche e generali. Ore settimanali e anno si regolano dal Profilo.")
            }
        }
    }
}

@Composable
fun ExercisesScreen(vm: MainViewModel, nav: NavController) {
    val exercises by vm.repo.exercises.collectAsState(emptyList())
    val progress by vm.repo.exerciseProgress.collectAsState(emptyList())
    Column {
        ScreenTitle("Esercizi", "Numerici, tavole, automazione, inglese tecnico e materie umanistiche")
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(exercises, key = { it.id }) { exercise ->
                var expanded by remember(exercise.id) { mutableStateOf(false) }
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(exercise.title, style = MaterialTheme.typography.titleMedium)
                        Text(exercise.prompt)
                        Text("${exercise.category} • ${exercise.difficulty}", color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "Nascondi soluzione" else "Mostra soluzione")
                        }
                        if (expanded) {
                            Text(exercise.solution)
                            Button(onClick = { vm.completeExercise(exercise.id, "Consultata e svolta") }) {
                                Text(if (progress.any { it.exerciseId == exercise.id && it.completed }) "Completato" else "Segna completato")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(vm: MainViewModel) {
    var questions by remember { mutableStateOf<List<QuizQuestionEntity>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { questions = vm.repo.quiz(limit = 20) }

    Column(Modifier.padding(16.dp)) {
        ScreenTitle("Quiz adattivo", "20 domande dal dataset locale")
        if (questions.isEmpty()) {
            Loading("Preparazione quiz")
            return@Column
        }
        val question = questions[index]
        Text("${index + 1}/${questions.size}")
        LinearProgressIndicator(progress = { (index + 1) / questions.size.toFloat() }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Text(question.prompt, style = MaterialTheme.typography.titleLarge)
        parseOptions(question.optionsJson).forEach { option ->
            Row {
                RadioButton(selected = selected == option, onClick = { selected = option })
                Text(option, Modifier.padding(top = 12.dp))
            }
        }
        if (checked) {
            val isCorrect = selected.equals(question.correctAnswer, ignoreCase = true)
            Text(
                if (isCorrect) "Corretto" else "Risposta corretta: ${question.correctAnswer}",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Text(question.explanation)
        }
        Button(
            onClick = {
                if (!checked) {
                    checked = true
                    vm.answer(question, selected, 0)
                } else {
                    index = (index + 1) % questions.size
                    selected = ""
                    checked = false
                }
            },
            enabled = selected.isNotBlank(),
        ) {
            Text(if (checked) "Prossima" else "Verifica")
        }
    }
}

private fun parseOptions(raw: String): List<String> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { it.jsonPrimitive.content }
}.getOrDefault(emptyList())
