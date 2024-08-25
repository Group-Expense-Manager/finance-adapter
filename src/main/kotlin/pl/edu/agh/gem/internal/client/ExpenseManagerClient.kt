package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions

interface ExpenseManagerClient {
    fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions): List<Activity>
}

class ExpenseManagerClientException(override val message: String?) : RuntimeException()

class RetryableExpenseManagerClientException(override val message: String?) : RuntimeException()
