package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.expense.AcceptedExpenseDto
import pl.edu.agh.gem.external.dto.expense.AcceptedExpenseParticipantDto
import pl.edu.agh.gem.external.dto.expense.AcceptedExpensesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivityDto
import pl.edu.agh.gem.external.dto.group.CurrencyDto
import pl.edu.agh.gem.external.dto.group.GroupDTO
import pl.edu.agh.gem.external.dto.group.GroupResponse
import pl.edu.agh.gem.external.dto.group.MemberDto
import pl.edu.agh.gem.external.dto.group.UserGroupsResponse
import pl.edu.agh.gem.external.dto.payment.AcceptedPaymentDto
import pl.edu.agh.gem.external.dto.payment.AcceptedPaymentsResponse
import pl.edu.agh.gem.external.dto.payment.AmountDto
import pl.edu.agh.gem.external.dto.payment.FxDataDto
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivitiesResponse
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivityDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.job.ReconciliationJobState
import pl.edu.agh.gem.internal.job.ReconciliationJobState.STARTING
import pl.edu.agh.gem.internal.model.expense.AcceptedExpense
import pl.edu.agh.gem.internal.model.expense.AcceptedExpenseParticipant
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.finance.settlement.SettlementStatus
import pl.edu.agh.gem.internal.model.finance.settlement.Settlements
import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.internal.model.group.GroupData
import pl.edu.agh.gem.internal.model.payment.AcceptedPayment
import pl.edu.agh.gem.internal.model.payment.Amount
import pl.edu.agh.gem.internal.model.payment.FxData
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.model.GroupMember
import pl.edu.agh.gem.util.DummyData.ACTIVITY_TITLE
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.DummyData.EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.VALUE
import pl.edu.agh.gem.util.DummyData.solverTestData
import java.math.BigDecimal
import java.time.Instant

fun createUserGroupsResponse(
    vararg groups: String = arrayOf(GROUP_ID, OTHER_GROUP_ID),
) = UserGroupsResponse(groups = groups.map { GroupDTO(it) })

