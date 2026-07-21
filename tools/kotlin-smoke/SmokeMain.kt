import it.lucamichetti.mechlabacademy.domain.ProgressCalculator
import it.lucamichetti.mechlabacademy.domain.SpacedRepetition
import it.lucamichetti.mechlabacademy.domain.TechnicalCalculators

fun main() {
    check(TechnicalCalculators.calculate("work", 10.0, 20.0).value == 200.0)
    check(ProgressCalculator.calculate(10, 5, 0, emptySet()).percent == 50)
    check(SpacedRepetition.review(5, 3, 2.5, 2, 0).intervalDays > 3)
    println("Pure Kotlin domain smoke tests: OK")
}
