package it.lucamichetti.mechlabacademy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyLogicTest {
    @Test
    fun progressIsBounded() {
        assertEquals(50, ProgressCalculator.calculate(10, 5, 100, emptySet()).percent)
        assertEquals(0, ProgressCalculator.calculate(0, 8, -1, emptySet()).percent)
    }

    @Test
    fun spacedRepetitionSchedules() {
        val result = SpacedRepetition.review(5, 3, 2.5, 2, 0)
        assertTrue(result.intervalDays > 3)
        assertTrue(result.dueAt > 0)
    }

    @Test
    fun quizScore() {
        assertEquals(7, QuizScoring.score(7, 10))
        assertEquals(0, QuizScoring.score(5, 0))
    }

    @Test
    fun calculators() {
        assertEquals(1_000.0, TechnicalCalculators.calculate("unit_length", 1.0, 0.0).value, 0.001)
        assertEquals(200.0, TechnicalCalculators.calculate("work", 10.0, 20.0).value, 0.001)
    }
}
