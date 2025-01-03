package pl.edu.agh.gem.integration.controller

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.finance.BalancesResponse
import pl.edu.agh.gem.external.dto.finance.InternalActivitiesResponse
import pl.edu.agh.gem.external.dto.finance.SettlementsResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.finance.settlement.SettlementStatus.SAVED
import pl.edu.agh.gem.internal.model.finance.settlement.Settlements
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
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
import java.math.BigDecimal.ZERO

class InternalFinanceControllerIT(
    private val service: ServiceTestClient,
    private val balancesRepository: BalancesRepository,
    private val settlementsRepository: SettlementsRepository,
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

    should("return internal balances") {
        // given
        stubGroupManagerGroupData(
            createGroupResponse(
                members = createMembersDTO(USER_ID, OTHER_USER_ID),
                groupCurrencies = createCurrenciesDTO(CURRENCY_1, CURRENCY_2),
            ),
            GROUP_ID,
        )
        balancesRepository.save(
            Balances(
                currency = CURRENCY_1,
                groupId = GROUP_ID,
                users = listOf(
                    Balance(
                        userId = USER_ID,
                        value = "0".toBigDecimal(),
                    ),
                    Balance(
                        userId = OTHER_USER_ID,
                        value = "0".toBigDecimal(),
                    ),
                ),
            ),
        )
        balancesRepository.save(
            Balances(
                currency = CURRENCY_2,
                groupId = GROUP_ID,
                users = listOf(
                    Balance(
                        userId = USER_ID,
                        value = "3".toBigDecimal(),
                    ),
                    Balance(
                        userId = OTHER_USER_ID,
                        value = "-3".toBigDecimal(),
                    ),
                ),
            ),
        )

        // when
        val response = service.getInternalBalances(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<BalancesResponse> {
            groupId shouldBe GROUP_ID
            balances shouldHaveSize 2
            balances.first().also { first ->
                first.currency shouldBe CURRENCY_1
                first.userBalances.also { userBalances ->
                    userBalances.map { it.userId } shouldContainExactly listOf(USER_ID, OTHER_USER_ID)
                    userBalances.shouldForAll { userBalance ->
                        userBalance.value shouldBe ZERO
                    }
                }
            }

            balances.last().also { last ->
                last.currency shouldBe CURRENCY_2
                last.userBalances.also { userBalances ->
                    userBalances.first().also { userBalance ->
                        userBalance.userId shouldBe OTHER_USER_ID
                        userBalance.value shouldBe "-3".toBigDecimal()
                    }
                    userBalances.last().also { userBalance ->
                        userBalance.userId shouldBe USER_ID
                        userBalance.value shouldBe "3".toBigDecimal()
                    }
                }
            }
        }
    }

    should("return internal settlements") {
        // given
        stubGroupManagerGroupData(
            createGroupResponse(
                members = createMembersDTO(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID),
                groupCurrencies = createCurrenciesDTO(CURRENCY_1, CURRENCY_2),
            ),
            GROUP_ID,
        )
        settlementsRepository.save(
            Settlements(
                currency = CURRENCY_1,
                groupId = GROUP_ID,
                status = SAVED,
                settlements = listOf(
                    Settlement(
                        fromUserId = USER_ID,
                        toUserId = OTHER_USER_ID,
                        value = "4".toBigDecimal(),
                    ),
                    Settlement(
                        fromUserId = ANOTHER_USER_ID,
                        toUserId = OTHER_USER_ID,
                        value = "7".toBigDecimal(),
                    ),
                ),

            ),
        )

        // when
        val response = service.getInternalSettlements(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<SettlementsResponse> {
            groupId shouldBe GROUP_ID
            settlements shouldHaveSize 2
            settlements.first().also { first ->
                first.currency shouldBe CURRENCY_1
                first.status shouldBe SAVED
                first.settlements.also { settlements ->
                    settlements shouldHaveSize 2
                    settlements.first().also { firstSettlement ->
                        firstSettlement.fromUserId shouldBe USER_ID
                        firstSettlement.toUserId shouldBe OTHER_USER_ID
                        firstSettlement.value.toString() shouldBe "4"
                    }
                    settlements.last().also { lastSettlement ->
                        lastSettlement.fromUserId shouldBe ANOTHER_USER_ID
                        lastSettlement.toUserId shouldBe OTHER_USER_ID
                        lastSettlement.value.toString() shouldBe "7"
                    }
                }
            }

            settlements.last().also { last ->
                last.currency shouldBe CURRENCY_2
                last.status shouldBe SAVED
                last.settlements.also { settlements ->
                    settlements shouldHaveSize 0
                }
            }
        }
    }
},)
