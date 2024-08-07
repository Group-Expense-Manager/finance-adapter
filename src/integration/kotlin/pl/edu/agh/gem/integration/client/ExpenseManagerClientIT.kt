package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.ExpenseManagerClientException
import pl.edu.agh.gem.internal.client.RetryableExpenseManagerClientException
import pl.edu.agh.gem.util.DummyData.EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_EXPENSE_ID
import pl.edu.agh.gem.util.createExpenseFilterOptions
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse

class ExpenseManagerClientIT(
    private val expenseManagerClient: ExpenseManagerClient,
) : BaseIntegrationSpec({

    should("get activities") {
        // given
        val expenseFilterOptions = createExpenseFilterOptions()
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, expenseFilterOptions)

        // when
        val result = expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)

        // then
        result.map { it.activityId } shouldContainExactly listOf(EXPENSE_ID, OTHER_EXPENSE_ID)
    }

    should("throw ExpenseManagerClientException when we send bad request") {
        // given
        val expenseFilterOptions = createExpenseFilterOptions()
        stubExpenseManagerActivities(createExpenseManagerActivitiesResponse(), GROUP_ID, expenseFilterOptions, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<ExpenseManagerClientException> {
            expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)
        }
    }

    should("throw RetryableGroupManagerClientException when client has internal error") {
        // given
        val expenseFilterOptions = createExpenseFilterOptions()
        stubExpenseManagerActivities(createExpenseManagerActivitiesResponse(), GROUP_ID, expenseFilterOptions, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableExpenseManagerClientException> {
            expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)
        }
    }
},)
