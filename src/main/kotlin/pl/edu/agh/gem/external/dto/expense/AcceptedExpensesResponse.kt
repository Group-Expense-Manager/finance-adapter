package pl.edu.agh.gem.external.dto.expense

import pl.edu.agh.gem.external.dto.payment.AmountDto
import pl.edu.agh.gem.external.dto.payment.FxDataDto
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
    val amount: AmountDto,
    val fxData: FxDataDto?,
    val participants: List<AcceptedExpenseParticipantDto>,
    val expenseDate: Instant,
) {
    fun toDomain() = AcceptedExpense(
        creatorId = creatorId,
        title = title,
        amount = amount.toDomain(),
        fxData = fxData?.toDomain(),
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
