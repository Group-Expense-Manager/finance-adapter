package pl.edu.agh.gem.external.dto.expense

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import java.math.BigDecimal
import java.time.Instant

data class ExpenseManagerActivitiesResponse(
    val groupId: String,
    val expenses: List<ExpenseManagerActivityDTO>,
) {
    fun toDomain() = expenses.map { it.toActivity() }
}

data class ExpenseManagerActivityDTO(
    val expenseId: String,
    val creatorId: String,
    val title: String,
    val cost: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val expenseDate: Instant,
) {
    fun toActivity() = Activity(
        activityId = expenseId,
        type = EXPENSE,
        creatorId = creatorId,
        title = title,
        sum = cost,
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        status = status,
        participantIds = participantIds,
        activityDate = expenseDate,
    )
}
