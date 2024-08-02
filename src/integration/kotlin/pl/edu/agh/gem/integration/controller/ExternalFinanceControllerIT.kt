package pl.edu.agh.gem.integration.controller

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
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubExpenseManagerActivities
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.util.createExpenseFilterOptions
import pl.edu.agh.gem.util.createExpenseManagerActivitiesResponse
import pl.edu.agh.gem.util.createUserGroupsResponse

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
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, createExpenseFilterOptions())
        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe expenseManagerActivitiesResponse.expenses.size
            activities.map { it.activityId } shouldContainExactly expenseManagerActivitiesResponse.expenses.map { it.expenseId }
        }
    }

    should("return expense activities when type is EXPENSE") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, createExpenseFilterOptions())
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
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        val expenseManagerActivitiesResponse = createExpenseManagerActivitiesResponse()
        stubExpenseManagerActivities(expenseManagerActivitiesResponse, GROUP_ID, createExpenseFilterOptions())
        // when
        val response = service.getActivities(user, GROUP_ID, type = PAYMENT)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ActivitiesResponse> {
            groupId shouldBe GROUP_ID
            activities.size shouldBe 0
        }
    }

    should("return  INTERNAL_SERVER_ERROR when fetching data from expenseManager or paymentManager fails") {
        // given
        val user = createGemUser(USER_ID)
        stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID), USER_ID)
        stubExpenseManagerActivities(null, GROUP_ID, createExpenseFilterOptions(), INTERNAL_SERVER_ERROR)
        // when
        val response = service.getActivities(user, GROUP_ID)

        // then
        response shouldHaveHttpStatus INTERNAL_SERVER_ERROR
    }
},)
