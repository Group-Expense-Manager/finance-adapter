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
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import java.util.Optional

private fun createActivitiesUrl(groupId: String, clientFilterOptions: ClientFilterOptions?) =
    UriComponentsBuilder.fromUriString("$INTERNAL/payments/activities/groups/$groupId")
        .queryParamIfPresent("title", Optional.ofNullable(clientFilterOptions?.title))
        .queryParamIfPresent("status", Optional.ofNullable(clientFilterOptions?.status))
        .queryParamIfPresent("creatorId", Optional.ofNullable(clientFilterOptions?.creatorId))
        .queryParamIfPresent("currency", Optional.ofNullable(clientFilterOptions?.currency))
        .queryParamIfPresent("sortedBy", Optional.ofNullable(clientFilterOptions?.sortedBy))
        .queryParamIfPresent("sortOrder", Optional.ofNullable(clientFilterOptions?.sortOrder))
        .build()
        .toUriString()

fun stubPaymentManagerActivities(body: Any?, groupId: String, clientFilterOptions: ClientFilterOptions? = null, statusCode: HttpStatusCode = OK) {
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

private fun createAcceptedPaymentsUrl(groupId: String, currency: String) = "$INTERNAL/payments/accepted/groups/$groupId?currency=$currency"

fun stubAcceptedPayments(body: Any?, groupId: String, currency: String = CURRENCY_1, statusCode: HttpStatusCode = OK) {
    wiremock.stubFor(
        get(createAcceptedPaymentsUrl(groupId, currency))
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
