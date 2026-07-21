package it.lucamichetti.mechlabacademy.domain

import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

data class ProgressSummary(
    val total: Int,
    val completed: Int,
    val percent: Int,
    val studiedSeconds: Long,
    val streak: Int,
)

object ProgressCalculator {
    fun calculate(
        total: Int,
        completed: Int,
        studiedSeconds: Long,
        activeEpochDays: Set<Long>,
        nowMillis: Long = System.currentTimeMillis(),
    ): ProgressSummary {
        val normalizedTotal = max(0, total)
        val normalizedCompleted = completed.coerceIn(0, normalizedTotal)
        val percent = if (normalizedTotal == 0) {
            0
        } else {
            (normalizedCompleted * 100.0 / normalizedTotal).roundToInt().coerceIn(0, 100)
        }
        val today = nowMillis / MILLIS_PER_DAY
        var streak = 0
        var day = today
        while (day in activeEpochDays) {
            streak++
            day--
        }
        return ProgressSummary(
            total = normalizedTotal,
            completed = normalizedCompleted,
            percent = percent,
            studiedSeconds = max(0, studiedSeconds),
            streak = streak,
        )
    }
}

data class ReviewResult(
    val state: String,
    val intervalDays: Int,
    val ease: Double,
    val repetitions: Int,
    val dueAt: Long,
)

object SpacedRepetition {
    /**
     * Variante locale e leggibile dell'algoritmo SM-2. La qualità è compresa tra 0 e 5.
     * Il risultato non dipende da server o servizi esterni.
     */
    fun review(
        quality: Int,
        intervalDays: Int,
        ease: Double,
        repetitions: Int,
        now: Long = System.currentTimeMillis(),
    ): ReviewResult {
        val normalizedQuality = quality.coerceIn(0, 5)
        if (normalizedQuality < 3) {
            return ReviewResult(
                state = "IN_LEARNING",
                intervalDays = 1,
                ease = (ease - 0.2).coerceAtLeast(1.3),
                repetitions = 0,
                dueAt = now + MILLIS_PER_DAY,
            )
        }

        val newRepetitions = repetitions + 1
        val newInterval = when (newRepetitions) {
            1 -> 1
            2 -> 3
            else -> (intervalDays.coerceAtLeast(3) * ease)
                .roundToInt()
                .coerceAtLeast(intervalDays + 1)
        }
        val newEase = (
            ease + (0.1 - (5 - normalizedQuality) * (0.08 + (5 - normalizedQuality) * 0.02))
            ).coerceIn(1.3, 3.0)
        val state = when {
            newRepetitions >= 5 -> "ACQUIRED"
            newRepetitions >= 3 -> "ALMOST_ACQUIRED"
            else -> "IN_LEARNING"
        }
        return ReviewResult(
            state = state,
            intervalDays = newInterval,
            ease = newEase,
            repetitions = newRepetitions,
            dueAt = now + newInterval * MILLIS_PER_DAY,
        )
    }
}

object QuizScoring {
    fun score(correct: Int, total: Int, pointsPerQuestion: Int = 1): Int {
        if (total <= 0 || pointsPerQuestion <= 0) return 0
        return correct.coerceIn(0, total) * pointsPerQuestion
    }
}

data class CalculationResult(
    val value: Double,
    val unit: String,
    val explanation: String,
)

object TechnicalCalculators {
    /**
     * A, B e C assumono le unità dichiarate nel testo di ciascuno strumento.
     * I calcoli sono didattici e non sostituiscono verifiche tecniche professionali.
     */
    fun calculate(key: String, a: Double, b: Double, c: Double = 0.0): CalculationResult =
        when (key) {
            "unit_length" -> CalculationResult(a * 1_000.0, "mm", "Conversione applicata: metri × 1.000")
            "unit_pressure" -> CalculationResult(a / 100_000.0, "bar", "Conversione applicata: pascal ÷ 100.000")
            "cutting_rpm" -> positive(a, b) {
                CalculationResult(1_000.0 * a / (PI * b), "giri/min", "n = 1000·Vc/(π·D)")
            }
            "feed_rate" -> positive(a, b, c) {
                CalculationResult(a * b * c, "mm/min", "Vf = fz·z·n")
            }
            "torque_power" -> positive(a, b) {
                CalculationResult(9_550.0 * a / b, "N·m", "T = 9550·P/n, con P in kW")
            }
            "power_torque" -> positive(a, b) {
                CalculationResult(a * 2.0 * PI * b / 60_000.0, "kW", "P = T·2πn/60, convertita in kW")
            }
            "efficiency" -> positive(b) {
                CalculationResult(a / b * 100.0, "%", "η = uscita/ingresso × 100")
            }
            "gear_ratio" -> positive(b) {
                CalculationResult(a / b, "", "i = valore condotto / valore motore")
            }
            "ohm_voltage" -> CalculationResult(a * b, "V", "V = R·I")
            "ohm_current" -> positive(b) {
                CalculationResult(a / b, "A", "I = V/R")
            }
            "pressure" -> positive(b) {
                CalculationResult(a / b, "Pa", "p = F/A con unità SI")
            }
            "force_pressure" -> CalculationResult(a * b, "N", "F = p·A con unità SI")
            "flow" -> CalculationResult(a * b, "m³/s", "Q = A·v")
            "work" -> CalculationResult(a * b, "J", "W = F·s")
            "kinetic" -> CalculationResult(0.5 * a * b.pow(2), "J", "Ek = 1/2·m·v²")
            "thermal_expansion" -> CalculationResult(a * b * c, "m", "ΔL = α·L₀·ΔT")
            "interpolation" -> CalculationResult(a + (b - a) * c, "", "Interpolazione normalizzata: y = a +(b-a)·c, 0≤c≤1")
            "circle_area" -> positive(a) {
                CalculationResult(PI * a.pow(2) / 4.0, "unità²", "A = πD²/4")
            }
            "cylinder_volume" -> positive(a, b) {
                CalculationResult(PI * a.pow(2) * b / 4.0, "unità³", "V = πD²L/4")
            }
            "beam_stress" -> positive(b) {
                CalculationResult(a / b, "Pa", "σ = M/W, usando unità SI coerenti")
            }
            "axial_stress" -> positive(b) {
                CalculationResult(a / b, "Pa", "σ = F/A")
            }
            "hydraulic_power" -> CalculationResult(a * b, "W", "P = p·Q con p in Pa e Q in m³/s")
            "heat_conduction" -> positive(c) {
                CalculationResult(a * b / c, "W/K o W", "Forma ridotta λ·A·ΔT/L: C rappresenta L; includere ΔT in B se richiesto")
            }
            "break_even" -> positive(b - c) {
                CalculationResult(a / (b - c), "unità", "Qbe = CF/(p-CVu)")
            }
            else -> {
                if (key.startsWith("reference_")) {
                    CalculationResult(Double.NaN, "", "Voce di formulario: consultare formula, simboli e lezione associata")
                } else {
                    error("Calcolatore non implementato: $key")
                }
            }
        }

    private inline fun positive(vararg values: Double, block: () -> CalculationResult): CalculationResult {
        require(values.all { it > 0.0 && it.isFinite() }) {
            "I valori richiesti devono essere positivi e finiti"
        }
        return block()
    }
}

private const val MILLIS_PER_DAY = 86_400_000L
