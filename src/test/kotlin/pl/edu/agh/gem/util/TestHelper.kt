package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.expense.AcceptedExpenseDto
import pl.edu.agh.gem.external.dto.expense.AcceptedExpenseParticipantDto
import pl.edu.agh.gem.external.dto.expense.AcceptedExpensesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivityDto
import pl.edu.agh.gem.external.dto.finance.BalancesResponse
import pl.edu.agh.gem.external.dto.finance.CurrencyBalancesDto
import pl.edu.agh.gem.external.dto.finance.UserBalanceDto
import pl.edu.agh.gem.external.dto.group.CurrencyDTO
import pl.edu.agh.gem.external.dto.group.GroupDTO
import pl.edu.agh.gem.external.dto.group.GroupResponse
import pl.edu.agh.gem.external.dto.group.MemberDTO
import pl.edu.agh.gem.external.dto.group.UserGroupsResponse
import pl.edu.agh.gem.external.dto.payment.AcceptedPaymentDto
import pl.edu.agh.gem.external.dto.payment.AcceptedPaymentsResponse
import pl.edu.agh.gem.external.dto.payment.AmountDto
import pl.edu.agh.gem.external.dto.payment.FxDataDto
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivityDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembers
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.expense.AcceptedExpense
import pl.edu.agh.gem.internal.model.expense.AcceptedExpenseParticipant
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
import pl.edu.agh.gem.internal.model.group.Currencies
import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.internal.model.group.GroupData
import pl.edu.agh.gem.internal.model.payment.AcceptedPayment
import pl.edu.agh.gem.internal.model.payment.Amount
import pl.edu.agh.gem.internal.model.payment.FxData
import pl.edu.agh.gem.model.GroupMembers
import pl.edu.agh.gem.util.DummyData.ACTIVITY_TITLE
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
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

fun createAcceptedExpensesResponse(
    groupId: String = GROUP_ID,
    expenses: List<AcceptedExpenseDto> = listOf(
        createAcceptedExpenseDto(creatorId = USER_ID),
    ),
) = AcceptedExpensesResponse(
    groupId = groupId,
    expenses = expenses,
)

