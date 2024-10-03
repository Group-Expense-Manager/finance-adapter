package pl.edu.agh.gem.external.dto.expense

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class ExpenseManagerActivitiesResponse(
    val groupId: String,
    val expenses: List<ExpenseManagerActivityDto>,
) {
    fun toDomain() = expenses.map { it.toActivity() }
}

data class ExpenseManagerActivityDto(
    val expenseId: String,
    val creatorId: String,
    val title: String,
    val totalCost: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val exchangeRate: BigDecimal?,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val expenseDate: Instant,
) {
    fun toActivity() = Activity(
        activityId = expenseId,
        type = EXPENSE,
        creatorId = creatorId,
        title = title,
        value = totalCost.multiply(exchangeRate ?: BigDecimal.ONE).setScale(2, RoundingMode.DOWN).stripTrailingZeros(),
        currency = targetCurrency ?: baseCurrency,
        status = status,
        participantIds = participantIds,
        date = expenseDate,
    )
}
