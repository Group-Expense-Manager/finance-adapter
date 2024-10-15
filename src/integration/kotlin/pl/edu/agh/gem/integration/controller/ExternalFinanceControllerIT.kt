package pl.edu.agh.gem.integration.controller

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.FORBIDDEN
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
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.integration.ability.stubPaymentManagerActivities
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
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
    private val balancesRepository: BalancesRepository,
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
