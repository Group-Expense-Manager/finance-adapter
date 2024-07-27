package pl.edu.agh.gem.integration.ability

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatusCode
import org.springframework.web.util.UriComponentsBuilder
import pl.edu.agh.gem.headers.HeadersTestUtils.withAppContentType
import pl.edu.agh.gem.integration.environment.ProjectConfig.wiremock
import pl.edu.agh.gem.internal.model.expense.filter.ExpenseFilterOptions
import pl.edu.agh.gem.paths.Paths.INTERNAL
import java.util.*

private fun createActivitiesUrl(groupId: String, expenseFilterOptions: ExpenseFilterOptions) =
    UriComponentsBuilder.fromUriString("$INTERNAL/expenses/activities/groups/$groupId")
        .queryParamIfPresent("title", Optional.ofNullable(expenseFilterOptions.title))
        .queryParamIfPresent("status", Optional.ofNullable(expenseFilterOptions.status))
        .queryParamIfPresent("isCreator", Optional.ofNullable(expenseFilterOptions.creatorId))
        .queryParam("sortedBy", expenseFilterOptions.sortedBy)
        .queryParam("sortOrder", expenseFilterOptions.sortOrder)
        .build()
        .toUriString()

fun stubExpenseManagerActivities(body: Any?, groupId: String, expenseFilterOptions: ExpenseFilterOptions, statusCode: HttpStatusCode = OK) {
    wiremock.stubFor(
        get(createActivitiesUrl(groupId, expenseFilterOptions))
            .willReturn(
                aResponse()
                    .withStatus(statusCode.value())
                    .withAppContentType()
                    .withBody(
                        jacksonObjectMapper().registerModules(JavaTimeModule()).writeValueAsString(body),
                    ),
            ),
    )
}
