package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.data.repository.GlobalSearchItem
import it.lucamichetti.mechlabacademy.ui.EmptyState
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(vm: MainViewModel, nav: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<GlobalSearchItem>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        if (query.trim().length < 2) {
            results = emptyList()
            searching = false
        } else {
            searching = true
            delay(250)
            results = vm.repo.globalSearch(query)
            searching = false
        }
    }

    Column {
        ScreenTitle(
            title = "Ricerca globale",
            subtitle = "Lezioni, video, esercizi, laboratori, strumenti e glossario",
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            label = { Text("Cerca in tutta l'Academy") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )

        if (searching) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        when {
            query.length < 2 -> EmptyState("Ricerca a 360°", "Scrivi almeno due caratteri")
            !searching && results.isEmpty() -> EmptyState("Nessun risultato", "Prova con un termine più generale")
            else -> LazyColumn {
                items(results, key = { "${it.type}:${it.id}" }) { item ->
                    ListItem(
                        leadingContent = { Icon(searchIcon(item.type), contentDescription = null) },
                        headlineContent = { Text(item.title) },
                        overlineContent = { Text(searchTypeLabel(item.type)) },
                        supportingContent = { Text(item.subtitle, maxLines = 2) },
                        modifier = Modifier.clickable { openSearchResult(nav, item) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun openSearchResult(nav: NavController, item: GlobalSearchItem) {
    val route = when (item.type) {
        "LESSON" -> Routes.lesson(item.targetId)
        "VIDEO" -> Routes.video(item.targetId)
        "EXERCISE" -> Routes.lessonExercises(item.targetId)
        "LAB" -> Routes.lab(item.targetId)
        "TOOL" -> Routes.TOOLS
        "GLOSSARY" -> Routes.GLOSSARY
        else -> Routes.SEARCH
    }
    nav.navigate(route)
}

private fun searchIcon(type: String): ImageVector = when (type) {
    "LESSON" -> Icons.Default.MenuBook
    "VIDEO" -> Icons.Default.PlayCircle
    "LAB" -> Icons.Default.Science
    "TOOL" -> Icons.Default.Build
    "EXERCISE" -> Icons.Default.Straighten
    else -> Icons.Default.Search
}

private fun searchTypeLabel(type: String): String = when (type) {
    "LESSON" -> "LEZIONE"
    "VIDEO" -> "VIDEO"
    "EXERCISE" -> "ESERCIZIO"
    "LAB" -> "LABORATORIO"
    "TOOL" -> "STRUMENTO"
    "GLOSSARY" -> "GLOSSARIO"
    else -> type
}

@Composable
fun GlossaryScreen(vm: MainViewModel) {
    var query by remember { mutableStateOf("") }
    val entries by vm.repo.glossary(query).collectAsState(emptyList())

    Column {
        ScreenTitle("Glossario bilingue", "2.500 voci italiane e inglesi")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            label = { Text("Italiano, inglese o definizione") },
            singleLine = true,
        )
        LazyColumn {
            items(entries, key = { it.id }) { entry ->
                ListItem(
                    headlineContent = { Text(entry.italianTerm, fontWeight = FontWeight.Bold) },
                    overlineContent = { Text(entry.englishTerm) },
                    supportingContent = {
                        Column {
                            Text(entry.definition)
                            if (entry.practicalUse.isNotBlank()) {
                                Text("Uso: ${entry.practicalUse}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (entry.example.isNotBlank()) {
                                Text("Esempio: ${entry.example}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun FlashcardsScreen(vm: MainViewModel, lessonId: String = "") {
    val cards by vm.repo.flashcardsForLesson(lessonId).collectAsState(emptyList())
    var index by remember(lessonId) { mutableIntStateOf(0) }
    var flipped by remember(lessonId) { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        ScreenTitle(
            "Flashcard",
            if (lessonId.isBlank()) "Ripetizione dilazionata locale" else "Ripasso collegato alla lezione",
        )
        if (cards.isEmpty()) {
            EmptyState("Nessuna flashcard", "Per questa selezione non ci sono ancora carte")
            return@Column
        }

        val safeIndex = index.mod(cards.size)
        val card = cards[safeIndex]
        Text("Carta ${safeIndex + 1} di ${cards.size}")
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .clickable { flipped = !flipped },
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (flipped) card.back else card.front,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    if (flipped && card.formula.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text(card.formula, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    if (flipped && card.englishTerm.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text("EN: ${card.englishTerm}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Tocca la carta per girarla")

        if (flipped) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(1 to "Difficile", 3 to "Bene", 5 to "Facile").forEach { (quality, label) ->
                    Button(
                        onClick = {
                            vm.reviewCard(card.id, quality)
                            index++
                            flipped = false
                        },
                        modifier = Modifier.weight(1f),
                    ) { Text(label) }
                }
            }
        }
    }
}
