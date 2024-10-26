package pl.edu.agh.gem.external.client

import io.github.resilience4j.retry.annotation.Retry
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.edu.agh.gem.config.ExpenseManagerProperties
import pl.edu.agh.gem.external.dto.expense.AcceptedExpensesResponse
import pl.edu.agh.gem.external.dto.expense.ExpenseManagerActivitiesResponse
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.ExpenseManagerClientException
import pl.edu.agh.gem.internal.client.RetryableExpenseManagerClientException
import pl.edu.agh.gem.internal.model.expense.AcceptedExpense
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.paths.Paths.INTERNAL
import java.util.Optional

@Component
class RestExpenseManagerClient(
    @Qualifier("ExpenseManagerRestTemplate") val restTemplate: RestTemplate,
    val expenseManagerProperties: ExpenseManagerProperties,
) : ExpenseManagerClient {

    @Retry(name = "expenseManager")
    override fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions?): List<Activity> {
        return try {
            restTemplate.exchange(
                resolveActivitiesAddress(groupId, clientFilterOptions),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                ExpenseManagerActivitiesResponse::class.java,
            ).body?.toDomain() ?: throw ExpenseManagerClientException("While trying to retrieve activities we receive empty body")
        } catch (ex: HttpClientErrorException) {
            logger.warn(ex) { "Client side exception while trying to retrieve activities" }
            throw ExpenseManagerClientException(ex.message)
        } catch (ex: HttpServerErrorException) {
            logger.warn(ex) { "Server side exception while trying to retrieve activities" }
            throw RetryableExpenseManagerClientException(ex.message)
        } catch (ex: Exception) {
            logger.warn(ex) { "Unexpected exception while trying to retrieve activities" }
            throw ExpenseManagerClientException(ex.message)
        }
    }

    @Retry(name = "expenseManager")
    override fun getAcceptedExpenses(groupId: String, currency: String): List<AcceptedExpense> {
        return try {
            restTemplate.exchange(
                resolveAcceptedExpensesAddress(groupId, currency),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                AcceptedExpensesResponse::class.java,
            ).body?.toDomain() ?: throw ExpenseManagerClientException("While trying to retrieve accepted expenses we receive empty body")
        } catch (ex: HttpClientErrorException) {
            logger.warn(ex) { "Client side exception while trying to retrieve accepted expenses" }
            throw ExpenseManagerClientException(ex.message)
        } catch (ex: HttpServerErrorException) {
            logger.warn(ex) { "Server side exception while trying to retrieve accepted expenses" }
            throw RetryableExpenseManagerClientException(ex.message)
        } catch (ex: Exception) {
            logger.warn(ex) { "Unexpected exception while trying to retrieve accepted expenses" }
            throw ExpenseManagerClientException(ex.message)
        }
    }

    private fun resolveActivitiesAddress(groupId: String, clientFilterOptions: ClientFilterOptions?) =
        UriComponentsBuilder.fromUriString("${expenseManagerProperties.url}$INTERNAL/expenses/activities/groups/$groupId")
            .queryParamIfPresent("title", Optional.ofNullable(clientFilterOptions?.title))
            .queryParamIfPresent("status", Optional.ofNullable(clientFilterOptions?.status))
            .queryParamIfPresent("isCreator", Optional.ofNullable(clientFilterOptions?.creatorId))
            .queryParamIfPresent("sortedBy", Optional.ofNullable(clientFilterOptions?.sortedBy))
            .queryParamIfPresent("sortOrder", Optional.ofNullable(clientFilterOptions?.sortOrder))
            .build()
            .toUriString()

    private fun resolveAcceptedExpensesAddress(groupId: String, currency: String) =
        "${expenseManagerProperties.url}$INTERNAL/expenses/accepted/groups/$groupId?currency=$currency"

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
