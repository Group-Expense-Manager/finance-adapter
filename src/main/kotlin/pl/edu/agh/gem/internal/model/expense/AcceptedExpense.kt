package pl.edu.agh.gem.internal.model.expense

import pl.edu.agh.gem.internal.model.finance.balance.Balance
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
    fun toBalanceList(): List<Balance> {
        val multiplier = fxData?.exchangeRate ?: BigDecimal.ONE

        val participantsBalanceElements = participants.map {
            Balance(
                it.participantId,
                it.participantCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().negate(),
            )
        }
        val creatorCost = participantsBalanceElements.sumOf { it.value }.negate().stripTrailingZeros()
        val creatorBalanceElement = Balance(creatorId, creatorCost)
        return participantsBalanceElements + creatorBalanceElement
    }
}

data class AcceptedExpenseParticipant(
    val participantId: String,
    val participantCost: BigDecimal,
)
