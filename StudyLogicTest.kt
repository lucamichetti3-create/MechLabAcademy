package it.lucamichetti.mechlabacademy.data.seed

import android.content.Context
import androidx.room.withTransaction
import it.lucamichetti.mechlabacademy.data.local.AcademyDatabase
import it.lucamichetti.mechlabacademy.data.local.ConceptMapEntity
import it.lucamichetti.mechlabacademy.data.local.ExerciseEntity
import it.lucamichetti.mechlabacademy.data.local.FlashcardEntity
import it.lucamichetti.mechlabacademy.data.local.GlossaryEntity
import it.lucamichetti.mechlabacademy.data.local.LabEntity
import it.lucamichetti.mechlabacademy.data.local.LessonEntity
import it.lucamichetti.mechlabacademy.data.local.NoteEntity
import it.lucamichetti.mechlabacademy.data.local.QuizQuestionEntity
import it.lucamichetti.mechlabacademy.data.local.StudyPlanEntity
import it.lucamichetti.mechlabacademy.data.local.SubjectEntity
import it.lucamichetti.mechlabacademy.data.local.TechnicalToolEntity
import it.lucamichetti.mechlabacademy.data.local.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class SeedImporter(
    private val context: Context,
    private val database: AcademyDatabase,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun ensureSeeded(onProgress: (String) -> Unit = {}): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (database.academyDao().subjectCount() > 0) return@runCatching

                database.withTransaction {
                    val dao = database.academyDao()

                    onProgress("Importazione materie")
                    dao.insertSubjects(
                        read("subjects.json").map {
                            SubjectEntity(
                                id = it.string("id"),
                                code = it.string("code"),
                                name = it.string("name"),
                                category = it.string("category"),
                                yearsJson = it.raw("years"),
                                sortOrder = it.int("sortOrder"),
                                description = it.string("description"),
                            )
                        },
                    )

                    onProgress("Importazione lezioni")
                    read("lessons.json").chunked(100).forEach { chunk ->
                        dao.insertLessons(
                            chunk.map { item ->
                                LessonEntity(
                                    id = item.string("id"),
                                    subjectId = item.string("subjectId"),
                                    year = item.int("year"),
                                    macroarea = item.string("macroarea"),
                                    module = item.string("module"),
                                    chapter = item.string("chapter"),
                                    title = item.string("title"),
                                    durationMinutes = item.int("durationMinutes"),
                                    difficulty = item.string("difficulty"),
                                    prerequisitesJson = item.raw("prerequisites"),
                                    objectivesJson = item.raw("objectives"),
                                    introduction = item.string("introduction"),
                                    explanation = item.string("explanation"),
                                    formulasJson = item.raw("formulas"),
                                    symbolsJson = item.raw("symbols"),
                                    intuitiveDemoJson = item.raw("intuitiveDemo"),
                                    numericExamplesJson = item.raw("numericExamples"),
                                    industrialExamplesJson = item.raw("industrialExamples"),
                                    practicalExamplesJson = item.raw("practicalExamples"),
                                    crossLinksJson = item.raw("crossLinks"),
                                    commonErrorsJson = item.raw("commonErrors"),
                                    keywordsJson = item.raw("keywords"),
                                    summary = item.string("summary"),
                                    selfCheckJson = item.raw("selfCheck"),
                                    guidedExercisesJson = item.raw("guidedExercises"),
                                    autonomousExercisesJson = item.raw("autonomousExercises"),
                                    solutionsJson = item.raw("solutions"),
                                    approfondimentiJson = item.raw("approfondimenti"),
                                    sourceRefsJson = item.raw("sourceRefs"),
                                    status = item.string("status"),
                                    sortOrder = item.int("sortOrder"),
                                    contentVersion = item.int("contentVersion"),
                                )
                            },
                        )
                    }

                    onProgress("Importazione quiz ed esercizi")
                    read("quiz.json").chunked(250).forEach { chunk ->
                        dao.insertQuiz(
                            chunk.map { item ->
                                QuizQuestionEntity(
                                    id = item.string("id"),
                                    lessonId = item.string("lessonId"),
                                    subjectId = item.string("subjectId"),
                                    type = item.string("type"),
                                    prompt = item.string("prompt"),
                                    optionsJson = item.raw("options"),
                                    correctAnswer = item.string("correctAnswer"),
                                    explanation = item.string("explanation"),
                                    stepsJson = item.raw("steps"),
                                    typicalError = item.string("typicalError"),
                                    level = item.string("level"),
                                    source = item.string("source"),
                                    score = item.int("score"),
                                )
                            },
                        )
                    }
                    read("exercises.json").chunked(200).forEach { chunk ->
                        dao.insertExercises(
                            chunk.map { item ->
                                ExerciseEntity(
                                    id = item.string("id"),
                                    lessonId = item.string("lessonId"),
                                    subjectId = item.string("subjectId"),
                                    category = item.string("category"),
                                    title = item.string("title"),
                                    prompt = item.string("prompt"),
                                    dataJson = item.raw("data"),
                                    expected = item.string("expected"),
                                    solution = item.string("solution"),
                                    stepsJson = item.raw("steps"),
                                    difficulty = item.string("difficulty"),
                                    score = item.int("score"),
                                    source = item.string("source"),
                                )
                            },
                        )
                    }

                    onProgress("Importazione flashcard e glossario")
                    read("flashcards.json").chunked(250).forEach { chunk ->
                        dao.insertFlashcards(
                            chunk.map { item ->
                                FlashcardEntity(
                                    id = item.string("id"),
                                    lessonId = item.string("lessonId"),
                                    subjectId = item.string("subjectId"),
                                    front = item.string("front"),
                                    back = item.string("back"),
                                    formula = item.string("formula"),
                                    imageAsset = item.string("imageAsset"),
                                    italianTerm = item.string("italianTerm"),
                                    englishTerm = item.string("englishTerm"),
                                    difficulty = item.string("difficulty"),
                                    year = item.int("year"),
                                )
                            },
                        )
                    }
                    read("glossary.json").chunked(250).forEach { chunk ->
                        dao.insertGlossary(
                            chunk.map { item ->
                                GlossaryEntity(
                                    id = item.string("id"),
                                    italianTerm = item.string("italianTerm"),
                                    englishTerm = item.string("englishTerm"),
                                    definition = item.string("definition"),
                                    practicalUse = item.string("practicalUse"),
                                    symbol = item.string("symbol"),
                                    unit = item.string("unit"),
                                    synonymsJson = item.raw("synonyms"),
                                    commonErrorsJson = item.raw("commonErrors"),
                                    subjectId = item.string("subjectId"),
                                    example = item.string("example"),
                                    source = item.string("source"),
                                )
                            },
                        )
                    }

                    onProgress("Importazione mappe e laboratori")
                    dao.insertMaps(
                        read("maps.json").map { item ->
                            ConceptMapEntity(
                                id = item.string("id"),
                                subjectId = item.string("subjectId"),
                                lessonId = item.string("lessonId"),
                                title = item.string("title"),
                                category = item.string("category"),
                                nodesJson = item.raw("nodes"),
                                edgesJson = item.raw("edges"),
                                favorite = item.boolean("favorite"),
                            )
                        },
                    )
                    dao.insertLabs(
                        read("labs.json").map { item ->
                            LabEntity(
                                id = item.string("id"),
                                subjectId = item.string("subjectId"),
                                lessonId = item.string("lessonId"),
                                title = item.string("title"),
                                objective = item.string("objective"),
                                tools = item.string("tools"),
                                dpi = item.string("dpi"),
                                theory = item.string("theory"),
                                procedureJson = item.raw("procedure"),
                                operationOrderJson = item.raw("operationOrder"),
                                checksJson = item.raw("checks"),
                                risksJson = item.raw("risks"),
                                errorsJson = item.raw("errors"),
                                expectedResult = item.string("expectedResult"),
                                questionsJson = item.raw("questions"),
                                simulationJson = item.raw("simulation"),
                                reportTemplate = item.string("reportTemplate"),
                            )
                        },
                    )

                    onProgress("Importazione video, strumenti e piano")
                    dao.insertVideos(
                        read("videos.json").map { item ->
                            VideoEntity(
                                id = item.string("id"),
                                title = item.string("title"),
                                author = item.string("author"),
                                url = item.string("url"),
                                platform = item.string("platform"),
                                language = item.string("language"),
                                duration = item.string("duration"),
                                level = item.string("level"),
                                topic = item.string("topic"),
                                lessonId = item.string("lessonId"),
                                subjectId = item.string("subjectId"),
                                description = item.string("description"),
                                reason = item.string("reason"),
                                lastVerified = item.string("lastVerified"),
                                linkStatus = item.string("linkStatus"),
                                favorite = item.boolean("favorite"),
                                watched = item.boolean("watched"),
                            )
                        },
                    )
                    dao.insertTools(
                        read("tools.json").map { item ->
                            TechnicalToolEntity(
                                id = item.string("id"),
                                key = item.string("key"),
                                name = item.string("name"),
                                category = item.string("category"),
                                formula = item.string("formula"),
                                symbols = item.string("symbols"),
                                inputSpecJson = item.raw("inputSpec"),
                                explanation = item.string("explanation"),
                                lessonId = item.string("lessonId"),
                                professionalDisclaimer = item.string("professionalDisclaimer"),
                            )
                        },
                    )
                    dao.insertNotes(
                        read("notes.json").map { item ->
                            NoteEntity(
                                id = item.string("id"),
                                title = item.string("title"),
                                body = item.string("body"),
                                subjectId = item.string("subjectId"),
                                lessonId = item.string("lessonId"),
                                notebook = item.string("notebook"),
                                important = item.boolean("important"),
                                attachmentUri = item.string("attachmentUri"),
                                type = item.string("type"),
                                createdAt = item.long("createdAt"),
                                updatedAt = item.long("updatedAt"),
                            )
                        },
                    )
                    dao.insertStudyPlan(
                        read("study_plan.json").map { item ->
                            StudyPlanEntity(
                                id = item.string("id"),
                                date = item.string("date"),
                                lessonId = item.string("lessonId"),
                                subjectId = item.string("subjectId"),
                                plannedMinutes = item.int("plannedMinutes"),
                                status = item.string("status"),
                                mode = item.string("mode"),
                            )
                        },
                    )
                }
            }
        }

    private fun read(name: String): List<JsonObject> {
        val text = context.assets.open("seed/$name").bufferedReader().use { it.readText() }
        return json.parseToJsonElement(text).jsonArray.map { it.jsonObject }
    }

    private fun JsonObject.string(key: String): String =
        this[key]?.jsonPrimitive?.contentOrNull.orEmpty()

    private fun JsonObject.int(key: String): Int =
        this[key]?.jsonPrimitive?.intOrNull ?: 0

    private fun JsonObject.long(key: String): Long =
        this[key]?.jsonPrimitive?.longOrNull ?: 0L

    private fun JsonObject.boolean(key: String): Boolean =
        this[key]?.jsonPrimitive?.booleanOrNull ?: false

    private fun JsonObject.raw(key: String): String = this[key]?.toString() ?: "[]"
}
