package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.group.Group

interface GroupManagerClient {
    fun getGroups(userId: String): List<Group>
}

class GroupManagerClientException(override val message: String?) : RuntimeException()

class RetryableGroupManagerClientException(override val message: String?) : RuntimeException()
