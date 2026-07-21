package it.lucamichetti.mechlabacademy.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import it.lucamichetti.mechlabacademy.data.local.AcademyDatabase
import it.lucamichetti.mechlabacademy.data.local.ExerciseProgressEntity
import it.lucamichetti.mechlabacademy.data.local.FlashcardProgressEntity
import it.lucamichetti.mechlabacademy.data.local.LessonProgressEntity
import it.lucamichetti.mechlabacademy.data.local.NoteEntity
import it.lucamichetti.mechlabacademy.data.local.QuizAttemptEntity
import it.lucamichetti.mechlabacademy.data.local.StudyPlanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupEnvelope(
    val formatVersion: Int = 1,
    val exportedAt: Long,
    val progress: List<LessonProgressBackup>,
    val attempts: List<QuizAttemptBackup>,
    val exercises: List<ExerciseProgressBackup>,
    val flashcards: List<FlashcardProgressBackup>,
    val notes: List<NoteBackup>,
    val plan: List<PlanBackup>,
)

@Serializable
data class LessonProgressBackup(
    val lessonId: String,
    val completed: Boolean,
    val favorite: Boolean,
    val personalNotes: String,
    val lastOpenedAt: Long,
    val studySeconds: Long,
    val completionPercent: Int,
)

@Serializable
data class QuizAttemptBackup(
    val id: Long,
    val questionId: String,
    val selectedAnswer: String,
    val correct: Boolean,
    val elapsedSeconds: Int,
    val attemptedAt: Long,
)

@Serializable
data class ExerciseProgressBackup(
    val exerciseId: String,
    val completed: Boolean,
    val answer: String,
    val updatedAt: Long,
)

@Serializable
data class FlashcardProgressBackup(
    val flashcardId: String,
    val state: String,
    val intervalDays: Int,
    val ease: Double,
    val repetitions: Int,
    val dueAt: Long,
    val lastReviewedAt: Long,
)

@Serializable
data class NoteBackup(
    val id: String,
    val title: String,
    val body: String,
    val subjectId: String,
    val lessonId: String,
    val notebook: String,
    val important: Boolean,
    val attachmentUri: String,
    val type: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class PlanBackup(
    val id: String,
    val date: String,
    val lessonId: String,
    val subjectId: String,
    val plannedMinutes: Int,
    val status: String,
    val mode: String,
)

class BackupManager(
    private val context: Context,
    private val database: AcademyDatabase,
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun export(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val dao = database.academyDao()
            val envelope = BackupEnvelope(
                exportedAt = System.currentTimeMillis(),
                progress = dao.allProgress().map {
                    LessonProgressBackup(
                        lessonId = it.lessonId,
                        completed = it.completed,
                        favorite = it.favorite,
                        personalNotes = it.personalNotes,
                        lastOpenedAt = it.lastOpenedAt,
                        studySeconds = it.studySeconds,
                        completionPercent = it.completionPercent,
                    )
                },
                attempts = dao.allAttempts().map {
                    QuizAttemptBackup(
                        id = it.id,
                        questionId = it.questionId,
                        selectedAnswer = it.selectedAnswer,
                        correct = it.correct,
                        elapsedSeconds = it.elapsedSeconds,
                        attemptedAt = it.attemptedAt,
                    )
                },
                exercises = dao.allExerciseProgress().map {
                    ExerciseProgressBackup(
                        exerciseId = it.exerciseId,
                        completed = it.completed,
                        answer = it.answer,
                        updatedAt = it.updatedAt,
                    )
                },
                flashcards = dao.allFlashcardProgress().map {
                    FlashcardProgressBackup(
                        flashcardId = it.flashcardId,
                        state = it.state,
                        intervalDays = it.intervalDays,
                        ease = it.ease,
                        repetitions = it.repetitions,
                        dueAt = it.dueAt,
                        lastReviewedAt = it.lastReviewedAt,
                    )
                },
                notes = dao.allNotes().map {
                    NoteBackup(
                        id = it.id,
                        title = it.title,
                        body = it.body,
                        subjectId = it.subjectId,
                        lessonId = it.lessonId,
                        notebook = it.notebook,
                        important = it.important,
                        attachmentUri = it.attachmentUri,
                        type = it.type,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    )
                },
                plan = dao.allPlan().map {
                    PlanBackup(
                        id = it.id,
                        date = it.date,
                        lessonId = it.lessonId,
                        subjectId = it.subjectId,
                        plannedMinutes = it.plannedMinutes,
                        status = it.status,
                        mode = it.mode,
                    )
                },
            )

            val stream = context.contentResolver.openOutputStream(uri)
                ?: error("Impossibile aprire il file di destinazione")
            stream.bufferedWriter().use { writer ->
                writer.write(json.encodeToString(envelope))
            }
        }
    }

    suspend fun import(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val stream = context.contentResolver.openInputStream(uri)
                ?: error("Impossibile leggere il file selezionato")
            val text = stream.bufferedReader().use { it.readText() }
            val envelope = json.decodeFromString<BackupEnvelope>(text)
            require(envelope.formatVersion == 1) {
                "Formato backup non supportato: ${envelope.formatVersion}"
            }

            database.withTransaction {
                val dao = database.academyDao()
                dao.clearProgress()
                dao.clearAttempts()
                dao.clearExerciseProgress()
                dao.clearFlashcardProgress()
                dao.clearNotes()
                dao.clearPlan()

                dao.restoreProgress(
                    envelope.progress.map {
                        LessonProgressEntity(
                            lessonId = it.lessonId,
                            completed = it.completed,
                            favorite = it.favorite,
                            personalNotes = it.personalNotes,
                            lastOpenedAt = it.lastOpenedAt,
                            studySeconds = it.studySeconds,
                            completionPercent = it.completionPercent,
                        )
                    },
                )
                dao.restoreAttempts(
                    envelope.attempts.map {
                        QuizAttemptEntity(
                            id = it.id,
                            questionId = it.questionId,
                            selectedAnswer = it.selectedAnswer,
                            correct = it.correct,
                            elapsedSeconds = it.elapsedSeconds,
                            attemptedAt = it.attemptedAt,
                        )
                    },
                )
                dao.restoreExerciseProgress(
                    envelope.exercises.map {
                        ExerciseProgressEntity(
                            exerciseId = it.exerciseId,
                            completed = it.completed,
                            answer = it.answer,
                            updatedAt = it.updatedAt,
                        )
                    },
                )
                dao.restoreFlashcardProgress(
                    envelope.flashcards.map {
                        FlashcardProgressEntity(
                            flashcardId = it.flashcardId,
                            state = it.state,
                            intervalDays = it.intervalDays,
                            ease = it.ease,
                            repetitions = it.repetitions,
                            dueAt = it.dueAt,
                            lastReviewedAt = it.lastReviewedAt,
                        )
                    },
                )
                dao.insertNotes(
                    envelope.notes.map {
                        NoteEntity(
                            id = it.id,
                            title = it.title,
                            body = it.body,
                            subjectId = it.subjectId,
                            lessonId = it.lessonId,
                            notebook = it.notebook,
                            important = it.important,
                            attachmentUri = it.attachmentUri,
                            type = it.type,
                            createdAt = it.createdAt,
                            updatedAt = it.updatedAt,
                        )
                    },
                )
                dao.insertStudyPlan(
                    envelope.plan.map {
                        StudyPlanEntity(
                            id = it.id,
                            date = it.date,
                            lessonId = it.lessonId,
                            subjectId = it.subjectId,
                            plannedMinutes = it.plannedMinutes,
                            status = it.status,
                            mode = it.mode,
                        )
                    },
                )
            }
        }
    }
}
