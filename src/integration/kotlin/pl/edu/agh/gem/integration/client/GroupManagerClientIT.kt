package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClientException
import pl.edu.agh.gem.internal.client.RetryableGroupManagerClientException
import pl.edu.agh.gem.util.createUserGroupsResponse

class GroupManagerClientIT(
    private val groupManagerClient: GroupManagerClient,
) : BaseIntegrationSpec({

    should("get user groups") {
        // given
        val userGroups = arrayOf(GROUP_ID, OTHER_GROUP_ID)
        val userGroupsResponse = createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID)
        stubGroupManagerUserGroups(userGroupsResponse, USER_ID)

        // when
        val result = groupManagerClient.getGroups(USER_ID)

        // then
        result.all {
            it.groupId in userGroups
        }
    }

    should("throw GroupManagerClientException when we send bad request") {
        // given
        stubGroupManagerUserGroups(createUserGroupsResponse(), USER_ID, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<GroupManagerClientException> {
            groupManagerClient.getGroups(USER_ID)
        }
    }

    should("throw RetryableGroupManagerClientException when client has internal error") {
        // given
        stubGroupManagerUserGroups(createUserGroupsResponse(), USER_ID, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableGroupManagerClientException> {
            groupManagerClient.getGroups(USER_ID)
        }
    }
},)