fun createExpenseManagerActivityDto(
    expenseId: String = EXPENSE_ID,
    creatorId: String = USER_ID,
    title: String = ACTIVITY_TITLE,
    amount: AmountDto = createAmountDto(),
    fxData: FxDataDto? = createFxDataDto(),
    status: ActivityStatus = PENDING,
    participantIds: List<String> = listOf(OTHER_USER_ID, USER_ID),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = ExpenseManagerActivityDto(
    expenseId = expenseId,
    creatorId = creatorId,
    title = title,
    amount = amount,
    fxData = fxData,
    status = status,
    participantIds = participantIds,
    expenseDate = expenseDate,
)

fun createExpenseManagerActivitiesResponse(
    groupId: String = GROUP_ID,
    expenses: List<ExpenseManagerActivityDto> = listOf(
        createExpenseManagerActivityDto(expenseId = EXPENSE_ID),
        createExpenseManagerActivityDto(expenseId = OTHER_EXPENSE_ID),
    ),
) = ExpenseManagerActivitiesResponse(
    groupId = groupId,
    expenses = expenses,
)

fun createAmountDto(
    value: BigDecimal = "4".toBigDecimal(),
    currency: String = CURRENCY_2,
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
    payments: List<PaymentManagerActivityDto> = listOf(
        createPaymentManagerActivityDto(paymentId = PAYMENT_ID),
        createPaymentManagerActivityDto(paymentId = OTHER_PAYMENT_ID),
    ),
) = PaymentManagerActivitiesResponse(
    groupId = groupId,
    payments = payments,
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
    currency: String? = null,
    sortedBy: SortedBy = DATE,
    sortOrder: SortOrder = ASCENDING,
) = FilterOptions(
    title = title,
    status = status,
    creatorId = creatorId,
    type = type,
    currency = currency,
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
    amount: AmountDto = createAmountDto(),
    fxData: FxDataDto? = createFxDataDto(),
    participants: List<AcceptedExpenseParticipantDto> = listOf(
        createAcceptedExpenseParticipantDto(participantId = OTHER_USER_ID),
        createAcceptedExpenseParticipantDto(participantId = ANOTHER_USER_ID),
    ),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = AcceptedExpenseDto(
    creatorId = creatorId,
    title = title,
    amount = amount,
    fxData = fxData,
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
    amount: Amount = createAmount(),
    fxData: FxData? = createFxData(),
    participants: List<AcceptedExpenseParticipant> = listOf(
        createAcceptedExpenseParticipant(participantId = OTHER_USER_ID),
        createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID),
    ),
    expenseDate: Instant = Instant.ofEpochMilli(0L),
) = AcceptedExpense(
    creatorId = creatorId,
    title = title,
    amount = amount,
    fxData = fxData,
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
    targetCurrency: String = CURRENCY_1,
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
        createAcceptedPaymentDto(creatorId = USER_ID, recipientId = OTHER_USER_ID, amount = createAmountDto(value = "10".toBigDecimal())),
        createAcceptedPaymentDto(creatorId = OTHER_USER_ID, recipientId = USER_ID, amount = createAmountDto(value = "5".toBigDecimal())),

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
    members: List<MemberDto> = listOf(USER_ID, OTHER_USER_ID).map { MemberDto(it) },
    groupCurrencies: List<CurrencyDto> = listOf(CURRENCY_1, CURRENCY_2).map { CurrencyDto(it) },
) = GroupResponse(
    members = members,
    groupCurrencies = groupCurrencies,
)

fun createGroupData(
    members: List<GroupMember> = listOf(USER_ID, OTHER_USER_ID).map { GroupMember(it) },
    currencies: List<Currency> = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
) = GroupData(
    members = members,
    currencies = currencies,
)

fun createCurrenciesDTO(
    vararg currency: String = arrayOf(CURRENCY_1, CURRENCY_2),
) = currency.map { CurrencyDto(it) }

fun createMembersDTO(
    vararg members: String = arrayOf(USER_ID, OTHER_USER_ID),
) = members.map { MemberDto(it) }

fun createBalances(
    currency: String = CURRENCY_1,
    groupId: String = GROUP_ID,
    balances: List<Balance> = listOf(
        createBalance(userId = USER_ID, "5".toBigDecimal()),
        createBalance(userId = OTHER_USER_ID, "-2".toBigDecimal()),
        createBalance(userId = ANOTHER_USER_ID, "-3".toBigDecimal()),
    ),
) = Balances(
    groupId = groupId,
    currency = currency,
    users = balances,
)

fun createBalance(
    userId: String = USER_ID,
    value: BigDecimal = "1".toBigDecimal(),
) = Balance(
    userId = userId,
    value = value,
)

fun createSettlements(
    groupId: String = "group1",
    currency: String = "USD",
    status: SettlementStatus = SettlementStatus.PENDING,
    settlements: List<Settlement> = listOf(
        createSettlement(fromUserId = USER_ID, toUserId = OTHER_USER_ID, value = "100".toBigDecimal()),
        createSettlement(fromUserId = USER_ID, toUserId = OTHER_USER_ID, value = "200".toBigDecimal()),
        createSettlement(fromUserId = OTHER_USER_ID, toUserId = USER_ID, value = "-300".toBigDecimal()),
    ),
) = Settlements(
    groupId = groupId,
    currency = currency,
    status = status,
    settlements = settlements,
)

fun createSettlement(
    fromUserId: String = USER_ID,
    toUserId: String = OTHER_USER_ID,
    value: BigDecimal = "100".toBigDecimal(),
) = Settlement(
    fromUserId = fromUserId,
    toUserId = toUserId,
    value = value,
)

fun createReconciliationJob(
    id: String = "exchange-rate-job-id",
    groupId: String = GROUP_ID,
    currency: String = CURRENCY_1,
    state: ReconciliationJobState = STARTING,
    balances: List<Balance> = listOf(),
    settlements: List<Settlement> = listOf(),
    nextProcessAt: Instant = Instant.parse("2023-01-01T00:00:00.00Z"),
    retry: Long = 0,
    canceled: Boolean = false,
) = ReconciliationJob(
    id = id,
    groupId = groupId,
    currency = currency,
    state = state,
    balances = balances,
    settlements = settlements,
    nextProcessAt = nextProcessAt,
    retry = retry,
    canceled = canceled,
)

fun getSolverTestData(maxSize: Long): List<List<Balance>> {
    return solverTestData.filter { it.size <= maxSize }
}

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

    val solverTestData = listOf(
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-462")),
            Balance(userId = "User 1", value = BigDecimal("462")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("248")),
            Balance(userId = "User 1", value = BigDecimal("-183")),
            Balance(userId = "User 2", value = BigDecimal("-65")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-345")),
            Balance(userId = "User 1", value = BigDecimal("96")),
            Balance(userId = "User 2", value = BigDecimal("249")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("1735")),
            Balance(userId = "User 1", value = BigDecimal("-432")),
            Balance(userId = "User 2", value = BigDecimal("-1303")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-1207")),
            Balance(userId = "User 1", value = BigDecimal("578")),
            Balance(userId = "User 2", value = BigDecimal("629")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-999")),
            Balance(userId = "User 1", value = BigDecimal("347")),
            Balance(userId = "User 2", value = BigDecimal("652")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("555")),
            Balance(userId = "User 1", value = BigDecimal("-233")),
            Balance(userId = "User 2", value = BigDecimal("-322")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("0")),
            Balance(userId = "User 1", value = BigDecimal("0")),
            Balance(userId = "User 2", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-1")),
            Balance(userId = "User 1", value = BigDecimal("-1")),
            Balance(userId = "User 2", value = BigDecimal("1")),
            Balance(userId = "User 3", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-34")),
            Balance(userId = "User 1", value = BigDecimal("8")),
            Balance(userId = "User 2", value = BigDecimal("14")),
            Balance(userId = "User 3", value = BigDecimal("6")),
            Balance(userId = "User 4", value = BigDecimal("2")),
            Balance(userId = "User 5", value = BigDecimal("4")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-62")),
            Balance(userId = "User 1", value = BigDecimal("23")),
            Balance(userId = "User 2", value = BigDecimal("14")),
            Balance(userId = "User 3", value = BigDecimal("10")),
            Balance(userId = "User 4", value = BigDecimal("8")),
            Balance(userId = "User 5", value = BigDecimal("4")),
            Balance(userId = "User 6", value = BigDecimal("3")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-78")),
            Balance(userId = "User 1", value = BigDecimal("31")),
            Balance(userId = "User 2", value = BigDecimal("13")),
            Balance(userId = "User 3", value = BigDecimal("12")),
            Balance(userId = "User 4", value = BigDecimal("8")),
            Balance(userId = "User 5", value = BigDecimal("10")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-90")),
            Balance(userId = "User 1", value = BigDecimal("39")),
            Balance(userId = "User 2", value = BigDecimal("20")),
            Balance(userId = "User 3", value = BigDecimal("13")),
            Balance(userId = "User 4", value = BigDecimal("5")),
            Balance(userId = "User 5", value = BigDecimal("7")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("2")),
            Balance(userId = "User 8", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-111")),
            Balance(userId = "User 1", value = BigDecimal("41")),
            Balance(userId = "User 2", value = BigDecimal("29")),
            Balance(userId = "User 3", value = BigDecimal("25")),
            Balance(userId = "User 4", value = BigDecimal("13")),
            Balance(userId = "User 5", value = BigDecimal("3")),
            Balance(userId = "User 6", value = BigDecimal("0")),
            Balance(userId = "User 7", value = BigDecimal("0")),
            Balance(userId = "User 8", value = BigDecimal("0")),
            Balance(userId = "User 9", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-127")),
            Balance(userId = "User 1", value = BigDecimal("45")),
            Balance(userId = "User 2", value = BigDecimal("28")),
            Balance(userId = "User 3", value = BigDecimal("18")),
            Balance(userId = "User 4", value = BigDecimal("22")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("0")),
            Balance(userId = "User 8", value = BigDecimal("0")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-144")),
            Balance(userId = "User 1", value = BigDecimal("50")),
            Balance(userId = "User 2", value = BigDecimal("34")),
            Balance(userId = "User 3", value = BigDecimal("10")),
            Balance(userId = "User 4", value = BigDecimal("35")),
            Balance(userId = "User 5", value = BigDecimal("6")),
            Balance(userId = "User 6", value = BigDecimal("5")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-158")),
            Balance(userId = "User 1", value = BigDecimal("51")),
            Balance(userId = "User 2", value = BigDecimal("42")),
            Balance(userId = "User 3", value = BigDecimal("22")),
            Balance(userId = "User 4", value = BigDecimal("21")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("5")),
            Balance(userId = "User 8", value = BigDecimal("3")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-176")),
            Balance(userId = "User 1", value = BigDecimal("62")),
            Balance(userId = "User 2", value = BigDecimal("42")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("28")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("8")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-190")),
            Balance(userId = "User 1", value = BigDecimal("65")),
            Balance(userId = "User 2", value = BigDecimal("33")),
            Balance(userId = "User 3", value = BigDecimal("66")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("2")),
            Balance(userId = "User 8", value = BigDecimal("0")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
            Balance(userId = "User 14", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-203")),
            Balance(userId = "User 1", value = BigDecimal("68")),
            Balance(userId = "User 2", value = BigDecimal("29")),
            Balance(userId = "User 3", value = BigDecimal("69")),
            Balance(userId = "User 4", value = BigDecimal("15")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("1")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
            Balance(userId = "User 14", value = BigDecimal("0")),
            Balance(userId = "User 15", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-221")),
            Balance(userId = "User 1", value = BigDecimal("70")),
            Balance(userId = "User 2", value = BigDecimal("34")),
            Balance(userId = "User 3", value = BigDecimal("82")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("11")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("4")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
            Balance(userId = "User 14", value = BigDecimal("0")),
            Balance(userId = "User 15", value = BigDecimal("0")),
            Balance(userId = "User 16", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-240")),
            Balance(userId = "User 1", value = BigDecimal("76")),
            Balance(userId = "User 2", value = BigDecimal("38")),
            Balance(userId = "User 3", value = BigDecimal("94")),
            Balance(userId = "User 4", value = BigDecimal("13")),
            Balance(userId = "User 5", value = BigDecimal("9")),
            Balance(userId = "User 6", value = BigDecimal("5")),
            Balance(userId = "User 7", value = BigDecimal("4")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
            Balance(userId = "User 14", value = BigDecimal("0")),
            Balance(userId = "User 15", value = BigDecimal("0")),
            Balance(userId = "User 16", value = BigDecimal("0")),
            Balance(userId = "User 17", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-250")),
            Balance(userId = "User 1", value = BigDecimal("70")),
            Balance(userId = "User 2", value = BigDecimal("35")),
            Balance(userId = "User 3", value = BigDecimal("100")),
            Balance(userId = "User 4", value = BigDecimal("17")),
            Balance(userId = "User 5", value = BigDecimal("13")),
            Balance(userId = "User 6", value = BigDecimal("11")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("0")),
            Balance(userId = "User 10", value = BigDecimal("0")),
            Balance(userId = "User 11", value = BigDecimal("0")),
            Balance(userId = "User 12", value = BigDecimal("0")),
            Balance(userId = "User 13", value = BigDecimal("0")),
            Balance(userId = "User 14", value = BigDecimal("0")),
            Balance(userId = "User 15", value = BigDecimal("0")),
            Balance(userId = "User 16", value = BigDecimal("0")),
            Balance(userId = "User 17", value = BigDecimal("0")),
            Balance(userId = "User 18", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-1")),
            Balance(userId = "User 1", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-4")),
            Balance(userId = "User 1", value = BigDecimal("4")),
            Balance(userId = "User 2", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-7")),
            Balance(userId = "User 1", value = BigDecimal("3")),
            Balance(userId = "User 2", value = BigDecimal("4")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-19")),
            Balance(userId = "User 1", value = BigDecimal("9")),
            Balance(userId = "User 2", value = BigDecimal("10")),
            Balance(userId = "User 3", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-21")),
            Balance(userId = "User 1", value = BigDecimal("7")),
            Balance(userId = "User 2", value = BigDecimal("11")),
            Balance(userId = "User 3", value = BigDecimal("3")),
            Balance(userId = "User 4", value = BigDecimal("0")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-62")),
            Balance(userId = "User 1", value = BigDecimal("23")),
            Balance(userId = "User 2", value = BigDecimal("46")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("-32")),
            Balance(userId = "User 5", value = BigDecimal("4")),
            Balance(userId = "User 6", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-78")),
            Balance(userId = "User 1", value = BigDecimal("31")),
            Balance(userId = "User 2", value = BigDecimal("6")),
            Balance(userId = "User 3", value = BigDecimal("12")),
            Balance(userId = "User 4", value = BigDecimal("14")),
            Balance(userId = "User 5", value = BigDecimal("10")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-90")),
            Balance(userId = "User 1", value = BigDecimal("39")),
            Balance(userId = "User 2", value = BigDecimal("13")),
            Balance(userId = "User 3", value = BigDecimal("13")),
            Balance(userId = "User 4", value = BigDecimal("11")),
            Balance(userId = "User 5", value = BigDecimal("7")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("2")),
            Balance(userId = "User 8", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-127")),
            Balance(userId = "User 1", value = BigDecimal("45")),
            Balance(userId = "User 2", value = BigDecimal("33")),
            Balance(userId = "User 3", value = BigDecimal("18")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("4")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("3")),
            Balance(userId = "User 10", value = BigDecimal("-4")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-154")),
            Balance(userId = "User 1", value = BigDecimal("50")),
            Balance(userId = "User 2", value = BigDecimal("66")),
            Balance(userId = "User 3", value = BigDecimal("10")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("1")),
        ),

        listOf(
            Balance(userId = "User 0", value = BigDecimal("-158")),
            Balance(userId = "User 1", value = BigDecimal("51")),
            Balance(userId = "User 2", value = BigDecimal("42")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("18")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("5")),
            Balance(userId = "User 8", value = BigDecimal("4")),
            Balance(userId = "User 9", value = BigDecimal("3")),
            Balance(userId = "User 10", value = BigDecimal("-1")),
            Balance(userId = "User 11", value = BigDecimal("-1")),
            Balance(userId = "User 12", value = BigDecimal("-1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-176")),
            Balance(userId = "User 1", value = BigDecimal("62")),
            Balance(userId = "User 2", value = BigDecimal("67")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("18")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("8")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("-3")),
            Balance(userId = "User 12", value = BigDecimal("-4")),
            Balance(userId = "User 13", value = BigDecimal("-5")),
        ),

        listOf(
            Balance(userId = "User 0", value = BigDecimal("-34")),
            Balance(userId = "User 1", value = BigDecimal("8")),
            Balance(userId = "User 2", value = BigDecimal("-6")),
            Balance(userId = "User 3", value = BigDecimal("6")),
            Balance(userId = "User 4", value = BigDecimal("2")),
            Balance(userId = "User 5", value = BigDecimal("24")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-62")),
            Balance(userId = "User 1", value = BigDecimal("23")),
            Balance(userId = "User 2", value = BigDecimal("46")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("-32")),
            Balance(userId = "User 5", value = BigDecimal("4")),
            Balance(userId = "User 6", value = BigDecimal("1")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-78")),
            Balance(userId = "User 1", value = BigDecimal("31")),
            Balance(userId = "User 2", value = BigDecimal("6")),
            Balance(userId = "User 3", value = BigDecimal("12")),
            Balance(userId = "User 4", value = BigDecimal("14")),
            Balance(userId = "User 5", value = BigDecimal("10")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("1")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-90")),
            Balance(userId = "User 1", value = BigDecimal("39")),
            Balance(userId = "User 2", value = BigDecimal("13")),
            Balance(userId = "User 3", value = BigDecimal("13")),
            Balance(userId = "User 4", value = BigDecimal("11")),
            Balance(userId = "User 5", value = BigDecimal("7")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("2")),
            Balance(userId = "User 8", value = BigDecimal("1")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-111")),
            Balance(userId = "User 1", value = BigDecimal("41")),
            Balance(userId = "User 2", value = BigDecimal("31")),
            Balance(userId = "User 3", value = BigDecimal("25")),
            Balance(userId = "User 4", value = BigDecimal("10")),
            Balance(userId = "User 5", value = BigDecimal("3")),
            Balance(userId = "User 6", value = BigDecimal("1")),
            Balance(userId = "User 7", value = BigDecimal("1")),
            Balance(userId = "User 8", value = BigDecimal("-4")),
            Balance(userId = "User 9", value = BigDecimal("3")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-127")),
            Balance(userId = "User 1", value = BigDecimal("45")),
            Balance(userId = "User 2", value = BigDecimal("33")),
            Balance(userId = "User 3", value = BigDecimal("18")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("4")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("3")),
            Balance(userId = "User 10", value = BigDecimal("-4")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-154")),
            Balance(userId = "User 1", value = BigDecimal("50")),
            Balance(userId = "User 2", value = BigDecimal("66")),
            Balance(userId = "User 3", value = BigDecimal("10")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("1")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-158")),
            Balance(userId = "User 1", value = BigDecimal("51")),
            Balance(userId = "User 2", value = BigDecimal("42")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("18")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("6")),
            Balance(userId = "User 7", value = BigDecimal("5")),
            Balance(userId = "User 8", value = BigDecimal("4")),
            Balance(userId = "User 9", value = BigDecimal("3")),
            Balance(userId = "User 10", value = BigDecimal("-1")),
            Balance(userId = "User 11", value = BigDecimal("-1")),
            Balance(userId = "User 12", value = BigDecimal("-1")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-176")),
            Balance(userId = "User 1", value = BigDecimal("62")),
            Balance(userId = "User 2", value = BigDecimal("67")),
            Balance(userId = "User 3", value = BigDecimal("20")),
            Balance(userId = "User 4", value = BigDecimal("18")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("8")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("-3")),
            Balance(userId = "User 12", value = BigDecimal("-4")),
            Balance(userId = "User 13", value = BigDecimal("-5")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-190")),
            Balance(userId = "User 1", value = BigDecimal("65")),
            Balance(userId = "User 2", value = BigDecimal("33")),
            Balance(userId = "User 3", value = BigDecimal("17")),
            Balance(userId = "User 4", value = BigDecimal("81")),
            Balance(userId = "User 5", value = BigDecimal("8")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("2")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("-3")),
            Balance(userId = "User 12", value = BigDecimal("-4")),
            Balance(userId = "User 13", value = BigDecimal("-5")),
            Balance(userId = "User 14", value = BigDecimal("-6")),

        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-203")),
            Balance(userId = "User 1", value = BigDecimal("68")),
            Balance(userId = "User 2", value = BigDecimal("100")),
            Balance(userId = "User 3", value = BigDecimal("19")),
            Balance(userId = "User 4", value = BigDecimal("15")),
            Balance(userId = "User 5", value = BigDecimal("12")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("1")),
            Balance(userId = "User 10", value = BigDecimal("-1")),
            Balance(userId = "User 11", value = BigDecimal("-2")),
            Balance(userId = "User 12", value = BigDecimal("-3")),
            Balance(userId = "User 13", value = BigDecimal("-4")),
            Balance(userId = "User 14", value = BigDecimal("-5")),
            Balance(userId = "User 15", value = BigDecimal("-6")),
        ),

        listOf(
            Balance(userId = "User 0", value = BigDecimal("-215")),
            Balance(userId = "User 1", value = BigDecimal("70")),
            Balance(userId = "User 2", value = BigDecimal("131")),
            Balance(userId = "User 3", value = BigDecimal("21")),
            Balance(userId = "User 4", value = BigDecimal("11")),
            Balance(userId = "User 5", value = BigDecimal("10")),
            Balance(userId = "User 6", value = BigDecimal("4")),
            Balance(userId = "User 7", value = BigDecimal("3")),
            Balance(userId = "User 8", value = BigDecimal("1")),
            Balance(userId = "User 9", value = BigDecimal("-1")),
            Balance(userId = "User 10", value = BigDecimal("-2")),
            Balance(userId = "User 11", value = BigDecimal("-3")),
            Balance(userId = "User 12", value = BigDecimal("-4")),
            Balance(userId = "User 13", value = BigDecimal("-5")),
            Balance(userId = "User 14", value = BigDecimal("-6")),
            Balance(userId = "User 15", value = BigDecimal("-7")),
            Balance(userId = "User 16", value = BigDecimal("-8")),
        ),

        listOf(
            Balance(userId = "User 0", value = BigDecimal("-223")),
            Balance(userId = "User 1", value = BigDecimal("71")),
            Balance(userId = "User 2", value = BigDecimal("170")),
            Balance(userId = "User 3", value = BigDecimal("19")),
            Balance(userId = "User 4", value = BigDecimal("12")),
            Balance(userId = "User 5", value = BigDecimal("3")),
            Balance(userId = "User 6", value = BigDecimal("2")),
            Balance(userId = "User 7", value = BigDecimal("1")),
            Balance(userId = "User 8", value = BigDecimal("-1")),
            Balance(userId = "User 9", value = BigDecimal("-2")),
            Balance(userId = "User 10", value = BigDecimal("-3")),
            Balance(userId = "User 11", value = BigDecimal("-4")),
            Balance(userId = "User 12", value = BigDecimal("-5")),
            Balance(userId = "User 13", value = BigDecimal("-6")),
            Balance(userId = "User 14", value = BigDecimal("-7")),
            Balance(userId = "User 15", value = BigDecimal("-8")),
            Balance(userId = "User 16", value = BigDecimal("-9")),
            Balance(userId = "User 17", value = BigDecimal("-10")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("12")),
            Balance(userId = "User 1", value = BigDecimal("-24")),
            Balance(userId = "User 2", value = BigDecimal("12")),
            Balance(userId = "User 3", value = BigDecimal("-65")),
            Balance(userId = "User 4", value = BigDecimal("44")),
            Balance(userId = "User 5", value = BigDecimal("19")),
            Balance(userId = "User 6", value = BigDecimal("2")),
            Balance(userId = "User 7", value = BigDecimal("13")),
            Balance(userId = "User 8", value = BigDecimal("2")),
            Balance(userId = "User 9", value = BigDecimal("-23")),
            Balance(userId = "User 10", value = BigDecimal("18")),
            Balance(userId = "User 11", value = BigDecimal("-20")),
            Balance(userId = "User 12", value = BigDecimal("12")),
            Balance(userId = "User 13", value = BigDecimal("-2")),
            Balance(userId = "User 14", value = BigDecimal("-20")),
            Balance(userId = "User 15", value = BigDecimal("10")),
            Balance(userId = "User 16", value = BigDecimal("-8")),
            Balance(userId = "User 17", value = BigDecimal("19")),
            Balance(userId = "User 18", value = BigDecimal("-2")),
            Balance(userId = "User 19", value = BigDecimal("1")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-440")),
            Balance(userId = "User 1", value = BigDecimal("-120")),
            Balance(userId = "User 2", value = BigDecimal("-240")),
            Balance(userId = "User 3", value = BigDecimal("-145")),
            Balance(userId = "User 4", value = BigDecimal("-85")),
            Balance(userId = "User 5", value = BigDecimal("-265")),
            Balance(userId = "User 6", value = BigDecimal("-85")),
            Balance(userId = "User 7", value = BigDecimal("165")),
            Balance(userId = "User 8", value = BigDecimal("-95")),
            Balance(userId = "User 9", value = BigDecimal("120")),
            Balance(userId = "User 10", value = BigDecimal("-50")),
            Balance(userId = "User 11", value = BigDecimal("235")),
            Balance(userId = "User 12", value = BigDecimal("-260")),
            Balance(userId = "User 13", value = BigDecimal("340")),
            Balance(userId = "User 14", value = BigDecimal("-470")),
            Balance(userId = "User 15", value = BigDecimal("510")),
            Balance(userId = "User 16", value = BigDecimal("-310")),
            Balance(userId = "User 17", value = BigDecimal("420")),
            Balance(userId = "User 18", value = BigDecimal("-185")),
            Balance(userId = "User 19", value = BigDecimal("510")),
            Balance(userId = "User 20", value = BigDecimal("225")),
            Balance(userId = "User 21", value = BigDecimal("-150")),
            Balance(userId = "User 22", value = BigDecimal("375")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("-450")),
            Balance(userId = "User 1", value = BigDecimal("-130")),
            Balance(userId = "User 2", value = BigDecimal("-240")),
            Balance(userId = "User 3", value = BigDecimal("-145")),
            Balance(userId = "User 4", value = BigDecimal("-85")),
            Balance(userId = "User 5", value = BigDecimal("-265")),
            Balance(userId = "User 6", value = BigDecimal("-85")),
            Balance(userId = "User 7", value = BigDecimal("165")),
            Balance(userId = "User 8", value = BigDecimal("-95")),
            Balance(userId = "User 9", value = BigDecimal("120")),
            Balance(userId = "User 10", value = BigDecimal("-50")),
            Balance(userId = "User 11", value = BigDecimal("235")),
            Balance(userId = "User 12", value = BigDecimal("-260")),
            Balance(userId = "User 13", value = BigDecimal("340")),
            Balance(userId = "User 14", value = BigDecimal("-470")),
            Balance(userId = "User 15", value = BigDecimal("510")),
            Balance(userId = "User 16", value = BigDecimal("-310")),
            Balance(userId = "User 17", value = BigDecimal("420")),
            Balance(userId = "User 18", value = BigDecimal("-185")),
            Balance(userId = "User 19", value = BigDecimal("335")),
            Balance(userId = "User 20", value = BigDecimal("-245")),
            Balance(userId = "User 21", value = BigDecimal("110")),
            Balance(userId = "User 22", value = BigDecimal("-80")),
            Balance(userId = "User 23", value = BigDecimal("410")),
            Balance(userId = "User 24", value = BigDecimal("-385")),
            Balance(userId = "User 25", value = BigDecimal("225")),
            Balance(userId = "User 26", value = BigDecimal("-150")),
            Balance(userId = "User 27", value = BigDecimal("475")),
            Balance(userId = "User 28", value = BigDecimal("-120")),
            Balance(userId = "User 29", value = BigDecimal("365")),
            Balance(userId = "User 30", value = BigDecimal("-430")),
            Balance(userId = "User 31", value = BigDecimal("510")),
            Balance(userId = "User 32", value = BigDecimal("-240")),
            Balance(userId = "User 33", value = BigDecimal("160")),
            Balance(userId = "User 34", value = BigDecimal("120")),
            Balance(userId = "User 35", value = BigDecimal("-335")),
            Balance(userId = "User 36", value = BigDecimal("170")),
            Balance(userId = "User 37", value = BigDecimal("-250")),
            Balance(userId = "User 38", value = BigDecimal("415")),
            Balance(userId = "User 39", value = BigDecimal("-60")),
            Balance(userId = "User 40", value = BigDecimal("340")),
            Balance(userId = "User 41", value = BigDecimal("-470")),
            Balance(userId = "User 42", value = BigDecimal("255")),
            Balance(userId = "User 43", value = BigDecimal("-315")),
            Balance(userId = "User 44", value = BigDecimal("510")),
            Balance(userId = "User 45", value = BigDecimal("-510")),
            Balance(userId = "User 46", value = BigDecimal("390")),
            Balance(userId = "User 47", value = BigDecimal("-170")),
            Balance(userId = "User 48", value = BigDecimal("320")),
            Balance(userId = "User 49", value = BigDecimal("-370")),
        ),
        listOf(
            Balance(userId = "User 0", value = BigDecimal("340")),
            Balance(userId = "User 1", value = BigDecimal("-95")),
            Balance(userId = "User 2", value = BigDecimal("-956")),
            Balance(userId = "User 3", value = BigDecimal("-78")),
            Balance(userId = "User 4", value = BigDecimal("129")),
            Balance(userId = "User 5", value = BigDecimal("-46")),
            Balance(userId = "User 6", value = BigDecimal("183")),
            Balance(userId = "User 7", value = BigDecimal("-32")),
            Balance(userId = "User 8", value = BigDecimal("89")),
            Balance(userId = "User 9", value = BigDecimal("-59")),
            Balance(userId = "User 10", value = BigDecimal("243")),
            Balance(userId = "User 11", value = BigDecimal("-102")),
            Balance(userId = "User 12", value = BigDecimal("67")),
            Balance(userId = "User 13", value = BigDecimal("-98")),
            Balance(userId = "User 14", value = BigDecimal("190")),
            Balance(userId = "User 15", value = BigDecimal("-300")),
            Balance(userId = "User 16", value = BigDecimal("270")),
            Balance(userId = "User 17", value = BigDecimal("-320")),
            Balance(userId = "User 18", value = BigDecimal("180")),
            Balance(userId = "User 19", value = BigDecimal("-60")),
            Balance(userId = "User 20", value = BigDecimal("45")),
            Balance(userId = "User 21", value = BigDecimal("150")),
            Balance(userId = "User 22", value = BigDecimal("-400")),
            Balance(userId = "User 23", value = BigDecimal("130")),
            Balance(userId = "User 24", value = BigDecimal("-210")),
            Balance(userId = "User 25", value = BigDecimal("370")),
            Balance(userId = "User 26", value = BigDecimal("-185")),
            Balance(userId = "User 27", value = BigDecimal("195")),
            Balance(userId = "User 28", value = BigDecimal("-65")),
            Balance(userId = "User 29", value = BigDecimal("75")),
            Balance(userId = "User 30", value = BigDecimal("-55")),
            Balance(userId = "User 31", value = BigDecimal("350")),
            Balance(userId = "User 32", value = BigDecimal("-30")),
            Balance(userId = "User 33", value = BigDecimal("40")),
            Balance(userId = "User 34", value = BigDecimal("-100")),
            Balance(userId = "User 35", value = BigDecimal("110")),
            Balance(userId = "User 36", value = BigDecimal("-90")),
            Balance(userId = "User 37", value = BigDecimal("25")),
            Balance(userId = "User 38", value = BigDecimal("-25")),
            Balance(userId = "User 39", value = BigDecimal("195")),
            Balance(userId = "User 40", value = BigDecimal("-115")),
            Balance(userId = "User 41", value = BigDecimal("50")),
            Balance(userId = "User 42", value = BigDecimal("-50")),
            Balance(userId = "User 43", value = BigDecimal("300")),
            Balance(userId = "User 44", value = BigDecimal("-120")),
            Balance(userId = "User 45", value = BigDecimal("215")),
            Balance(userId = "User 46", value = BigDecimal("-215")),
            Balance(userId = "User 47", value = BigDecimal("125")),
            Balance(userId = "User 48", value = BigDecimal("-205")),
            Balance(userId = "User 49", value = BigDecimal("170")),
            Balance(userId = "User 50", value = BigDecimal("-370")),
            Balance(userId = "User 51", value = BigDecimal("295")),
            Balance(userId = "User 52", value = BigDecimal("-295")),
            Balance(userId = "User 53", value = BigDecimal("55")),
            Balance(userId = "User 54", value = BigDecimal("-35")),
            Balance(userId = "User 55", value = BigDecimal("85")),
            Balance(userId = "User 56", value = BigDecimal("-60")),
            Balance(userId = "User 57", value = BigDecimal("230")),
            Balance(userId = "User 58", value = BigDecimal("-230")),
            Balance(userId = "User 59", value = BigDecimal("190")),
            Balance(userId = "User 60", value = BigDecimal("-190")),
            Balance(userId = "User 61", value = BigDecimal("140")),
            Balance(userId = "User 62", value = BigDecimal("-140")),
            Balance(userId = "User 63", value = BigDecimal("230")),
            Balance(userId = "User 64", value = BigDecimal("-130")),
            Balance(userId = "User 65", value = BigDecimal("210")),
            Balance(userId = "User 66", value = BigDecimal("-210")),
            Balance(userId = "User 67", value = BigDecimal("110")),
            Balance(userId = "User 68", value = BigDecimal("-110")),
            Balance(userId = "User 69", value = BigDecimal("60")),
            Balance(userId = "User 70", value = BigDecimal("-60")),
            Balance(userId = "User 71", value = BigDecimal("30")),
            Balance(userId = "User 72", value = BigDecimal("-30")),
            Balance(userId = "User 73", value = BigDecimal("180")),
            Balance(userId = "User 74", value = BigDecimal("-180")),
            Balance(userId = "User 75", value = BigDecimal("400")),
            Balance(userId = "User 76", value = BigDecimal("-400")),
            Balance(userId = "User 77", value = BigDecimal("250")),
            Balance(userId = "User 78", value = BigDecimal("-250")),
            Balance(userId = "User 79", value = BigDecimal("150")),
            Balance(userId = "User 80", value = BigDecimal("-150")),
            Balance(userId = "User 81", value = BigDecimal("80")),
            Balance(userId = "User 82", value = BigDecimal("-80")),
            Balance(userId = "User 83", value = BigDecimal("20")),
            Balance(userId = "User 84", value = BigDecimal("-20")),
            Balance(userId = "User 85", value = BigDecimal("10")),
            Balance(userId = "User 86", value = BigDecimal("-10")),
            Balance(userId = "User 87", value = BigDecimal("30")),
            Balance(userId = "User 88", value = BigDecimal("-30")),
            Balance(userId = "User 89", value = BigDecimal("60")),
            Balance(userId = "User 90", value = BigDecimal("-60")),
            Balance(userId = "User 91", value = BigDecimal("45")),
            Balance(userId = "User 92", value = BigDecimal("-45")),
            Balance(userId = "User 93", value = BigDecimal("75")),
            Balance(userId = "User 94", value = BigDecimal("-75")),
            Balance(userId = "User 95", value = BigDecimal("85")),
            Balance(userId = "User 96", value = BigDecimal("-85")),
            Balance(userId = "User 97", value = BigDecimal("100")),
            Balance(userId = "User 98", value = BigDecimal("-110")),
            Balance(userId = "User 99", value = BigDecimal("10")),
        ),
    )
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
