package it.lucamichetti.mechlabacademy.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import it.lucamichetti.mechlabacademy.data.local.VideoEntity
import it.lucamichetti.mechlabacademy.domain.VideoSourceType
import it.lucamichetti.mechlabacademy.domain.VideoSources
import it.lucamichetti.mechlabacademy.ui.EmptyState
import it.lucamichetti.mechlabacademy.ui.Loading
import it.lucamichetti.mechlabacademy.ui.MainViewModel
import it.lucamichetti.mechlabacademy.ui.Routes
import it.lucamichetti.mechlabacademy.ui.ScreenTitle

private enum class VideoFilter { ALL, ORIGINALS, FAVORITES, WATCHED }

@Composable
fun VideosScreen(vm: MainViewModel, nav: NavController, lessonId: String = "") {
    val videos by vm.repo.videosForLesson(lessonId).collectAsState(emptyList())
    var query by remember(lessonId) { mutableStateOf("") }
    var filter by remember(lessonId) { mutableStateOf(VideoFilter.ALL) }

    val filtered = remember(videos, query, filter) {
        videos.filter { video ->
            val matchesQuery = query.isBlank() || listOf(video.title, video.author, video.topic, video.description)
                .any { it.contains(query, ignoreCase = true) }
            val matchesFilter = when (filter) {
                VideoFilter.ALL -> true
                VideoFilter.ORIGINALS -> video.platform == "MECHLAB_LOCAL"
                VideoFilter.FAVORITES -> video.favorite
                VideoFilter.WATCHED -> video.watched
            }
            matchesQuery && matchesFilter
        }
    }

    Column {
        ScreenTitle(
            title = if (lessonId.isBlank()) "Video Academy" else "Video della lezione",
            subtitle = "Originali offline, YouTube incorporato e risorse tecniche verificate",
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Cerca titolo, argomento o autore") },
            singleLine = true,
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item { Icon(Icons.Default.FilterList, contentDescription = null) }
            items(
                listOf(
                    VideoFilter.ALL to "Tutti",
                    VideoFilter.ORIGINALS to "Originali",
                    VideoFilter.FAVORITES to "Preferiti",
                    VideoFilter.WATCHED to "Visti",
                ),
            ) { (value, label) ->
                FilterChip(
                    selected = filter == value,
                    onClick = { filter = value },
                    label = { Text(label) },
                )
            }
        }

        if (filtered.isEmpty()) {
            EmptyState("Nessun video", "Cambia filtro o apri il catalogo completo")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filtered, key = { it.id }) { video ->
                    VideoCard(video, vm, nav)
                }
                item {
                    Text(
                        "I video YouTube richiedono Internet. Le videolezioni MechLab contrassegnate come OFFLINE sono incluse nell'app.",
                        modifier = Modifier.padding(vertical = 14.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoCard(video: VideoEntity, vm: MainViewModel, nav: NavController) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    if (video.platform == "MECHLAB_LOCAL") Icons.Default.DownloadDone else Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 10.dp),
                )
                Column(Modifier.weight(1f)) {
                    Text(video.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${video.author} • ${video.language} • ${video.duration}")
                }
                IconButton(onClick = { vm.toggleVideo(video, favorite = !video.favorite) }) {
                    Icon(
                        if (video.favorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Preferito",
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(video.description)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item { AssistChip(onClick = {}, label = { Text(video.topic) }) }
                item {
                    AssistChip(
                        onClick = {},
                        label = { Text(if (video.platform == "MECHLAB_LOCAL") "OFFLINE" else video.platform) },
                    )
                }
                if (video.watched) {
                    item {
                        AssistChip(
                            onClick = {},
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            label = { Text("Visto") },
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = { nav.navigate(Routes.video(video.id)) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (video.watched) "Rivedi" else "Guarda")
            }
        }
    }
}

@Composable
fun VideoPlayerScreen(vm: MainViewModel, id: String, nav: NavController) {
    val videoState by vm.repo.video(id).collectAsState(null)
    val video = videoState ?: return Loading("Apertura videolezione")
    val context = LocalContext.current
    val source = remember(video.url, video.platform) { VideoSources.resolve(video.url, video.platform) }
    val online = remember { isOnline(context) }

    LaunchedEffect(video.id) {
        if (!video.watched) vm.toggleVideo(video, watched = true)
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Videolezione", video.topic) }
        item {
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                when (source.type) {
                    VideoSourceType.LOCAL_RAW -> LocalOrDirectPlayer(source.value, isRaw = true)
                    VideoSourceType.DIRECT_MEDIA -> LocalOrDirectPlayer(source.value, isRaw = false)
                    VideoSourceType.YOUTUBE -> {
                        if (online) YoutubePlayer(source.value, context.packageName) else OfflineVideoMessage()
                    }
                    VideoSourceType.EXTERNAL -> ExternalVideoMessage()
                }
            }
        }
        item {
            Card(Modifier.padding(horizontal = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(video.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${video.author} • ${video.duration} • ${video.level}")
                    Spacer(Modifier.height(10.dp))
                    Text(video.description)
                    Spacer(Modifier.height(8.dp))
                    Text("Perché è consigliato", style = MaterialTheme.typography.titleMedium)
                    Text(video.reason)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.toggleVideo(video, favorite = !video.favorite) }) {
                            Icon(if (video.favorite) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = null)
                            Text(if (video.favorite) " Preferito" else " Salva")
                        }
                        if (source.type != VideoSourceType.LOCAL_RAW) {
                            Button(onClick = { openExternal(context, video.url) }) { Text("Apri fonte") }
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.padding(horizontal = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Percorso collegato", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Dopo il video torna alla teoria, svolgi gli esercizi e chiudi con il quiz.")
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { nav.navigate(Routes.lesson(video.lessonId)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Apri la lezione") }
                    OutlinedButton(
                        onClick = { nav.navigate(Routes.lessonQuiz(video.lessonId)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Vai al quiz") }
                }
            }
        }
    }
}

@UnstableApi
@Composable
private fun LocalOrDirectPlayer(value: String, isRaw: Boolean) {
    val context = LocalContext.current
    val uri = remember(value, isRaw) {
        if (isRaw) {
            val resourceId = context.resources.getIdentifier(value, "raw", context.packageName)
            Uri.parse("android.resource://${context.packageName}/$resourceId")
        } else {
            Uri.parse(value)
        }
    }
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    AndroidView(
        factory = { playerContext ->
            PlayerView(playerContext).apply {
                this.player = player
                useController = true
            }
        },
        update = { it.player = player },
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubePlayer(videoId: String, packageName: String) {
    val html = remember(videoId, packageName) {
        """
        <!doctype html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <meta name="referrer" content="strict-origin-when-cross-origin">
          <style>html,body{margin:0;padding:0;background:#000;height:100%;overflow:hidden}iframe{width:100%;height:100%;border:0}</style>
        </head>
        <body>
          <iframe
            src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&origin=https%3A%2F%2F$packageName"
            title="YouTube video player"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            allowfullscreen></iframe>
        </body>
        </html>
        """.trimIndent()
    }
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://$packageName", html, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            if (webView.url == null) {
                webView.loadDataWithBaseURL("https://$packageName", html, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
    )
}

@Composable
private fun OfflineVideoMessage() {
    Card(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CloudOff, contentDescription = null)
            Text("Video disponibile con connessione Internet", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ExternalVideoMessage() {
    Card(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.PlayCircle, contentDescription = null)
            Text("Questa risorsa si apre nella piattaforma originale", fontWeight = FontWeight.Bold)
        }
    }
}

private fun isOnline(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

private fun openExternal(context: Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://${context.packageName}"))
        }
        context.startActivity(intent)
    }
}
