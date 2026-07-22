package it.lucamichetti.mechlabacademy.domain

enum class VideoSourceType {
    LOCAL_RAW,
    YOUTUBE,
    DIRECT_MEDIA,
    EXTERNAL,
}

data class VideoSource(
    val type: VideoSourceType,
    val value: String,
)

object VideoSources {
    fun resolve(url: String, platform: String): VideoSource {
        val cleanUrl = url.trim()
        if (platform.equals("MECHLAB_LOCAL", ignoreCase = true) || cleanUrl.startsWith("raw:")) {
            return VideoSource(VideoSourceType.LOCAL_RAW, cleanUrl.removePrefix("raw:"))
        }
        youtubeId(cleanUrl)?.let { return VideoSource(VideoSourceType.YOUTUBE, it) }
        if (
            cleanUrl.endsWith(".mp4", ignoreCase = true) ||
            cleanUrl.endsWith(".m3u8", ignoreCase = true) ||
            cleanUrl.endsWith(".mpd", ignoreCase = true)
        ) {
            return VideoSource(VideoSourceType.DIRECT_MEDIA, cleanUrl)
        }
        return VideoSource(VideoSourceType.EXTERNAL, cleanUrl)
    }

    fun youtubeId(url: String): String? {
        val trimmed = url.trim()
        val candidates = listOf(
            Regex("(?:youtube\\.com/watch\\?v=)([A-Za-z0-9_-]{6,})"),
            Regex("(?:youtu\\.be/)([A-Za-z0-9_-]{6,})"),
            Regex("(?:youtube\\.com/embed/)([A-Za-z0-9_-]{6,})"),
            Regex("(?:youtube\\.com/shorts/)([A-Za-z0-9_-]{6,})"),
        )
        return candidates.firstNotNullOfOrNull { it.find(trimmed)?.groupValues?.getOrNull(1) }
    }
}
