package it.lucamichetti.mechlabacademy.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.ui.*
import kotlinx.serialization.json.*
import kotlin.math.hypot

data class MapNode(val id: String, val label: String, val x: Float, val y: Float, val lessonId: String)
data class MapEdge(val from: String, val to: String, val label: String)

@Composable
fun MapsScreen(vm: MainViewModel, nav: NavController) {
    val maps by vm.repo.maps.collectAsState(emptyList())
    Column {
        ScreenTitle("Mappe concettuali", "Dati interattivi: zoom, trascinamento e nodi")
        LazyColumn {
            items(maps, key = { it.id }) { map ->
                ListItem(
                    headlineContent = { Text(map.title) },
                    supportingContent = { Text(map.category) },
                    modifier = Modifier.clickable { nav.navigate(Routes.map(map.id)) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MapDetailScreen(vm: MainViewModel, id: String, nav: NavController) {
    val mapState by vm.repo.map(id).collectAsState(null)
    val map = mapState ?: return Loading("Mappa")
    val nodes = remember(map.nodesJson) { parseNodes(map.nodesJson) }
    val edges = remember(map.edgesJson) { parseEdges(map.edgesJson) }
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var selected by remember { mutableStateOf<MapNode?>(null) }

    Column {
        ScreenTitle(map.title, "${map.category} • pizzica per zoomare, trascina e tocca un nodo")
        BoxWithConstraints(
            Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp),
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()
            fun position(node: MapNode): Offset = Offset(
                widthPx / 2f + node.x * scale + pan.x,
                heightPx / 3f + node.y * scale + pan.y,
            )
            Canvas(
                Modifier.fillMaxSize()
                    .pointerInput(nodes, scale, pan) {
                        detectTapGestures { tap ->
                            selected = nodes.minByOrNull { node ->
                                val p = position(node)
                                hypot((p.x - tap.x).toDouble(), (p.y - tap.y).toDouble())
                            }?.takeIf { node ->
                                val p = position(node)
                                hypot((p.x - tap.x).toDouble(), (p.y - tap.y).toDouble()) <= 72.0 * scale
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, panChange, zoomChange, _ ->
                            pan += panChange
                            scale = (scale * zoomChange).coerceIn(0.45f, 3f)
                        }
                    },
            ) {
                edges.forEach { edge ->
                    val from = nodes.find { it.id == edge.from }
                    val to = nodes.find { it.id == edge.to }
                    if (from != null && to != null) {
                        drawLine(Color.Gray, position(from), position(to), strokeWidth = 3f)
                    }
                }
                val paint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textAlign = Paint.Align.CENTER
                    textSize = 12.dp.toPx() * scale.coerceAtMost(1.5f)
                    isAntiAlias = true
                }
                nodes.forEach { node ->
                    val point = position(node)
                    val radius = (if (node.id == "c") 66f else 56f) * scale
                    drawCircle(
                        color = if (node.id == "c") Color(0xFFFF9F1C) else Color(0xFF0B3A67),
                        radius = radius,
                        center = point,
                    )
                    drawCircle(Color.White, radius, point, style = Stroke(2f))
                    val label = if (node.label.length > 22) node.label.take(20) + "…" else node.label
                    drawContext.canvas.nativeCanvas.drawText(label, point.x, point.y + paint.textSize / 3f, paint)
                }
            }
        }
        selected?.let { node ->
            Card(Modifier.fillMaxWidth().padding(12.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(node.label, style = MaterialTheme.typography.titleMedium)
                    Text("Nodo collegato alla lezione ${node.lessonId}")
                    if (node.lessonId.isNotBlank()) {
                        Button(onClick = { nav.navigate(Routes.lesson(node.lessonId)) }) {
                            Text("Apri la lezione collegata")
                        }
                    }
                }
            }
        }
    }
}

private fun parseNodes(raw: String): List<MapNode> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { element ->
        val obj = element.jsonObject
        MapNode(
            id = obj["id"]!!.jsonPrimitive.content,
            label = obj["label"]!!.jsonPrimitive.content,
            x = obj["x"]!!.jsonPrimitive.float,
            y = obj["y"]!!.jsonPrimitive.float,
            lessonId = obj["lessonId"]?.jsonPrimitive?.content.orEmpty(),
        )
    }
}.getOrDefault(emptyList())

private fun parseEdges(raw: String): List<MapEdge> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { element ->
        val obj = element.jsonObject
        MapEdge(
            from = obj["from"]!!.jsonPrimitive.content,
            to = obj["to"]!!.jsonPrimitive.content,
            label = obj["label"]?.jsonPrimitive?.content.orEmpty(),
        )
    }
}.getOrDefault(emptyList())

@Composable
fun LabsScreen(vm: MainViewModel, nav: NavController) {
    val labs by vm.repo.labs.collectAsState(emptyList())
    Column {
        ScreenTitle("Laboratorio virtuale", "Preparazione guidata: non sostituisce il laboratorio reale")
        LazyColumn {
            items(labs, key = { it.id }) { lab ->
                ListItem(
                    headlineContent = { Text(lab.title) },
                    supportingContent = { Text(lab.objective) },
                    modifier = Modifier.clickable { nav.navigate(Routes.lab(lab.id)) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun LabDetailScreen(vm: MainViewModel, id: String) {
    val labState by vm.repo.lab(id).collectAsState(null)
    val lab = labState ?: return Loading("Laboratorio")
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(lab.title, style = MaterialTheme.typography.headlineMedium)
            Text("Obiettivo: ${lab.objective}")
        }
        item { LabSection("Strumenti", lab.tools) }
        item { LabSection("DPI", lab.dpi) }
        item { LabSection("Teoria", lab.theory) }
        item { LabSection("Procedura", lab.procedureJson) }
        item { LabSection("Ordine delle operazioni", lab.operationOrderJson) }
        item { LabSection("Controlli", lab.checksJson) }
        item { LabSection("Rischi", lab.risksJson) }
        item { LabSection("Errori", lab.errorsJson) }
        item { LabSection("Risultato atteso", lab.expectedResult) }
        item { LabSection("Domande", lab.questionsJson) }
        item { LabSection("Simulazione", lab.simulationJson) }
        item { LabSection("Relazione finale", lab.reportTemplate) }
    }
}

@Composable
private fun LabSection(title: String, value: String) {
    Card {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value.replace("[", " ").replace("]", " ").replace("\"", ""))
        }
    }
}
