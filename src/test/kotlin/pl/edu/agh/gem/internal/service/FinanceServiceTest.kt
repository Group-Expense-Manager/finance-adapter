package pl.edu.agh.gem.internal.service

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.util.createActivity
import pl.edu.agh.gem.util.createExpenseFilterOptions
import pl.edu.agh.gem.util.createFilterOptions

class FinanceServiceTest : ShouldSpec({
    val expenseManagerClient = mock<ExpenseManagerClient>()
    val financeService = FinanceService(
        expenseManagerClient,
    )

    should("get all activities when type is not specified") {
        // given
        val expenseFilterOptions = createExpenseFilterOptions()
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
        val expenseFilterOptions = createExpenseFilterOptions()
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
        val expenseFilterOptions = createExpenseFilterOptions()
        val expenseManagerActivities = listOf(createActivity(type = EXPENSE))
        whenever(expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)).thenReturn(expenseManagerActivities)

        // when
        val result = financeService.getActivities(GROUP_ID, createFilterOptions(type = PAYMENT))

        // then
        result shouldBe listOf()
        verify(expenseManagerClient, times(0)).getActivities(GROUP_ID, expenseFilterOptions)
    }
},)
