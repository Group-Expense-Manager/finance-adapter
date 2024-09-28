package pl.edu.agh.gem.external.dto.expense

import pl.edu.agh.gem.internal.model.expense.AcceptedExpense
import pl.edu.agh.gem.internal.model.expense.AcceptedExpenseParticipant
import java.math.BigDecimal
import java.time.Instant

data class AcceptedExpensesResponse(
    val groupId: String,
    val expenses: List<AcceptedExpenseDto>,
) {
    fun toDomain() = expenses.map { it.toDomain() }
}

data class AcceptedExpenseDto(
    val creatorId: String,
    val title: String,
    val totalCost: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val exchangeRate: BigDecimal?,
    val participants: List<AcceptedExpenseParticipantDto>,
    val expenseDate: Instant,
) {
    fun toDomain() = AcceptedExpense(
        creatorId = creatorId,
        title = title,
        totalCost = totalCost,
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        exchangeRate = exchangeRate,
        participants = participants.map { it.toDomain() },
        expenseDate = expenseDate,
    )
}
data class AcceptedExpenseParticipantDto(
    val participantId: String,
    val participantCost: BigDecimal,
) {
    fun toDomain() = AcceptedExpenseParticipant(
        participantId = participantId,
        participantCost = participantCost,
    )
}
