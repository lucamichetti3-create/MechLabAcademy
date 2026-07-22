package it.lucamichetti.mechlabacademy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import it.lucamichetti.mechlabacademy.BuildConfig
import it.lucamichetti.mechlabacademy.data.local.TechnicalToolEntity
import it.lucamichetti.mechlabacademy.domain.TechnicalCalculators
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(vm: MainViewModel) {
    val notes by vm.repo.notes.collectAsState(emptyList())
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    Column {
        ScreenTitle(
            title = "Appunti e quaderni",
            subtitle = "Include il quaderno “Domande da fare al professore”",
        )
        Card(Modifier.padding(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Titolo") },
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Appunto o dubbio") },
                    minLines = 3,
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        vm.saveNote(null, title, body)
                        title = ""
                        body = ""
                    },
                    enabled = title.isNotBlank(),
                ) {
                    Text("Salva")
                }
            }
        }
        LazyColumn {
            items(notes, key = { it.id }) { note ->
                ListItem(
                    headlineContent = { Text(note.title) },
                    supportingContent = { Text(note.body) },
                    trailingContent = {
                        TextButton(onClick = { vm.deleteNote(note) }) { Text("Elimina") }
                    },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ToolsScreen(vm: MainViewModel) {
    val tools by vm.repo.tools.collectAsState(emptyList())
    var selected by remember { mutableStateOf<TechnicalToolEntity?>(null) }
    var inputA by remember { mutableStateOf("") }
    var inputB by remember { mutableStateOf("") }
    var inputC by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    LaunchedEffect(tools) {
        if (selected == null) selected = tools.firstOrNull()
    }

    Column {
        ScreenTitle("Strumenti tecnici", "Calcolatori, formule e formulari")
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                selected?.let { tool ->
                    Card {
                        Column(Modifier.padding(14.dp)) {
                            Text(tool.name, style = MaterialTheme.typography.titleLarge)
                            Text(tool.formula, style = MaterialTheme.typography.titleMedium)
                            Text(tool.symbols, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = inputA,
                                onValueChange = { inputA = it },
                                label = { Text("Valore A") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = inputB,
                                onValueChange = { inputB = it },
                                label = { Text("Valore B") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = inputC,
                                onValueChange = { inputC = it },
                                label = { Text("Valore C, se richiesto") },
                                singleLine = true,
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    result = runCatching {
                                        val calculation = TechnicalCalculators.calculate(
                                            key = tool.key,
                                            a = inputA.toDouble(),
                                            b = inputB.toDoubleOrNull() ?: 0.0,
                                            c = inputC.toDoubleOrNull() ?: 0.0,
                                        )
                                        val valueText = if (calculation.value.isNaN()) {
                                            calculation.explanation
                                        } else {
                                            "${calculation.value} ${calculation.unit}\n${calculation.explanation}"
                                        }
                                        vm.saveCalculation(
                                            key = tool.key,
                                            inputs = "$inputA,$inputB,$inputC",
                                            result = calculation.value.toString(),
                                            unit = calculation.unit,
                                        )
                                        valueText
                                    }.getOrElse { error -> "Errore: ${error.message}" }
                                },
                                enabled = inputA.isNotBlank(),
                            ) {
                                Text("Calcola")
                            }
                            if (result.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(result, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(tool.explanation)
                            Text(
                                tool.professionalDisclaimer,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            item {
                Text("Catalogo strumenti", style = MaterialTheme.typography.titleMedium)
            }
            items(tools, key = { it.id }) { tool ->
                Card(
                    onClick = {
                        selected = tool
                        inputA = ""
                        inputB = ""
                        inputC = ""
                        result = ""
                    },
                ) {
                    ListItem(
                        headlineContent = { Text(tool.name) },
                        supportingContent = { Text(tool.formula) },
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(vm: MainViewModel, nav: NavController) {
    val settings by vm.settings.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var backupStatus by remember { mutableStateOf("") }

    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        vm.setReminder(granted)
    }

    val exportBackup = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                backupStatus = vm.backupManager.export(it).fold(
                    onSuccess = { "Backup esportato" },
                    onFailure = { error -> "Errore: ${error.message}" },
                )
            }
        }
    }

    val importBackup = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                backupStatus = vm.backupManager.import(it).fold(
                    onSuccess = { "Backup importato" },
                    onFailure = { error -> "Errore: ${error.message}" },
                )
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Profilo di Luca", "Percorso personale di preparazione al diploma") }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("MechLab Academy ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.titleLarge)
                    Text("Ecosistema 360°: app Android offline, Video Academy, sessioni adattive e portale web complementare.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { nav.navigate(Routes.TODAY) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Avvia Studio di oggi")
                    }
                }
            }
        }
        item {
            Text("Anno selezionato")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (3..5).forEach { year ->
                    FilterChip(
                        selected = settings.selectedYear == year,
                        onClick = { vm.setYear(year) },
                        label = { Text("$year°") },
                    )
                }
            }
        }
        item {
            Text("Ore di studio settimanali: ${settings.weeklyHours}")
            Slider(
                value = settings.weeklyHours.toFloat(),
                onValueChange = { vm.setWeeklyHours(it.toInt()) },
                valueRange = 1f..30f,
                steps = 28,
            )
        }
        item {
            Text("Dimensione testo")
            Slider(
                value = settings.textScale,
                onValueChange = vm::setTextScale,
                valueRange = 0.85f..1.35f,
            )
        }
        item {
            Text("Tema")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("SYSTEM", "LIGHT", "DARK").forEach { theme ->
                    FilterChip(
                        selected = settings.theme == theme,
                        onClick = { vm.setTheme(theme) },
                        label = { Text(theme) },
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Promemoria giornaliero")
                    Text("Gestito localmente con WorkManager", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = settings.reminderEnabled,
                    onCheckedChange = { enabled ->
                        val needsPermission = enabled &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) != PackageManager.PERMISSION_GRANTED
                        if (needsPermission) {
                            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            vm.setReminder(enabled)
                        }
                    },
                )
            }
        }
        item {
            Button(onClick = { exportBackup.launch("mechlab-backup.json") }) {
                Text("Esporta backup")
            }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(
                onClick = { importBackup.launch(arrayOf("application/json", "text/plain")) },
            ) {
                Text("Importa backup")
            }
            if (backupStatus.isNotBlank()) Text(backupStatus)
        }
        item {
            HorizontalDivider()
            Text("Risorse", style = MaterialTheme.typography.titleMedium)
            val resources = listOf(
                "Ricerca" to Routes.SEARCH,
                "Video Academy" to Routes.VIDEOS,
                "Studio di oggi" to Routes.TODAY,
                "Mappe" to Routes.MAPS,
                "Flashcard" to Routes.FLASHCARDS,
                "Laboratorio" to Routes.LABS,
                "Simulatori" to Routes.SIMULATORS,
                "Glossario" to Routes.GLOSSARY,
                "Appunti" to Routes.NOTES,
                "Strumenti" to Routes.TOOLS,
            )
            resources.forEach { (label, route) ->
                TextButton(onClick = { nav.navigate(route) }) { Text(label) }
            }
        }
        item {
            Text(
                text = "Uso personale e didattico. L’app non sostituisce scuola, laboratorio, docenti, diploma, norme tecniche o verifiche professionali.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
