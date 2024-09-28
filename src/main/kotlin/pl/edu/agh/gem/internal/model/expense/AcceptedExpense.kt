package pl.edu.agh.gem.internal.model.expense

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class AcceptedExpense(
    val creatorId: String,
    val title: String,
    val totalCost: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val exchangeRate: BigDecimal?,
    val participants: List<AcceptedExpenseParticipant>,
    val expenseDate: Instant,
) {
    fun toBalanceElements(): List<Triple<String, String, BigDecimal>> {
        val currency = targetCurrency ?: baseCurrency
        val multiplier = exchangeRate ?: BigDecimal.ONE

        val participantsBalanceElements = participants.map {
            Triple(
                currency,
                it.participantId,
                it.participantCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP).negate().stripTrailingZeros(),
            )
        }
        val creatorCost = participantsBalanceElements.sumOf { it.third }.negate().stripTrailingZeros()
        val creatorBalanceElement = Triple(currency, creatorId, creatorCost)
        return participantsBalanceElements + creatorBalanceElement
    }
}

data class AcceptedExpenseParticipant(
    val participantId: String,
    val participantCost: BigDecimal,
)
