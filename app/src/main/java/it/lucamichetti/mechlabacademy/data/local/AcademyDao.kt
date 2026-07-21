package it.lucamichetti.mechlabacademy.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {
    @Query("SELECT COUNT(*) FROM subjects") suspend fun subjectCount():Int
    @Query("SELECT * FROM subjects ORDER BY sortOrder") fun observeSubjects():Flow<List<SubjectEntity>>
    @Query("SELECT * FROM subjects WHERE id=:id") fun observeSubject(id:String):Flow<SubjectEntity?>
    @Query("SELECT * FROM lessons WHERE subjectId=:subjectId AND (:year=0 OR year=:year) ORDER BY year,sortOrder") fun observeLessons(subjectId:String,year:Int=0):Flow<List<LessonEntity>>
    @Query("SELECT * FROM lessons WHERE id=:id") fun observeLesson(id:String):Flow<LessonEntity?>
    @Query("SELECT * FROM lessons WHERE id=:id") suspend fun lesson(id:String):LessonEntity?
    @Query("SELECT * FROM lessons ORDER BY sortOrder LIMIT 1") suspend fun firstLesson():LessonEntity?
    @Query("SELECT * FROM lessons WHERE title LIKE '%'||:query||'%' OR explanation LIKE '%'||:query||'%' OR keywordsJson LIKE '%'||:query||'%' ORDER BY title LIMIT 100") fun searchLessons(query:String):Flow<List<LessonEntity>>
    @Query("SELECT * FROM lesson_progress") fun observeAllProgress():Flow<List<LessonProgressEntity>>
    @Query("SELECT * FROM lesson_progress WHERE lessonId=:lessonId") fun observeProgress(lessonId:String):Flow<LessonProgressEntity?>
    @Query("SELECT * FROM lesson_progress ORDER BY lastOpenedAt DESC LIMIT 1") fun observeLatestProgress():Flow<LessonProgressEntity?>
    @Upsert suspend fun upsertProgress(value:LessonProgressEntity)

    @Query("SELECT * FROM quiz_questions WHERE (:lessonId='' OR lessonId=:lessonId) ORDER BY id LIMIT :limit") suspend fun quiz(lessonId:String="",limit:Int=20):List<QuizQuestionEntity>
    @Query("SELECT * FROM quiz_questions WHERE subjectId=:subjectId ORDER BY RANDOM() LIMIT :limit") suspend fun quizForSubject(subjectId:String,limit:Int=20):List<QuizQuestionEntity>
    @Insert suspend fun insertAttempt(value:QuizAttemptEntity)
    @Query("SELECT * FROM quiz_attempts ORDER BY attemptedAt DESC") fun observeAttempts():Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM exercises ORDER BY id LIMIT 200") fun observeExercises():Flow<List<ExerciseEntity>>
    @Query("SELECT * FROM exercises WHERE id=:id") fun observeExercise(id:String):Flow<ExerciseEntity?>
    @Query("SELECT * FROM exercise_progress") fun observeExerciseProgress():Flow<List<ExerciseProgressEntity>>
    @Upsert suspend fun upsertExerciseProgress(value:ExerciseProgressEntity)

    @Query("SELECT * FROM flashcards ORDER BY id LIMIT 500") fun observeFlashcards():Flow<List<FlashcardEntity>>
    @Query("SELECT * FROM flashcard_progress") fun observeFlashcardProgress():Flow<List<FlashcardProgressEntity>>
    @Upsert suspend fun upsertFlashcardProgress(value:FlashcardProgressEntity)

    @Query("SELECT * FROM glossary WHERE italianTerm LIKE '%'||:query||'%' OR englishTerm LIKE '%'||:query||'%' OR definition LIKE '%'||:query||'%' ORDER BY italianTerm LIMIT 300") fun observeGlossary(query:String):Flow<List<GlossaryEntity>>
    @Query("SELECT * FROM videos ORDER BY subjectId,title") fun observeVideos():Flow<List<VideoEntity>>
    @Update suspend fun updateVideo(value:VideoEntity)
    @Query("SELECT * FROM concept_maps ORDER BY title LIMIT 250") fun observeMaps():Flow<List<ConceptMapEntity>>
    @Query("SELECT * FROM concept_maps WHERE id=:id") fun observeMap(id:String):Flow<ConceptMapEntity?>
    @Query("SELECT * FROM labs ORDER BY title LIMIT 250") fun observeLabs():Flow<List<LabEntity>>
    @Query("SELECT * FROM labs WHERE id=:id") fun observeLab(id:String):Flow<LabEntity?>
    @Query("SELECT * FROM technical_tools ORDER BY category,name") fun observeTools():Flow<List<TechnicalToolEntity>>
    @Query("SELECT * FROM notes ORDER BY important DESC,updatedAt DESC") fun observeNotes():Flow<List<NoteEntity>>
    @Upsert suspend fun upsertNote(value:NoteEntity)
    @Delete suspend fun deleteNote(value:NoteEntity)
    @Query("SELECT * FROM study_plan ORDER BY date") fun observeStudyPlan():Flow<List<StudyPlanEntity>>
    @Upsert suspend fun upsertStudyPlan(value:StudyPlanEntity)
    @Query("DELETE FROM study_plan WHERE id=:id") suspend fun deleteStudyPlan(id:String)
    @Insert suspend fun insertCalculation(value:CalculationHistoryEntity)
    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC LIMIT 100") fun observeCalculationHistory():Flow<List<CalculationHistoryEntity>>

    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertSubjects(values:List<SubjectEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertLessons(values:List<LessonEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertQuiz(values:List<QuizQuestionEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertExercises(values:List<ExerciseEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertFlashcards(values:List<FlashcardEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertGlossary(values:List<GlossaryEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertVideos(values:List<VideoEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertMaps(values:List<ConceptMapEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertLabs(values:List<LabEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertTools(values:List<TechnicalToolEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertNotes(values:List<NoteEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertStudyPlan(values:List<StudyPlanEntity>)

    @Query("SELECT * FROM lesson_progress") suspend fun allProgress():List<LessonProgressEntity>
    @Query("SELECT * FROM quiz_attempts") suspend fun allAttempts():List<QuizAttemptEntity>
    @Query("SELECT * FROM exercise_progress") suspend fun allExerciseProgress():List<ExerciseProgressEntity>
    @Query("SELECT * FROM flashcard_progress") suspend fun allFlashcardProgress():List<FlashcardProgressEntity>
    @Query("SELECT * FROM notes") suspend fun allNotes():List<NoteEntity>
    @Query("SELECT * FROM study_plan") suspend fun allPlan():List<StudyPlanEntity>
    @Query("DELETE FROM lesson_progress") suspend fun clearProgress()
    @Query("DELETE FROM quiz_attempts") suspend fun clearAttempts()
    @Query("DELETE FROM exercise_progress") suspend fun clearExerciseProgress()
    @Query("DELETE FROM flashcard_progress") suspend fun clearFlashcardProgress()
    @Query("DELETE FROM notes") suspend fun clearNotes()
    @Query("DELETE FROM study_plan") suspend fun clearPlan()
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun restoreProgress(values:List<LessonProgressEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun restoreAttempts(values:List<QuizAttemptEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun restoreExerciseProgress(values:List<ExerciseProgressEntity>)
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun restoreFlashcardProgress(values:List<FlashcardProgressEntity>)
}
