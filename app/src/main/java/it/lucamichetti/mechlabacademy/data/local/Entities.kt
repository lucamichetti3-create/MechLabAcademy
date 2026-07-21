package it.lucamichetti.mechlabacademy.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(@PrimaryKey val id:String, val code:String, val name:String, val category:String, val yearsJson:String, val sortOrder:Int, val description:String)

@Entity(tableName="lessons", foreignKeys=[ForeignKey(SubjectEntity::class,["id"],["subjectId"],onDelete=ForeignKey.CASCADE)], indices=[Index("subjectId"),Index("year"),Index("status")])
data class LessonEntity(
    @PrimaryKey val id:String, val subjectId:String, val year:Int, val macroarea:String, val module:String, val chapter:String,
    val title:String, val durationMinutes:Int, val difficulty:String, val prerequisitesJson:String, val objectivesJson:String,
    val introduction:String, val explanation:String, val formulasJson:String, val symbolsJson:String, val intuitiveDemoJson:String,
    val numericExamplesJson:String, val industrialExamplesJson:String, val practicalExamplesJson:String, val crossLinksJson:String,
    val commonErrorsJson:String, val keywordsJson:String, val summary:String, val selfCheckJson:String, val guidedExercisesJson:String,
    val autonomousExercisesJson:String, val solutionsJson:String, val approfondimentiJson:String, val sourceRefsJson:String,
    val status:String, val sortOrder:Int, val contentVersion:Int
)

@Entity(tableName="lesson_progress", foreignKeys=[ForeignKey(LessonEntity::class,["id"],["lessonId"],onDelete=ForeignKey.CASCADE)], indices=[Index("lessonId",unique=true)])
data class LessonProgressEntity(@PrimaryKey val lessonId:String, val completed:Boolean=false, val favorite:Boolean=false, val personalNotes:String="", val lastOpenedAt:Long=0, val studySeconds:Long=0, val completionPercent:Int=0)

@Entity(tableName="quiz_questions", indices=[Index("lessonId"),Index("subjectId")])
data class QuizQuestionEntity(@PrimaryKey val id:String, val lessonId:String, val subjectId:String, val type:String, val prompt:String, val optionsJson:String, val correctAnswer:String, val explanation:String, val stepsJson:String, val typicalError:String, val level:String, val source:String, val score:Int)

@Entity(tableName="quiz_attempts", indices=[Index("questionId"),Index("attemptedAt")])
data class QuizAttemptEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val questionId:String, val selectedAnswer:String, val correct:Boolean, val elapsedSeconds:Int, val attemptedAt:Long=System.currentTimeMillis())

@Entity(tableName="exercises", indices=[Index("lessonId"),Index("subjectId"),Index("category")])
data class ExerciseEntity(@PrimaryKey val id:String, val lessonId:String, val subjectId:String, val category:String, val title:String, val prompt:String, val dataJson:String, val expected:String, val solution:String, val stepsJson:String, val difficulty:String, val score:Int, val source:String)

@Entity(tableName="exercise_progress")
data class ExerciseProgressEntity(@PrimaryKey val exerciseId:String, val completed:Boolean=false, val answer:String="", val updatedAt:Long=0)

@Entity(tableName="flashcards", indices=[Index("lessonId"),Index("subjectId")])
data class FlashcardEntity(@PrimaryKey val id:String, val lessonId:String, val subjectId:String, val front:String, val back:String, val formula:String, val imageAsset:String, val italianTerm:String, val englishTerm:String, val difficulty:String, val year:Int)

@Entity(tableName="flashcard_progress")
data class FlashcardProgressEntity(@PrimaryKey val flashcardId:String, val state:String="NEW", val intervalDays:Int=0, val ease:Double=2.5, val repetitions:Int=0, val dueAt:Long=0, val lastReviewedAt:Long=0)

@Entity(tableName="glossary", indices=[Index("subjectId"),Index(value=["italianTerm"])])
data class GlossaryEntity(@PrimaryKey val id:String, val italianTerm:String, val englishTerm:String, val definition:String, val practicalUse:String, val symbol:String, val unit:String, val synonymsJson:String, val commonErrorsJson:String, val subjectId:String, val example:String, val source:String)

@Entity(tableName="videos", indices=[Index("lessonId"),Index("subjectId")])
data class VideoEntity(@PrimaryKey val id:String, val title:String, val author:String, val url:String, val platform:String, val language:String, val duration:String, val level:String, val topic:String, val lessonId:String, val subjectId:String, val description:String, val reason:String, val lastVerified:String, val linkStatus:String, val favorite:Boolean, val watched:Boolean)

@Entity(tableName="concept_maps", indices=[Index("lessonId"),Index("subjectId")])
data class ConceptMapEntity(@PrimaryKey val id:String, val subjectId:String, val lessonId:String, val title:String, val category:String, val nodesJson:String, val edgesJson:String, val favorite:Boolean)

@Entity(tableName="labs", indices=[Index("lessonId"),Index("subjectId")])
data class LabEntity(@PrimaryKey val id:String, val subjectId:String, val lessonId:String, val title:String, val objective:String, val tools:String, val dpi:String, val theory:String, val procedureJson:String, val operationOrderJson:String, val checksJson:String, val risksJson:String, val errorsJson:String, val expectedResult:String, val questionsJson:String, val simulationJson:String, val reportTemplate:String)

@Entity(tableName="technical_tools", indices=[Index("lessonId"),Index("category")])
data class TechnicalToolEntity(@PrimaryKey val id:String, val key:String, val name:String, val category:String, val formula:String, val symbols:String, val inputSpecJson:String, val explanation:String, val lessonId:String, val professionalDisclaimer:String)

@Entity(tableName="notes", indices=[Index("lessonId"),Index("subjectId"),Index("type")])
data class NoteEntity(@PrimaryKey val id:String, val title:String, val body:String, val subjectId:String, val lessonId:String, val notebook:String, val important:Boolean, val attachmentUri:String, val type:String, val createdAt:Long, val updatedAt:Long)

@Entity(tableName="study_plan", indices=[Index("date"),Index("lessonId")])
data class StudyPlanEntity(@PrimaryKey val id:String, val date:String, val lessonId:String, val subjectId:String, val plannedMinutes:Int, val status:String, val mode:String)

@Entity(tableName="calculation_history")
data class CalculationHistoryEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val toolKey:String, val inputsJson:String, val result:String, val unit:String, val createdAt:Long=System.currentTimeMillis())
