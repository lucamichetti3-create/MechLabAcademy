package it.lucamichetti.mechlabacademy.data.repository

import it.lucamichetti.mechlabacademy.data.local.AcademyDao
import it.lucamichetti.mechlabacademy.data.local.CalculationHistoryEntity
import it.lucamichetti.mechlabacademy.data.local.ExerciseProgressEntity
import it.lucamichetti.mechlabacademy.data.local.FlashcardProgressEntity
import it.lucamichetti.mechlabacademy.data.local.LessonProgressEntity
import it.lucamichetti.mechlabacademy.data.local.NoteEntity
import it.lucamichetti.mechlabacademy.data.local.QuizAttemptEntity
import it.lucamichetti.mechlabacademy.data.local.QuizQuestionEntity
import it.lucamichetti.mechlabacademy.data.local.StudyPlanEntity
import it.lucamichetti.mechlabacademy.data.local.VideoEntity
import it.lucamichetti.mechlabacademy.domain.SpacedRepetition
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

data class LessonResourceCounts(
    val videos: Int = 0,
    val exercises: Int = 0,
    val flashcards: Int = 0,
    val maps: Int = 0,
    val labs: Int = 0,
)

data class StudySession(
    val lessonId: String,
    val lessonTitle: String,
    val macroarea: String,
    val videoId: String?,
    val resourceCounts: LessonResourceCounts,
    val quizCount: Int,
)

data class GlobalSearchItem(
    val id: String,
    val type: String,
    val title: String,
    val subtitle: String,
    val targetId: String,
)

class AcademyRepository(private val dao: AcademyDao) {
    val subjects = dao.observeSubjects()
    val allProgress = dao.observeAllProgress()
    val attempts = dao.observeAttempts()
    val exercises = dao.observeExercises()
    val exerciseProgress = dao.observeExerciseProgress()
    val flashcards = dao.observeFlashcards()
    val flashcardProgress = dao.observeFlashcardProgress()
    val videos = dao.observeVideos()
    val maps = dao.observeMaps()
    val labs = dao.observeLabs()
    val tools = dao.observeTools()
    val notes = dao.observeNotes()
    val plan = dao.observeStudyPlan()
    val calculationHistory = dao.observeCalculationHistory()

    fun subject(id: String) = dao.observeSubject(id)
    fun lessons(subjectId: String, year: Int = 0) = dao.observeLessons(subjectId, year)
    fun lesson(id: String) = dao.observeLesson(id)
    fun progress(id: String) = dao.observeProgress(id)
    fun glossary(query: String) = dao.observeGlossary(query.trim())
    fun map(id: String) = dao.observeMap(id)
    fun lab(id: String) = dao.observeLab(id)
    fun exercise(id: String) = dao.observeExercise(id)
    fun video(id: String) = dao.observeVideo(id)
    fun videosForLesson(lessonId: String = "") = dao.observeVideosForLesson(lessonId)
    fun exercisesForLesson(lessonId: String = "") = dao.observeExercisesForLesson(lessonId)
    fun flashcardsForLesson(lessonId: String = "") = dao.observeFlashcardsForLesson(lessonId)
    fun search(query: String) = if (query.isBlank()) flowOf(emptyList()) else dao.searchLessons(query.trim())

    fun resourceCounts(lessonId: String) = combine(
        dao.observeVideoCount(lessonId),
        dao.observeExerciseCount(lessonId),
        dao.observeFlashcardCount(lessonId),
        dao.observeMapCount(lessonId),
        dao.observeLabCount(lessonId),
    ) { videos, exercises, flashcards, maps, labs ->
        LessonResourceCounts(videos, exercises, flashcards, maps, labs)
    }

    suspend fun firstLessonId(): String? = dao.firstLesson()?.id

    suspend fun buildStudySession(year: Int): StudySession? {
        val lesson = dao.nextIncompleteLesson(year) ?: dao.firstLesson() ?: return null
        val counts = resourceCounts(lesson.id).first()
        val linkedVideo = dao.observeVideosForLesson(lesson.id).first().firstOrNull()
        return StudySession(
            lessonId = lesson.id,
            lessonTitle = lesson.title,
            macroarea = lesson.macroarea,
            videoId = linkedVideo?.id,
            resourceCounts = counts,
            quizCount = dao.quizCountForLesson(lesson.id),
        )
    }

