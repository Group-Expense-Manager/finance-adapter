package pl.edu.agh.gem.integration.controller

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.finance.ActivitiesResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse
import pl.edu.agh.gem.util.createPaymentManagerActivitiesResponse

class InternalFinanceControllerIT(
    private val service: ServiceTestClient,

) : BaseIntegrationSpec({
    should("get internal activities") {
        // given
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID)

        // when
        val response = service.getInternalActivities(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        val ids = expenseManagerActivitiesResponse.expenses.map { it.expenseId } + paymentManagerActivitiesResponse.payments.map { it.paymentId }
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe expenseManagerActivitiesResponse.expenses.size + paymentManagerActivitiesResponse.payments.size
            activities.map { it.activityId } shouldContainExactly ids
        }
    }
},)
