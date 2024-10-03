package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivityDto
import pl.edu.agh.gem.external.dto.group.GroupDTO
import pl.edu.agh.gem.external.dto.group.UserGroupsResponse
import pl.edu.agh.gem.external.dto.payment.AmountDto
import pl.edu.agh.gem.external.dto.payment.FxDataDto
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivityDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.util.DummyData.ACTIVITY_TITLE
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.DummyData.EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.VALUE
import java.math.BigDecimal
import java.time.Instant

fun createUserGroupsResponse(
    vararg groups: String = arrayOf(GROUP_ID, OTHER_GROUP_ID),
) = UserGroupsResponse(groups = groups.map { GroupDTO(it) })

fun createExpenseManagerActivityDto(
    expenseId: String = EXPENSE_ID,
    creatorId: String = USER_ID,
    title: String = ACTIVITY_TITLE,
    totalCost: BigDecimal = VALUE,
    baseCurrency: String = CURRENCY_1,
    targetCurrency: String? = CURRENCY_2,
    exchangeRate: BigDecimal? = "2".toBigDecimal(),
    status: ActivityStatus = PENDING,
    participantIds: List<String> = listOf(OTHER_USER_ID, USER_ID),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = ExpenseManagerActivityDto(
    expenseId = expenseId,
    creatorId = creatorId,
    title = title,
    totalCost = totalCost,
    baseCurrency = baseCurrency,
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
    status = status,
    participantIds = participantIds,
    expenseDate = expenseDate,
)

fun createExpenseManagerActivitiesResponse(
    groupId: String = GROUP_ID,
    vararg expenses: ExpenseManagerActivityDto = arrayOf(
        createExpenseManagerActivityDto(expenseId = EXPENSE_ID),
        createExpenseManagerActivityDto(expenseId = OTHER_EXPENSE_ID),
    ),
) = ExpenseManagerActivitiesResponse(
    groupId = groupId,
    expenses = expenses.toList(),
)

fun createAmountDto(
    value: BigDecimal = "4".toBigDecimal(),
    currency: String = CURRENCY_1,
) = AmountDto(
    value = value,
    currency = currency,
)

fun createFxDataDto(
    targetCurrency: String = CURRENCY_2,
    exchangeRate: BigDecimal = "2".toBigDecimal(),
) = FxDataDto(
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
)

fun createPaymentManagerActivityDto(
    paymentId: String = PAYMENT_ID,
    creatorId: String = USER_ID,
    recipientId: String = OTHER_USER_ID,
    title: String = ACTIVITY_TITLE,
    amount: AmountDto = createAmountDto(),
    fxData: FxDataDto? = createFxDataDto(),
    status: ActivityStatus = PENDING,
    date: Instant = Instant.ofEpochMilli(0L),
) = PaymentManagerActivityDto(
    paymentId = paymentId,
    creatorId = creatorId,
    recipientId = recipientId,
    title = title,
    amount = amount,
    fxData = fxData,
    status = status,
    date = date,
)

fun createPaymentManagerActivitiesResponse(
    groupId: String = GROUP_ID,
    vararg expenses: PaymentManagerActivityDto = arrayOf(
        createPaymentManagerActivityDto(paymentId = PAYMENT_ID),
        createPaymentManagerActivityDto(paymentId = OTHER_PAYMENT_ID),
    ),
) = PaymentManagerActivitiesResponse(
    groupId = groupId,
    payments = expenses.toList(),
)

fun createActivity(
    activityId: String = EXPENSE_ID,
    type: ActivityType = EXPENSE,
    creatorId: String = USER_ID,
    title: String = ACTIVITY_TITLE,
    value: BigDecimal = VALUE,
    currency: String = CURRENCY_1,
    status: ActivityStatus = PENDING,
    participantIds: List<String> = listOf(OTHER_USER_ID, USER_ID),
    date: Instant = Instant.ofEpochMilli(0L),
) = Activity(
    activityId = activityId,
    type = type,
    creatorId = creatorId,
    title = title,
    value = value,
    currency = currency,
    status = status,
    participantIds = participantIds,
    date = date,
)

fun createFilterOptions(
    title: String? = null,
    status: ActivityStatus? = null,
    creatorId: String? = null,
    type: ActivityType? = null,
    sortedBy: SortedBy = DATE,
    sortOrder: SortOrder = ASCENDING,
) = FilterOptions(
    title = title,
    status = status,
    creatorId = creatorId,
    type = type,
    sortedBy = sortedBy,
    sortOrder = sortOrder,
)

fun createClientFilterOptions(
    title: String? = null,
    status: ActivityStatus? = null,
    creatorId: String? = null,
    sortedBy: SortedBy = DATE,
    sortOrder: SortOrder = ASCENDING,
) = ClientFilterOptions(
    title = title,
    status = status,
    creatorId = creatorId,
    sortedBy = sortedBy,
    sortOrder = sortOrder,

)

object DummyData {
    const val EXPENSE_ID = "expenseId"
    const val OTHER_EXPENSE_ID = "otherExpenseId"
    const val PAYMENT_ID = "paymentId"
    const val OTHER_PAYMENT_ID = "otherPaymentId"

    const val ACTIVITY_TITLE = "activityTitle"
    val VALUE = 12.toBigDecimal()
    const val CURRENCY_1 = "PLN"
    const val CURRENCY_2 = "EUR"
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
