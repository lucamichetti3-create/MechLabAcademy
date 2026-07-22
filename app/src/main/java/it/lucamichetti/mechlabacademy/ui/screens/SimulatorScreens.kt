package it.lucamichetti.mechlabacademy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle
import java.util.Locale

@Composable
fun SimulatorsScreen(nav: NavController) {
    var force by remember { mutableFloatStateOf(200f) }
    var arm by remember { mutableFloatStateOf(0.30f) }
    val moment = force * arm

    LazyColumn(
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Simulatori interattivi", "Modifica i dati e osserva subito l’effetto fisico") }
        item {
            Card(Modifier.padding(horizontal = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Momento di una forza", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Sposta forza e braccio: M = F · b")
                    Spacer(Modifier.height(12.dp))
                    Text("Forza F = ${force.toInt()} N", fontWeight = FontWeight.Bold)
                    Slider(
                        value = force,
                        onValueChange = { force = it },
                        valueRange = 10f..500f,
                        steps = 48,
                    )
                    Text("Braccio b = ${formatDecimal(arm)} m", fontWeight = FontWeight.Bold)
                    Slider(
                        value = arm,
                        onValueChange = { arm = it },
                        valueRange = 0.05f..1f,
                        steps = 18,
                    )
                    Text(
                        "M = ${force.toInt()} · ${formatDecimal(arm)} = ${formatDecimal(moment)} N·m",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(10.dp))
                    LeverCanvas(force = force, arm = arm)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        when {
                            moment < 40f -> "Momento contenuto: aumenta la forza oppure allunga il braccio."
                            moment < 150f -> "Il braccio sta amplificando l’effetto rotatorio della forza."
                            else -> "Momento elevato: nelle applicazioni reali vanno verificati resistenza, serraggio e sicurezza."
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { nav.navigate(Routes.lesson("lesson_mechanics_004")) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        androidx.compose.material3.Icon(Icons.Default.OpenInNew, contentDescription = null)
                        Text(" Apri teoria, video ed esercizi")
                    }
                }
            }
        }
        item {
            Card(Modifier.padding(horizontal = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Laboratorio digitale in espansione", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Il motore è predisposto per trave, attrito, parametri di taglio, pneumatica e sequenze PLC. Le attività guidate restano disponibili nella sezione Laboratorio.")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { nav.navigate(Routes.LABS) }, modifier = Modifier.weight(1f)) { Text("Laboratori") }
                        Button(onClick = { nav.navigate(Routes.TOOLS) }, modifier = Modifier.weight(1f)) { Text("Calcolatori") }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeverCanvas(force: Float, arm: Float) {
    Canvas(Modifier.fillMaxWidth().aspectRatio(16f / 8f)) {
        val pivot = Offset(size.width * 0.78f, size.height * 0.70f)
        val startX = pivot.x - (arm / 1f).coerceIn(0.05f, 1f) * size.width * 0.62f
        val leverStart = Offset(startX, pivot.y - size.height * 0.12f)
        drawLine(
            color = androidx.compose.ui.graphics.Color(0xFF90A4AE),
            start = leverStart,
            end = pivot,
            strokeWidth = size.minDimension * 0.055f,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = androidx.compose.ui.graphics.Color(0xFFFFA000),
            radius = size.minDimension * 0.055f,
            center = pivot,
        )
        val arrowEnd = Offset(startX, leverStart.y - 4f)
        val arrowStart = Offset(startX, size.height * (0.55f - 0.35f * (force / 500f)))
        drawLine(
            color = androidx.compose.ui.graphics.Color(0xFF42A5F5),
            start = arrowStart,
            end = arrowEnd,
            strokeWidth = size.minDimension * 0.025f,
            cap = StrokeCap.Round,
        )
        val head = size.minDimension * 0.035f
        drawLine(
            color = androidx.compose.ui.graphics.Color(0xFF42A5F5),
            start = arrowEnd,
            end = Offset(arrowEnd.x - head, arrowEnd.y - head),
            strokeWidth = size.minDimension * 0.018f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = androidx.compose.ui.graphics.Color(0xFF42A5F5),
            start = arrowEnd,
            end = Offset(arrowEnd.x + head, arrowEnd.y - head),
            strokeWidth = size.minDimension * 0.018f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = androidx.compose.ui.graphics.Color(0xFFEF5350),
            start = Offset(startX, pivot.y + size.height * 0.10f),
            end = Offset(pivot.x, pivot.y + size.height * 0.10f),
            strokeWidth = size.minDimension * 0.010f,
        )
    }
}

private fun formatDecimal(value: Float): String = String.format(Locale.ITALY, "%.2f", value)
