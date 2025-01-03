package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubAcceptedPayments
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClientException
import pl.edu.agh.gem.internal.client.RetryablePaymentManagerClientException
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.OTHER_PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.PAYMENT_ID
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import pl.edu.agh.gem.util.createClientFilterOptions
import pl.edu.agh.gem.util.createPaymentManagerActivitiesResponse

class PaymentManagerClientIT(
    private val paymentManagerClient: PaymentManagerClient,
) : BaseIntegrationSpec({

    should("get activities") {
        // given
        val paymentFilterOptions = createClientFilterOptions()
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID, paymentFilterOptions)

        // when
        val result = paymentManagerClient.getActivities(GROUP_ID, paymentFilterOptions)

        // then
        result.map { it.activityId } shouldContainExactly listOf(PAYMENT_ID, OTHER_PAYMENT_ID)
    }

    should("throw PaymentManagerClientException when we send bad activities request") {
        // given
        val paymentFilterOptions = createClientFilterOptions()
        stubPaymentManagerActivities(createPaymentManagerActivitiesResponse(), GROUP_ID, paymentFilterOptions, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<PaymentManagerClientException> {
            paymentManagerClient.getActivities(GROUP_ID, paymentFilterOptions)
        }
    }

    should("throw RetryableGroupManagerClientException when sending activities request and client has internal error") {
        // given
        val paymentFilterOptions = createClientFilterOptions()
        stubPaymentManagerActivities(createPaymentManagerActivitiesResponse(), GROUP_ID, paymentFilterOptions, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryablePaymentManagerClientException> {
            paymentManagerClient.getActivities(GROUP_ID, paymentFilterOptions)
        }
    }

    should("get accepted payments") {
        // given
        val acceptedPaymentsResponse = createAcceptedPaymentsResponse()
        stubAcceptedPayments(acceptedPaymentsResponse, GROUP_ID)

        // when
        val result = paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_1)

        // then
        result shouldBe acceptedPaymentsResponse.toDomain()
    }

    should("throw PaymentManagerClientException when we send bad accepted payments request") {
        // given
        val acceptedPaymentsResponse = createAcceptedPaymentsResponse()
        stubAcceptedPayments(acceptedPaymentsResponse, GROUP_ID, CURRENCY_1, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<PaymentManagerClientException> {
            paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_1)
        }
    }

    should("throw RetryableGroupManagerClientException when sending accepted payments request and client has internal error") {
        // given
        val acceptedPaymentsResponse = createAcceptedPaymentsResponse()
        stubAcceptedPayments(acceptedPaymentsResponse, GROUP_ID, CURRENCY_1, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryablePaymentManagerClientException> {
            paymentManagerClient.getAcceptedPayments(GROUP_ID, CURRENCY_1)
        }
    }
},)
