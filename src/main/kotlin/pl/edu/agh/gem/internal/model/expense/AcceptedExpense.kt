package pl.edu.agh.gem.internal.model.expense

import pl.edu.agh.gem.internal.model.payment.Amount
import pl.edu.agh.gem.internal.model.payment.FxData
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class AcceptedExpense(
    val creatorId: String,
    val title: String,
    val amount: Amount,
    val fxData: FxData?,
    val participants: List<AcceptedExpenseParticipant>,
    val expenseDate: Instant,
) {
    fun toBalanceElements(): List<Triple<String, String, BigDecimal>> {
        val currency = fxData?.targetCurrency ?: amount.currency
        val multiplier = fxData?.exchangeRate ?: BigDecimal.ONE

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
