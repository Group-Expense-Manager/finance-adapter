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
import pl.edu.agh.gem.config.PaymentManagerProperties
import pl.edu.agh.gem.external.dto.payment.PaymentManagerActivitiesResponse
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClientException
import pl.edu.agh.gem.internal.client.RetryablePaymentManagerClientException
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.paths.Paths.INTERNAL
import java.util.Optional

@Component
class RestPaymentManagerClient(
    @Qualifier("PaymentManagerRestTemplate") val restTemplate: RestTemplate,
    val paymentManagerProperties: PaymentManagerProperties,
) : PaymentManagerClient {

    @Retry(name = "paymentManager")
    override fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions): List<Activity> {
        return try {
            restTemplate.exchange(
                resolveActivitiesAddress(groupId, clientFilterOptions),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                PaymentManagerActivitiesResponse::class.java,
            ).body?.toDomain() ?: throw PaymentManagerClientException("While trying to retrieve activities we receive empty body")
        } catch (ex: HttpClientErrorException) {
            logger.warn(ex) { "Client side exception while trying to retrieve activities" }
            throw PaymentManagerClientException(ex.message)
        } catch (ex: HttpServerErrorException) {
            logger.warn(ex) { "Server side exception while trying to retrieve activities" }
            throw RetryablePaymentManagerClientException(ex.message)
        } catch (ex: Exception) {
            logger.warn(ex) { "Unexpected exception while trying to retrieve activities" }
            throw PaymentManagerClientException(ex.message)
        }
    }

    private fun resolveActivitiesAddress(groupId: String, clientFilterOptions: ClientFilterOptions) =
        UriComponentsBuilder.fromUriString("${paymentManagerProperties.url}$INTERNAL/payments/activities/groups/$groupId")
            .queryParamIfPresent("title", Optional.ofNullable(clientFilterOptions.title))
            .queryParamIfPresent("status", Optional.ofNullable(clientFilterOptions.status))
            .queryParamIfPresent("isCreator", Optional.ofNullable(clientFilterOptions.creatorId))
            .queryParam("sortedBy", clientFilterOptions.sortedBy)
            .queryParam("sortOrder", clientFilterOptions.sortOrder)
            .build()
            .toUriString()

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}