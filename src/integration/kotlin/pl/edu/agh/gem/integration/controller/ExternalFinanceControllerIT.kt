package pl.edu.agh.gem.integration.controller

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveErrors
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.external.dto.finance.ActivitiesResponse
import pl.edu.agh.gem.external.dto.finance.BalancesResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubAcceptedExpenses
import pl.edu.agh.gem.integration.ability.stubAcceptedPayments
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpenseDto
import pl.edu.agh.gem.util.createAcceptedExpenseParticipantDto
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import pl.edu.agh.gem.util.createAcceptedPaymentDto
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createClientFilterOptions
import pl.edu.agh.gem.util.createCurrenciesDTO
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse
import pl.edu.agh.gem.util.createGroupResponse
import pl.edu.agh.gem.util.createMembersDTO
import pl.edu.agh.gem.util.createPaymentManagerActivitiesResponse
import pl.edu.agh.gem.util.createUserGroupsResponse
import java.math.BigDecimal

class ExternalFinanceControllerIT(
    private val service: ServiceTestClient,
) : BaseIntegrationSpec({

    should("return forbidden when user doesn't have access") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(OTHER_GROUP_ID), USER_ID)

        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus FORBIDDEN
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserWithoutGroupAccessException::class.simpleName
        }
    }

    should("return all activities when no filters applied") {
        // given
        val user = createGemUser(USER_ID)
        val clientFilterOptions = createClientFilterOptions()
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, clientFilterOptions)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID, clientFilterOptions)

        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        val ids = expenseManagerActivitiesResponse.expenses.map { it.expenseId } + paymentManagerActivitiesResponse.payments.map { it.paymentId }
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe expenseManagerActivitiesResponse.expenses.size + paymentManagerActivitiesResponse.payments.size
            activities.map { it.activityId } shouldContainExactly ids
        }
    }

    should("return expense activities when type is EXPENSE") {

        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, createClientFilterOptions())
        // when
        val response = service.getActivities(user, GROUP_ID, type = EXPENSE)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe expenseManagerActivitiesResponse.expenses.size
            activities.map { it.activityId } shouldContainExactly expenseManagerActivitiesResponse.expenses.map { it.expenseId }
        }
    }

    should("return payment activities when type is PAYMENT") {
        // given
        val user = createGemUser(USER_ID)
        val clientFilterOptions = createClientFilterOptions()
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID, clientFilterOptions)

        // when
        val response = service.getActivities(user, GROUP_ID, type = PAYMENT)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe paymentManagerActivitiesResponse.payments.size
            activities.map { it.activityId } shouldContainExactly paymentManagerActivitiesResponse.payments.map { it.paymentId }
        }
    }

    should("return INTERNAL_SERVER_ERROR when fetching data from expenseManager fails") {
        // given
        val user = createGemUser(USER_ID)
        val clientFilterOptions = createClientFilterOptions()
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, clientFilterOptions, INTERNAL_SERVER_ERROR)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID, clientFilterOptions)

        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }
    should("return INTERNAL_SERVER_ERROR when fetching data from paymentManager fails") {
        // given
        val user = createGemUser(USER_ID)
        val clientFilterOptions = createClientFilterOptions()
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, clientFilterOptions)
        val paymentManagerActivitiesResponse = createPaymentManagerActivitiesResponse()
        stubPaymentManagerActivities(paymentManagerActivitiesResponse, GROUP_ID, clientFilterOptions, INTERNAL_SERVER_ERROR)

        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }

    should("return forbidden when user doesn't have access") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(OTHER_GROUP_ID), USER_ID)

        // when
        val response = service.getBalances(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus FORBIDDEN
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserWithoutGroupAccessException::class.simpleName
        }
    }

    should("return INTERNAL_SERVER_ERROR when fetching data from groupManager fails") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID), USER_ID)
        stubGroupManagerUserGroups(createGroupResponse(), GROUP_ID, INTERNAL_SERVER_ERROR)

        // when
        val response = service.getBalances(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }

    should("return INTERNAL_SERVER_ERROR when fetching data from expenseManager fails") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID), USER_ID)
        stubGroupManagerUserGroups(createGroupResponse(), GROUP_ID)
        stubAcceptedExpenses(createAcceptedExpensesResponse(), GROUP_ID, INTERNAL_SERVER_ERROR)
        // when
        val response = service.getBalances(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }

    should("return INTERNAL_SERVER_ERROR when fetching data from expenseManager fails") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID), USER_ID)
        stubGroupManagerUserGroups(createGroupResponse(), GROUP_ID)
        stubAcceptedExpenses(createAcceptedExpensesResponse(), GROUP_ID)
        stubAcceptedPayments(createAcceptedPaymentsResponse(), GROUP_ID, INTERNAL_SERVER_ERROR)
        // when
        val response = service.getBalances(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }

    should("return balances") {
        // given
        val user = createGemUser(USER_ID)
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
                    createAcceptedExpenseDto(
                        creatorId = USER_ID,
                        participants = listOf(
                            createAcceptedExpenseParticipantDto(
                                participantId = OTHER_USER_ID,
                                participantCost = "2".toBigDecimal(),
                            ),
                        ),
                        baseCurrency = CURRENCY_1,
                        targetCurrency = CURRENCY_2,
                        exchangeRate = "3".toBigDecimal(),
                    ),
                ),
            ),
            GROUP_ID,
        )
        stubAcceptedPayments(
            createAcceptedPaymentsResponse(
                payments = listOf(
                    createAcceptedPaymentDto(
                        creatorId = OTHER_USER_ID,
                        recipientId = USER_ID,
                        amount = createAmountDto(
                            value = "3".toBigDecimal(),
                            currency = CURRENCY_2,
                        ),
                        fxData = null,
                    ),
                ),
            ),
            GROUP_ID,
        )
        // when
        val response = service.getBalances(user, GROUP_ID)

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
                        userBalance.balance shouldBe BigDecimal.ZERO
                    }
                }
            }

            balances.last().also { last ->
                last.currency shouldBe CURRENCY_2
                last.userBalances.also { userBalances ->
                    userBalances.first().also { userBalance ->
                        userBalance.userId shouldBe USER_ID
                        userBalance.balance shouldBe "3".toBigDecimal()
                    }
                    userBalances.last().also { userBalance ->
                        userBalance.userId shouldBe OTHER_USER_ID
                        userBalance.balance shouldBe "-3".toBigDecimal()
                    }
                }
            }
        }
    }
},)
