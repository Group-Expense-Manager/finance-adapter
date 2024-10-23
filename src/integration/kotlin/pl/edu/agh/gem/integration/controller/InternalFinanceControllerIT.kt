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
import pl.edu.agh.gem.external.dto.finance.ReportsResponse
import pl.edu.agh.gem.external.dto.finance.toReportActivityMemberDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubAcceptedExpenses
import pl.edu.agh.gem.integration.ability.stubAcceptedPayments
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.finance.settlement.SettlementStatus.SAVED
import pl.edu.agh.gem.internal.model.finance.settlement.Settlements
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpenseDto
import pl.edu.agh.gem.util.createAcceptedExpenseParticipantDto
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import pl.edu.agh.gem.util.createAcceptedPaymentDto
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createCurrenciesDTO
import pl.edu.agh.gem.util.createFxDataDto
import pl.edu.agh.gem.util.createGroupResponse
import pl.edu.agh.gem.util.createMembersDTO
import pl.edu.agh.gem.util.createReportActivityMember
import pl.edu.agh.gem.util.createUserGroupsResponse
import java.math.BigDecimal.ZERO

class InternalFinanceControllerIT(
    private val service: ServiceTestClient,
    private val balancesRepository: BalancesRepository,
    private val settlementsRepository: SettlementsRepository,
) : BaseIntegrationSpec({
    should("get report") {
        val acceptedExpenseDto = createAcceptedExpenseDto(
            creatorId = USER_ID,
            participants = listOf(
                createAcceptedExpenseParticipantDto(
                    participantId = OTHER_USER_ID,
                    participantCost = "2".toBigDecimal(),
                ),
            ),
            amount = createAmountDto(
                value = "3".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = createFxDataDto(
                targetCurrency = CURRENCY_2,
                exchangeRate = "3".toBigDecimal(),
            ),
        )
        val acceptedPaymentDto = createAcceptedPaymentDto(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmountDto(
                value = "3".toBigDecimal(),
                currency = CURRENCY_2,
            ),
            fxData = null,
        )

        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID), USER_ID)
        stubGroupManagerGroupData(
            createGroupResponse(
                members = createMembersDTO(USER_ID, OTHER_USER_ID),
                groupCurrencies = createCurrenciesDTO(CURRENCY_1, CURRENCY_2),
            ),
            GROUP_ID,
        )

        stubAcceptedExpenses(
            createAcceptedExpensesResponse(
                expenses = listOf(
                    acceptedExpenseDto,
                ),
            ),
            GROUP_ID,
            CURRENCY_1,
        )
        stubAcceptedPayments(
            createAcceptedPaymentsResponse(
                payments = listOf(
                    acceptedPaymentDto,
                ),
            ),
            GROUP_ID,
            CURRENCY_1,

        )

        stubAcceptedExpenses(
            createAcceptedExpensesResponse(expenses = listOf()),
            GROUP_ID,
            CURRENCY_2,
        )
        stubAcceptedPayments(
            createAcceptedPaymentsResponse(payments = listOf()),
            GROUP_ID,
            CURRENCY_2,
        )

        balancesRepository.save(
            Balances(
                currency = CURRENCY_2,
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
                currency = CURRENCY_1,
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
                ),

            ),
        )

        // when
        val response = service.getReport(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ReportsResponse> {
            groupId shouldBe GROUP_ID
            reports shouldHaveSize 2
            reports.first().also {
                it.currency shouldBe CURRENCY_1
                it.activities.also { activities ->
                    activities.size shouldBe 2
                    activities.first().also { first ->
                        first.title shouldBe acceptedExpenseDto.title
                        first.value shouldBe "9".toBigDecimal()
                        first.date shouldBe acceptedExpenseDto.expenseDate
                        first.members.size shouldBe 2
                        first.members shouldContainExactlyInAnyOrder listOf(
                            createReportActivityMember(USER_ID, "6".toBigDecimal()),
                            createReportActivityMember(OTHER_USER_ID, "-6".toBigDecimal()),
                        ).map { it.toReportActivityMemberDto() }
                    }
                    activities.last().also { last ->
                        last.title shouldBe acceptedExpenseDto.title
                        last.value shouldBe "3".toBigDecimal()
                        last.date shouldBe acceptedExpenseDto.expenseDate
                        last.members.size shouldBe 2
                        last.members shouldContainExactlyInAnyOrder listOf(
                            createReportActivityMember(USER_ID, "3".toBigDecimal()),
                            createReportActivityMember(OTHER_USER_ID, "-3".toBigDecimal()),
                        ).map { it.toReportActivityMemberDto() }
                    }
                }
                it.balances.also { balances ->
                    balances.size shouldBe 2
                    balances.first().also { userBalance ->
                        userBalance.userId shouldBe OTHER_USER_ID
                        userBalance.value shouldBe "-3".toBigDecimal()
                    }
                    balances.last().also { userBalance ->
                        userBalance.userId shouldBe USER_ID
                        userBalance.value shouldBe "3".toBigDecimal()
                    }
                }
                it.settlements.also { settlements ->
                    settlements shouldHaveSize 1
                    settlements.first().also { firstSettlement ->
                        firstSettlement.fromUserId shouldBe USER_ID
                        firstSettlement.toUserId shouldBe OTHER_USER_ID
                        firstSettlement.value.toString() shouldBe "4"
                    }
                }
            }
            reports.last().also {
                it.currency shouldBe CURRENCY_2
                it.activities shouldHaveSize 0
                it.balances.also { balances ->
                    balances.size shouldBe 2
                    balances.first().also { userBalance ->
                        userBalance.userId shouldBe USER_ID
                        userBalance.value shouldBe "0".toBigDecimal()
                    }
                    balances.last().also { userBalance ->
                        userBalance.userId shouldBe OTHER_USER_ID
                        userBalance.value shouldBe "0".toBigDecimal()
                    }
                }
                it.settlements shouldHaveSize 0
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
},)
