package pl.edu.agh.gem.internal.service

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembers
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpense
import pl.edu.agh.gem.util.createAcceptedExpenseParticipant
import pl.edu.agh.gem.util.createAcceptedPayment
import pl.edu.agh.gem.util.createActivity
import pl.edu.agh.gem.util.createAmount
import pl.edu.agh.gem.util.createClientFilterOptions
import pl.edu.agh.gem.util.createFilterOptions
import pl.edu.agh.gem.util.createGroupData
import java.math.BigDecimal

class FinanceServiceTest : ShouldSpec({
    val expenseManagerClient = mock<ExpenseManagerClient>()
    val paymentManagerClient = mock<PaymentManagerClient>()
    val groupManagerClient = mock<GroupManagerClient>()

    val financeService = FinanceService(
        expenseManagerClient,
        paymentManagerClient,
        groupManagerClient,
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
        val groupData = createGroupData()
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(groupData)
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID)).thenReturn(listOf())
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID)).thenReturn(listOf())

        // when
        val result = financeService.getBalances(GROUP_ID)

        // then
        result.also {
            it.keys shouldContainExactly setOf(CURRENCY_1, CURRENCY_2)
            it.values.shouldForAll { userBalanceMap ->
                userBalanceMap.keys shouldContainExactly setOf(USER_ID, OTHER_USER_ID)
                userBalanceMap.values.shouldForAll { balance ->
                    balance shouldBe BigDecimal.ZERO
                }
            }
        }
        verify(groupManagerClient, times(1)).getGroup(GROUP_ID)
        verify(expenseManagerClient, times(1)).getAcceptedExpenses(GROUP_ID)
        verify(paymentManagerClient, times(1)).getAcceptedPayments(GROUP_ID)
    }

    should("get balances") {
        // given
        val groupData = createGroupData(members = createGroupMembers(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID))
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            fxData = null,
        )
        val payment = createAcceptedPayment(
            creatorId = OTHER_USER_ID,
            recipientId = USER_ID,
            amount = createAmount(
                value = "3".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = null,
        )
        whenever(groupManagerClient.getGroup(GROUP_ID)).thenReturn(groupData)
        whenever(expenseManagerClient.getAcceptedExpenses(GROUP_ID)).thenReturn(listOf(expense))
        whenever(paymentManagerClient.getAcceptedPayments(GROUP_ID)).thenReturn(listOf(payment))

        // when
        val result = financeService.getBalances(GROUP_ID)

        // then
        result.also {
            it.keys shouldContainExactly setOf(CURRENCY_1, CURRENCY_2)
            it.values.shouldForAll { userBalanceMap ->
                userBalanceMap.keys shouldContainExactly setOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID)
            }
            it[CURRENCY_1]?.get(USER_ID) shouldBe "5".toBigDecimal()
            it[CURRENCY_1]?.get(OTHER_USER_ID) shouldBe "0".toBigDecimal()
            it[CURRENCY_1]?.get(ANOTHER_USER_ID) shouldBe "-5".toBigDecimal()

            it[CURRENCY_2]?.values?.shouldForAll { balance -> balance shouldBe BigDecimal.ZERO }
        }

        verify(groupManagerClient, times(1)).getGroup(GROUP_ID)
        verify(expenseManagerClient, times(1)).getAcceptedExpenses(GROUP_ID)
        verify(paymentManagerClient, times(1)).getAcceptedPayments(GROUP_ID)
    }
},)
