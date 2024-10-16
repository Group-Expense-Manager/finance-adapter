package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubAcceptedExpenses
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.ExpenseManagerClientException
import pl.edu.agh.gem.internal.client.RetryableExpenseManagerClientException
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_EXPENSE_ID
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import pl.edu.agh.gem.util.createClientFilterOptions
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse

class ExpenseManagerClientIT(
    private val expenseManagerClient: ExpenseManagerClient,
) : BaseIntegrationSpec({

    should("get activities") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, expenseFilterOptions)

        // when
        val result = expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)

        // then
        result.map { it.activityId } shouldContainExactly listOf(EXPENSE_ID, OTHER_EXPENSE_ID)
    }

    should("throw ExpenseManagerClientException when we send bad activities request") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        stubExpenseManagerActivities(createExpenseManagerActivitiesResponse(), GROUP_ID, expenseFilterOptions, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<ExpenseManagerClientException> {
            expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)
        }
    }

    should("throw RetryableGroupManagerClientException when sending activities request and client has internal error") {
        // given
        val expenseFilterOptions = createClientFilterOptions()
        stubExpenseManagerActivities(createExpenseManagerActivitiesResponse(), GROUP_ID, expenseFilterOptions, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableExpenseManagerClientException> {
            expenseManagerClient.getActivities(GROUP_ID, expenseFilterOptions)
        }
    }

    should("get accepted expenses") {
        // given
        val acceptedExpensesResponse = createAcceptedExpensesResponse()
        stubAcceptedExpenses(acceptedExpensesResponse, GROUP_ID)

        // when
        val result = expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_1)

        // then
        result shouldBe acceptedExpensesResponse.toDomain()
    }

    should("throw ExpenseManagerClientException when we send bad accepted expenses request") {
        // given
        val acceptedExpensesResponse = createAcceptedExpensesResponse()
        stubAcceptedExpenses(acceptedExpensesResponse, GROUP_ID, CURRENCY_1 ,NOT_ACCEPTABLE)

        // when & then
        shouldThrow<ExpenseManagerClientException> {
            expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_1)
        }
    }

    should("throw RetryableGroupManagerClientException when sending accepted expenses request and client has internal error") {
        // given
        val acceptedExpensesResponse = createAcceptedExpensesResponse()
        stubAcceptedExpenses(acceptedExpensesResponse, GROUP_ID, CURRENCY_1 , INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableExpenseManagerClientException> {
            expenseManagerClient.getAcceptedExpenses(GROUP_ID, CURRENCY_1)
        }
    }
},)