    suspend fun globalSearch(query: String): List<GlobalSearchItem> = coroutineScope {
        val normalized = query.trim()
        if (normalized.length < 2) return@coroutineScope emptyList()

        val lessons = async { dao.searchLessonsNow(normalized) }
        val videos = async { dao.searchVideosNow(normalized) }
        val glossary = async { dao.searchGlossaryNow(normalized) }
        val exercises = async { dao.searchExercisesNow(normalized) }
        val labs = async { dao.searchLabsNow(normalized) }
        val tools = async { dao.searchToolsNow(normalized) }

        buildList {
            addAll(lessons.await().map {
                GlobalSearchItem(it.id, "LESSON", it.title, "${it.year}° anno • ${it.macroarea}", it.id)
            })
            addAll(videos.await().map {
                GlobalSearchItem(it.id, "VIDEO", it.title, "${it.author} • ${it.topic}", it.id)
            })
            addAll(glossary.await().map {
                GlobalSearchItem(it.id, "GLOSSARY", it.italianTerm, "${it.englishTerm} • ${it.definition}", it.italianTerm)
            })
            addAll(exercises.await().map {
                GlobalSearchItem(it.id, "EXERCISE", it.title, "${it.category} • ${it.difficulty}", it.lessonId)
            })
            addAll(labs.await().map {
                GlobalSearchItem(it.id, "LAB", it.title, it.objective, it.id)
            })
            addAll(tools.await().map {
                GlobalSearchItem(it.id, "TOOL", it.name, "${it.category} • ${it.formula}", it.id)
            })
        }.sortedWith(compareBy<GlobalSearchItem> { searchTypeOrder(it.type) }.thenBy { it.title }).take(120)
    }

    suspend fun openLesson(id: String) {
        val current = dao.observeProgress(id).first() ?: LessonProgressEntity(id)
        dao.upsertProgress(current.copy(lastOpenedAt = System.currentTimeMillis()))
    }

    suspend fun addStudySeconds(id: String, seconds: Long) {
        if (seconds <= 0) return
        val current = dao.observeProgress(id).first() ?: LessonProgressEntity(id)
        dao.upsertProgress(
            current.copy(
                studySeconds = current.studySeconds + seconds,
                lastOpenedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun setCompleted(id: String, value: Boolean) {
        val current = dao.observeProgress(id).first() ?: LessonProgressEntity(id)
        dao.upsertProgress(
            current.copy(
                completed = value,
                completionPercent = if (value) 100 else 0,
                lastOpenedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun setFavorite(id: String, value: Boolean) {
        val current = dao.observeProgress(id).first() ?: LessonProgressEntity(id)
        dao.upsertProgress(current.copy(favorite = value))
    }

    suspend fun saveLessonNotes(id: String, text: String) {
        val current = dao.observeProgress(id).first() ?: LessonProgressEntity(id)
        dao.upsertProgress(
            current.copy(
                personalNotes = text,
                lastOpenedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun quiz(lessonId: String = "", limit: Int = 20) =
        dao.quiz(lessonId = lessonId, limit = limit.coerceIn(1, 100))

    suspend fun answer(question: QuizQuestionEntity, answer: String, seconds: Int) {
        dao.insertAttempt(
            QuizAttemptEntity(
                questionId = question.id,
                selectedAnswer = answer,
                correct = answer.trim().equals(question.correctAnswer.trim(), ignoreCase = true),
                elapsedSeconds = seconds.coerceAtLeast(0),
            ),
        )
    }

    suspend fun completeExercise(id: String, answer: String) {
        dao.upsertExerciseProgress(
            ExerciseProgressEntity(
                exerciseId = id,
                completed = true,
                answer = answer,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun reviewFlashcard(id: String, quality: Int) {
        val current = dao.observeFlashcardProgress().first().firstOrNull { it.flashcardId == id }
            ?: FlashcardProgressEntity(id)
        val result = SpacedRepetition.review(
            quality = quality,
            intervalDays = current.intervalDays,
            ease = current.ease,
            repetitions = current.repetitions,
        )
        dao.upsertFlashcardProgress(
            current.copy(
                state = result.state,
                intervalDays = result.intervalDays,
                ease = result.ease,
                repetitions = result.repetitions,
                dueAt = result.dueAt,
                lastReviewedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun toggleVideo(
        video: VideoEntity,
        favorite: Boolean? = null,
        watched: Boolean? = null,
    ) {
        dao.updateVideo(
            video.copy(
                favorite = favorite ?: video.favorite,
                watched = watched ?: video.watched,
            ),
        )
    }

    suspend fun saveNote(
        existing: NoteEntity?,
        title: String,
        body: String,
        type: String = "NOTE",
    ) {
        val now = System.currentTimeMillis()
        val base = existing ?: NoteEntity(
            id = UUID.randomUUID().toString(),
            title = "",
            body = "",
            subjectId = "",
            lessonId = "",
            notebook = "Generale",
            important = false,
            attachmentUri = "",
            type = type,
            createdAt = now,
            updatedAt = now,
        )
        dao.upsertNote(base.copy(title = title.trim(), body = body.trim(), updatedAt = now))
    }

    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    suspend fun saveCalculation(key: String, inputs: String, result: String, unit: String) {
        dao.insertCalculation(
            CalculationHistoryEntity(
                toolKey = key,
                inputsJson = inputs,
                result = result,
                unit = unit,
            ),
        )
    }

    suspend fun upsertPlan(item: StudyPlanEntity) = dao.upsertStudyPlan(item)
    suspend fun deletePlan(id: String) = dao.deleteStudyPlan(id)

    private fun searchTypeOrder(type: String): Int = when (type) {
        "LESSON" -> 0
        "VIDEO" -> 1
        "EXERCISE" -> 2
        "LAB" -> 3
        "TOOL" -> 4
        "GLOSSARY" -> 5
        else -> 9
    }
}
