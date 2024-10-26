package pl.edu.agh.gem.integration.controller

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.finance.InternalActivitiesResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createCurrenciesDTO
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse
import pl.edu.agh.gem.util.createExpenseManagerActivityDto
import pl.edu.agh.gem.util.createFxDataDto
import pl.edu.agh.gem.util.createGroupResponse
import pl.edu.agh.gem.util.createMembersDTO
import pl.edu.agh.gem.util.createPaymentManagerActivitiesResponse
import pl.edu.agh.gem.util.createPaymentManagerActivityDto

class InternalFinanceControllerIT(
    private val service: ServiceTestClient,

) : BaseIntegrationSpec({
    should("get internal activities") {
        // given
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse(
            expenses = listOf(
                createExpenseManagerActivityDto(
                    expenseId = "1",
                    amount = createAmountDto(currency = CURRENCY_1),
                    fxData = null,
                ),
            ),
        )
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse(
            payments = listOf(
                createPaymentManagerActivityDto(
                    paymentId = "2",
                    amount = createAmountDto(currency = CURRENCY_2),
                    fxData = createFxDataDto(
                        targetCurrency = CURRENCY_1,
                    ),
                ),
            ),
        )
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID)
        stubGroupManagerGroupData(
            createGroupResponse(
                members = createMembersDTO(USER_ID, OTHER_USER_ID),
                groupCurrencies = createCurrenciesDTO(CURRENCY_1, CURRENCY_2),
            ),
            GROUP_ID,
        )
        // when
        val response = service.getInternalActivities(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<InternalActivitiesResponse> {
            groupId shouldBe GROUP_ID
            groupActivities shouldHaveSize 2
            groupActivities.first().also { first ->
                first.currency shouldBe CURRENCY_1
                first.activities.map { it.id } shouldContainExactlyInAnyOrder listOf("1", "2")
            }

            groupActivities.last().also { last ->
                last.currency shouldBe CURRENCY_2
                last.activities shouldHaveSize 0
            }
        }
    }
},)
