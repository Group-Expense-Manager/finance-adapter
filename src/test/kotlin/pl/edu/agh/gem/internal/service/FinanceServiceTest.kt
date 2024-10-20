package pl.edu.agh.gem.internal.service

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.settelment.SettlementStatus
import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.model.GroupMember
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpense
import pl.edu.agh.gem.util.createAcceptedPayment
import pl.edu.agh.gem.util.createActivity
import pl.edu.agh.gem.util.createBalance
import pl.edu.agh.gem.util.createBalances
import pl.edu.agh.gem.util.createClientFilterOptions
import pl.edu.agh.gem.util.createFilterOptions
import pl.edu.agh.gem.util.createGroupData
import pl.edu.agh.gem.util.createSettlements
import java.math.BigDecimal

class FinanceServiceTest : ShouldSpec({
    val expenseManagerClient = mock<ExpenseManagerClient>()
    val paymentManagerClient = mock<PaymentManagerClient>()
    val groupManagerClient = mock<GroupManagerClient>()
    val balancesRepository = mock<BalancesRepository>()
    val settlementsRepository = mock<SettlementsRepository>()

    val financeService = FinanceService(
        expenseManagerClient,
        paymentManagerClient,
        groupManagerClient,
        balancesRepository,
        settlementsRepository,
    )

    should("get all activities when type is not specified") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        val expenseManagerActivities = listOf(createActivity(type = EXPENSE))
        whenever(expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)).thenReturn(expenseManagerActivities)

        // when
        val result = financeService.getActivities(GROUP_ID, createFilterOptions(type = null))

        // then
        result shouldBe expenseManagerActivities
        verify(expenseManagerClient, times(1)).getActivities(GROUP_ID, expenseFilterOptions)
    }

    should("get expense activities when type is EXPENSE") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        val expenseManagerActivities = listOf(createActivity(type = EXPENSE))
        whenever(expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)).thenReturn(expenseManagerActivities)

        // when
        val result = financeService.getActivities(GROUP_ID, createFilterOptions(type = EXPENSE))

        // then
        result shouldBe expenseManagerActivities
        verify(expenseManagerClient, times(1)).getActivities(GROUP_ID, expenseFilterOptions)
    }

    should("get payment activities when type is PAYMENT") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        val expenseManagerActivities = listOf(createActivity(type = EXPENSE))
        whenever(expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)).thenReturn(expenseManagerActivities)

        // when
        val result = financeService.getActivities(GROUP_ID, createFilterOptions(type = PAYMENT))

        // then
        result shouldBe listOf()
        verify(expenseManagerClient, times(0)).getActivities(GROUP_ID, expenseFilterOptions)
    }

    should("get empty balances") {
        // given
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(
            createGroupData(
                members = listOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID).map { GroupMember(it) },
                currencies = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
            ),
        )
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_1)).thenReturn(listOf())
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_2)).thenReturn(listOf(createAcceptedExpense()))
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_1)).thenReturn(listOf())
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_2)).thenReturn(listOf(createAcceptedPayment()))
        whenever(balancesRepository.getBalances(GROUP_ID)).thenReturn(listOf())

        // when
        val result = financeService.getBalances(GROUP_ID)

        // then
        verify(balancesRepository, times(1)).getBalances(GROUP_ID)
        result.map { it.currency } shouldContainExactlyInAnyOrder listOf(CURRENCY_1, CURRENCY_2)
        result.map { it.groupId } shouldContainOnly listOf(GROUP_ID)
        result.find { it.currency == CURRENCY_1 }?.users
            ?.map { Pair(it.value.toString(), it.userId) }
            .shouldContainExactlyInAnyOrder(
                listOf(
                    Pair("0", USER_ID),
                    Pair("0", OTHER_USER_ID),
                    Pair("0", ANOTHER_USER_ID),
                ),
            )
        result.find { it.currency == CURRENCY_2 }?.users
            ?.map { Pair(it.value.toString(), it.userId) }
            .shouldContainExactlyInAnyOrder(
                listOf(
                    Pair("-38.88", OTHER_USER_ID),
                    Pair("-6.48", ANOTHER_USER_ID),
                    Pair("45.36", USER_ID),
                ),
            )
        verify(balancesRepository, times(1)).getBalances(GROUP_ID)
    }

    should("get balances") {
        // given
        whenever(balancesRepository.getBalances(GROUP_ID)).thenReturn(
            listOf(
                createBalances(
                    currency = CURRENCY_1,
                    groupId = GROUP_ID,
                    balances = listOf(
                        createBalance(USER_ID, BigDecimal("5")),
                        createBalance(OTHER_USER_ID, BigDecimal("0")),
                        createBalance(ANOTHER_USER_ID, BigDecimal("-5")),
                    ),
                ),
                createBalances(
                    currency = CURRENCY_2,
                    groupId = GROUP_ID,
                    balances = listOf(
                        createBalance(OTHER_USER_ID, BigDecimal("0")),
                    ),
                ),
            ),
        )
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(
            createGroupData(
                members = listOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID).map { GroupMember(it) },
                currencies = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
            ),
        )
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_1)).thenReturn(listOf())
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_2)).thenReturn(listOf(createAcceptedExpense()))
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_1)).thenReturn(listOf())
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_2)).thenReturn(listOf(createAcceptedPayment()))
        whenever(balancesRepository.getBalances(GROUP_ID)).thenReturn(listOf())

        // when
        val result = financeService.getBalances(GROUP_ID)

        // then
        verify(balancesRepository, times(1)).getBalances(GROUP_ID)
        result.map { it.currency } shouldContainExactlyInAnyOrder listOf(CURRENCY_1, CURRENCY_2)
        result.map { it.groupId } shouldContainOnly listOf(GROUP_ID)
        result.find { it.currency == CURRENCY_1 }?.users
            ?.map { Pair(it.value.toString(), it.userId) }
            .shouldContainExactlyInAnyOrder(
                listOf(
                    Pair("0", USER_ID),
                    Pair("0", OTHER_USER_ID),
                    Pair("0", ANOTHER_USER_ID),
                ),
            )
        result.find { it.currency == CURRENCY_2 }?.users
            ?.map { Pair(it.value.toString(), it.userId) }
            .shouldContainExactlyInAnyOrder(
                listOf(
                    Pair("-38.88", OTHER_USER_ID),
                    Pair("-6.48", ANOTHER_USER_ID),
                    Pair("45.36", USER_ID),
                ),
            )
        verify(balancesRepository, times(1)).getBalances(GROUP_ID)
    }

    should("get empty settlements") {
        // given
        whenever(settlementsRepository.getSettlements(GROUP_ID)).thenReturn(listOf())
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(
            createGroupData(
                members = listOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID).map { GroupMember(it) },
                currencies = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
            ),
        )
        // when
        val result = financeService.getSettlements(GROUP_ID)

        // then
        verify(settlementsRepository, times(1)).getSettlements(GROUP_ID)

        result.map { it.currency } shouldContainExactlyInAnyOrder listOf(CURRENCY_1, CURRENCY_2)
        result.map { it.settlements } shouldContainExactlyInAnyOrder listOf(listOf(), listOf())
    }

    should("get settlements") {
        // given
        val settlements = listOf(createSettlements(groupId = GROUP_ID, currency = CURRENCY_1))
        whenever(settlementsRepository.getSettlements(GROUP_ID)).thenReturn(settlements)
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(
            createGroupData(
                members = listOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID).map { GroupMember(it) },
                currencies = listOf(CURRENCY_1, CURRENCY_2).map { Currency(it) },
            ),
        )
        // when
        val result = financeService.getSettlements(GROUP_ID)

        // then
        verify(settlementsRepository, times(1)).getSettlements(GROUP_ID)

        result.first() shouldBe settlements.first()
        result.last().also { last ->
            last.groupId shouldBe GROUP_ID
            last.currency shouldBe CURRENCY_2
            last.settlements shouldBe listOf()
            last.status shouldBe SettlementStatus.SAVED
        }
    }
},)