fun createAcceptedExpenseDto(
    creatorId: String = USER_ID,
    title: String = "Some title",
    totalCost: BigDecimal = BigDecimal.valueOf(4L),
    baseCurrency: String = CURRENCY_1,
    targetCurrency: String? = CURRENCY_2,
    exchangeRate: BigDecimal? = null,
    participants: List<AcceptedExpenseParticipantDto> = listOf(
        createAcceptedExpenseParticipantDto(participantId = OTHER_USER_ID),
        createAcceptedExpenseParticipantDto(participantId = ANOTHER_USER_ID),
    ),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = AcceptedExpenseDto(
    creatorId = creatorId,
    title = title,
    totalCost = totalCost,
    baseCurrency = baseCurrency,
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
    participants = participants,
    expenseDate = expenseDate,
)

fun createAcceptedExpenseParticipantDto(
    participantId: String = OTHER_USER_ID,
    participantCost: BigDecimal = "2".toBigDecimal(),
) = AcceptedExpenseParticipantDto(
    participantId = participantId,
    participantCost = participantCost,
)

fun createAcceptedExpense(
    creatorId: String = USER_ID,
    title: String = "Some title",
    totalCost: BigDecimal = BigDecimal.valueOf(4L),
    baseCurrency: String = CURRENCY_1,
    targetCurrency: String? = CURRENCY_2,
    exchangeRate: BigDecimal? = null,
    participants: List<AcceptedExpenseParticipant> = listOf(
        createAcceptedExpenseParticipant(participantId = OTHER_USER_ID),
        createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID),
    ),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = AcceptedExpense(
    creatorId = creatorId,
    title = title,
    totalCost = totalCost,
    baseCurrency = baseCurrency,
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
    participants = participants,
    expenseDate = expenseDate,
)

fun createAcceptedExpenseParticipant(
    participantId: String = OTHER_USER_ID,
    participantCost: BigDecimal = "2".toBigDecimal(),
) = AcceptedExpenseParticipant(
    participantId = participantId,
    participantCost = participantCost,
)

fun createFxDataDto(
    targetCurrency: String = CURRENCY_2,
    exchangeRate: BigDecimal = "3.24".toBigDecimal(),
) = FxDataDto(
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
)

fun createFxData(
    targetCurrency: String = CURRENCY_2,
    exchangeRate: BigDecimal = "3.24".toBigDecimal(),
) = FxData(
    targetCurrency = targetCurrency,
    exchangeRate = exchangeRate,
)

fun createAmount(
    value: BigDecimal = "10".toBigDecimal(),
    currency: String = CURRENCY_1,
) = Amount(
    value = value,
    currency = currency,
)

fun createAcceptedPaymentDto(
    creatorId: String = USER_ID,
    recipientId: String = OTHER_USER_ID,
    title: String = "Some title",
    amount: AmountDto = createAmountDto(),
    fxData: FxDataDto? = createFxDataDto(),
    date: Instant = Instant.ofEpochMilli(0L),
) = AcceptedPaymentDto(
    creatorId = creatorId,
    recipientId = recipientId,
    title = title,
    amount = amount,
    fxData = fxData,
    date = date,
)

fun createAcceptedPaymentsResponse(
    groupId: String = GROUP_ID,
    payments: List<AcceptedPaymentDto> = listOf(
        createAcceptedPaymentDto(creatorId = USER_ID, recipientId = OTHER_USER_ID),
        createAcceptedPaymentDto(creatorId = OTHER_USER_ID, recipientId = USER_ID),

    ),
) = AcceptedPaymentsResponse(
    groupId = groupId,
    payments = payments,
)

fun createAcceptedPayment(
    creatorId: String = USER_ID,
    recipientId: String = OTHER_USER_ID,
    title: String = "Some title",
    amount: Amount = createAmount(),
    fxData: FxData? = createFxData(),
    date: Instant = Instant.ofEpochMilli(0L),
) = AcceptedPayment(
    creatorId = creatorId,
    recipientId = recipientId,
    title = title,
    amount = amount,
    fxData = fxData,
    date = date,
)

fun createGroupResponse(
    members: List<MemberDTO> = listOf(USER_ID, OTHER_USER_ID).map { MemberDTO(it) },
    groupCurrencies: List<CurrencyDTO> = listOf(CURRENCY_1, CURRENCY_2).map { CurrencyDTO(it) },
) = GroupResponse(
    members = members,
    groupCurrencies = groupCurrencies,
)

fun createGroupData(
    members: GroupMembers = createGroupMembers(USER_ID, OTHER_USER_ID),
    currencies: Currencies = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
) = GroupData(
    members = members,
    currencies = currencies,
)

fun createCurrenciesDTO(
    vararg currency: String = arrayOf(CURRENCY_1, CURRENCY_2),
) = currency.map { CurrencyDTO(it) }

fun createMembersDTO(
    vararg members: String = arrayOf(USER_ID, OTHER_USER_ID),
) = members.map { MemberDTO(it) }

fun createCurrencyBalanceDto(
    currency: String = CURRENCY_1,
    userBalances: List<UserBalanceDto> = listOf(
        createUserBalanceDto(userId = USER_ID, "5".toBigDecimal()),
        createUserBalanceDto(userId = OTHER_USER_ID, "-2".toBigDecimal()),
        createUserBalanceDto(userId = ANOTHER_USER_ID, "-3".toBigDecimal()),

    ),
) = CurrencyBalancesDto(
    currency = currency,
    userBalances = userBalances,
)

fun createBalancesResponse(
    groupId: String = GROUP_ID,
    balances: List<CurrencyBalancesDto> = listOf(
        createCurrencyBalanceDto(currency = CURRENCY_1),
        createCurrencyBalanceDto(currency = CURRENCY_2),
    ),
) = BalancesResponse(
    groupId = groupId,
    balances = balances,
)

fun createBalances() = mapOf(
    CURRENCY_1 to mapOf(USER_ID to "1".toBigDecimal(), OTHER_USER_ID to "-1".toBigDecimal()),
    CURRENCY_2 to mapOf(USER_ID to "2".toBigDecimal(), OTHER_USER_ID to "-2".toBigDecimal()),
)

fun createUserBalanceDto(
    userId: String = USER_ID,
    balance: BigDecimal = "3".toBigDecimal(),
) = UserBalanceDto(
    userId = userId,
    balance = balance,
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

    const val ANOTHER_USER_ID = "anotherUserId"
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
