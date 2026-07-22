package it.lucamichetti.mechlabacademy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VideoSourcesTest {
    @Test
    fun resolvesLocalVideo() {
        assertEquals(
            VideoSource(VideoSourceType.LOCAL_RAW, "mechlab_forze_momenti"),
            VideoSources.resolve("raw:mechlab_forze_momenti", "MECHLAB_LOCAL"),
        )
    }

    @Test
    fun extractsYoutubeIds() {
        assertEquals("f08Y39UiC-o", VideoSources.youtubeId("https://www.youtube.com/watch?v=f08Y39UiC-o"))
        assertEquals("f08Y39UiC-o", VideoSources.youtubeId("https://youtu.be/f08Y39UiC-o"))
        assertNull(VideoSources.youtubeId("https://example.com/video.mp4"))
    }
}
