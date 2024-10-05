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
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.paths.Paths.INTERNAL
import java.util.Optional

private fun createActivitiesUrl(groupId: String, clientFilterOptions: ClientFilterOptions) =
    UriComponentsBuilder.fromUriString("$INTERNAL/expenses/activities/groups/$groupId")
        .queryParamIfPresent("title", Optional.ofNullable(clientFilterOptions.title))
        .queryParamIfPresent("status", Optional.ofNullable(clientFilterOptions.status))
        .queryParamIfPresent("isCreator", Optional.ofNullable(clientFilterOptions.creatorId))
        .queryParam("sortedBy", clientFilterOptions.sortedBy)
        .queryParam("sortOrder", clientFilterOptions.sortOrder)
        .build()
        .toUriString()

fun stubExpenseManagerActivities(body: Any?, groupId: String, clientFilterOptions: ClientFilterOptions, statusCode: HttpStatusCode = OK) {
    wiremock.stubFor(
        get(createActivitiesUrl(groupId, clientFilterOptions))
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

private fun createAcceptedExpensesUrl(groupId: String) = "$INTERNAL/expenses/accepted/groups/$groupId"

fun stubAcceptedExpenses(body: Any?, groupId: String, statusCode: HttpStatusCode = OK) {
    wiremock.stubFor(
        get(createAcceptedExpensesUrl(groupId))
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
