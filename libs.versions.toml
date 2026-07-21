package it.lucamichetti.mechlabacademy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.lucamichetti.mechlabacademy.MechLabApplication
import it.lucamichetti.mechlabacademy.data.local.NoteEntity
import it.lucamichetti.mechlabacademy.data.local.QuizQuestionEntity
import it.lucamichetti.mechlabacademy.data.local.VideoEntity
import it.lucamichetti.mechlabacademy.data.preferences.AppSettings
import it.lucamichetti.mechlabacademy.domain.ProgressCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SeedState(
    val loading: Boolean = true,
    val stage: String = "Preparazione",
    val error: String? = null,
)

data class HomeState(
    val total: Int = 0,
    val completed: Int = 0,
    val percent: Int = 0,
    val studiedSeconds: Long = 0,
    val streak: Int = 0,
    val latestLessonId: String? = null,
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val container = (app as MechLabApplication).container

    val repo = container.repository
    val settings = container.settings.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
    )

    private val _seed = MutableStateFlow(SeedState())
    val seed = _seed.asStateFlow()

    val home = combine(repo.allProgress, repo.subjects) { progress, _ ->
        val completed = progress.count { it.completed }
        val summary = ProgressCalculator.calculate(
            total = TOTAL_LESSONS,
            completed = completed,
            studiedSeconds = progress.sumOf { it.studySeconds },
            activeEpochDays = progress
                .filter { it.lastOpenedAt > 0 }
                .map { it.lastOpenedAt / MILLIS_PER_DAY }
                .toSet(),
        )
        HomeState(
            total = summary.total,
            completed = summary.completed,
            percent = summary.percent,
            studiedSeconds = summary.studiedSeconds,
            streak = summary.streak,
            latestLessonId = progress.maxByOrNull { it.lastOpenedAt }?.lessonId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState(),
    )

    init {
        viewModelScope.launch {
            _seed.value = SeedState(loading = true, stage = "Importazione contenuti")
            container.seedImporter.ensureSeeded { stage ->
                _seed.value = SeedState(loading = true, stage = stage)
            }.fold(
                onSuccess = { _seed.value = SeedState(loading = false, stage = "Pronto") },
                onFailure = {
                    _seed.value = SeedState(
                        loading = false,
                        stage = "Errore",
                        error = it.message ?: it::class.java.simpleName,
                    )
                },
            )
        }
    }

    fun setTheme(value: String) = viewModelScope.launch { container.settings.setTheme(value) }
    fun setTextScale(value: Float) = viewModelScope.launch { container.settings.setTextScale(value) }
    fun setYear(value: Int) = viewModelScope.launch { container.settings.setYear(value) }
    fun setWeeklyHours(value: Int) = viewModelScope.launch { container.settings.setWeeklyHours(value) }
    fun setReminder(value: Boolean) = viewModelScope.launch {
        container.settings.setReminder(value)
        container.reminderScheduler.setEnabled(value)
    }

    fun openLesson(id: String) = viewModelScope.launch { repo.openLesson(id) }
    fun complete(id: String, value: Boolean) = viewModelScope.launch { repo.setCompleted(id, value) }
    fun favorite(id: String, value: Boolean) = viewModelScope.launch { repo.setFavorite(id, value) }
    fun saveLessonNotes(id: String, text: String) = viewModelScope.launch { repo.saveLessonNotes(id, text) }

    fun answer(question: QuizQuestionEntity, answer: String, seconds: Int) =
        viewModelScope.launch { repo.answer(question, answer, seconds) }

    fun completeExercise(id: String, answer: String) =
        viewModelScope.launch { repo.completeExercise(id, answer) }

    fun reviewCard(id: String, quality: Int) =
        viewModelScope.launch { repo.reviewFlashcard(id, quality) }

    fun toggleVideo(video: VideoEntity, favorite: Boolean? = null, watched: Boolean? = null) =
        viewModelScope.launch { repo.toggleVideo(video, favorite, watched) }

    fun saveNote(existing: NoteEntity?, title: String, body: String) =
        viewModelScope.launch { repo.saveNote(existing, title, body) }

    fun deleteNote(note: NoteEntity) = viewModelScope.launch { repo.deleteNote(note) }

    fun saveCalculation(key: String, inputs: String, result: String, unit: String) =
        viewModelScope.launch { repo.saveCalculation(key, inputs, result, unit) }

    val backupManager get() = container.backupManager

    private companion object {
        const val TOTAL_LESSONS = 840
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
