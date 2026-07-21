package it.lucamichetti.mechlabacademy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(
    entities=[SubjectEntity::class,LessonEntity::class,LessonProgressEntity::class,QuizQuestionEntity::class,QuizAttemptEntity::class,
        ExerciseEntity::class,ExerciseProgressEntity::class,FlashcardEntity::class,FlashcardProgressEntity::class,GlossaryEntity::class,
        VideoEntity::class,ConceptMapEntity::class,LabEntity::class,TechnicalToolEntity::class,NoteEntity::class,StudyPlanEntity::class,
        CalculationHistoryEntity::class],
    version=1,
    exportSchema=true
)
abstract class AcademyDatabase:RoomDatabase(){
    abstract fun academyDao():AcademyDao
    companion object { val ALL_MIGRATIONS:Array<Migration> = emptyArray() }
}
