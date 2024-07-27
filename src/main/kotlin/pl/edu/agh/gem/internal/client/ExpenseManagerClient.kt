package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.expense.filter.ExpenseFilterOptions
import pl.edu.agh.gem.internal.model.finance.Activity

interface ExpenseManagerClient {
    fun getActivities(groupId: String, expenseFilterOptions: ExpenseFilterOptions): List<Activity>
}

class ExpenseManagerClientException(override val message: String?) : RuntimeException()

class RetryableExpenseManagerClientException(override val message: String?) : RuntimeException()
