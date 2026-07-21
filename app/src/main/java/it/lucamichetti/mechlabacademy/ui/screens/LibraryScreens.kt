package it.lucamichetti.mechlabacademy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.EmptyState
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle

@Composable
fun SearchScreen(vm: MainViewModel, nav: NavController) {
    var query by remember { mutableStateOf("") }
    val results by vm.repo.search(query).collectAsState(emptyList())

    Column {
        ScreenTitle(
            title = "Ricerca",
            subtitle = "Lezioni, formule e parole chiave del catalogo offline",
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("Cerca") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )

        if (query.isNotBlank() && results.isEmpty()) {
            EmptyState("Nessun risultato", "Prova con un termine più generale")
        } else {
            LazyColumn {
                items(results, key = { it.id }) { lesson ->
                    ListItem(
                        headlineContent = { Text(lesson.title) },
                        supportingContent = { Text("${lesson.macroarea} • ${lesson.module}") },
                        modifier = Modifier.clickable { nav.navigate(Routes.lesson(lesson.id)) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun VideosScreen(vm: MainViewModel) {
    val videos by vm.repo.videos.collectAsState(emptyList())
    val context = LocalContext.current

    Column {
        ScreenTitle(
            title = "Videolezioni",
            subtitle = "Link controllati singolarmente; i contenuti testuali restano offline",
        )
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(videos, key = { it.id }) { video ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(video.title, style = MaterialTheme.typography.titleMedium)
                        Text("${video.author} • ${video.language} • ${video.duration}")
                        Spacer(Modifier.height(4.dp))
                        Text(video.reason)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(video.url)),
                                        )
                                    }
                                    vm.toggleVideo(video, watched = true)
                                },
                            ) {
                                Text("Apri")
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = { vm.toggleVideo(video, favorite = !video.favorite) },
                            ) {
                                Icon(
                                    imageVector = if (video.favorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Preferito",
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Senza connessione: Video disponibile con connessione Internet",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("Italiano, inglese o definizione") },
            singleLine = true,
        )
        LazyColumn {
            items(entries, key = { it.id }) { entry ->
                ListItem(
                    headlineContent = { Text(entry.italianTerm) },
                    overlineContent = { Text(entry.englishTerm) },
                    supportingContent = {
                        Column {
                            Text(entry.definition)
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
fun FlashcardsScreen(vm: MainViewModel) {
    val cards by vm.repo.flashcards.collectAsState(emptyList())
    var index by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        ScreenTitle("Flashcard", "Ripetizione dilazionata locale e trasparente")
        if (cards.isEmpty()) {
            EmptyState("Nessuna flashcard", "Il catalogo non è ancora disponibile")
            return@Column
        }

        val card = cards[index % cards.size]
        Text("Carta ${index % cards.size + 1} di ${cards.size}")
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clickable { flipped = !flipped },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (flipped) card.back else card.front,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Tocca la carta per girarla")

        if (flipped) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1 to "Difficile", 3 to "Bene", 5 to "Facile").forEach { (quality, label) ->
                    Button(
                        onClick = {
                            vm.reviewCard(card.id, quality)
                            index++
                            flipped = false
                        },
                    ) {
                        Text(label)
                    }
                }
            }
        }
    }
}
