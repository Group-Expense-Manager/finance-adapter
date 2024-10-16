package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.expense.AcceptedExpense
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions

interface ExpenseManagerClient {
    fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions): List<Activity>
    fun getAcceptedExpenses(groupId: String, currency: String): List<AcceptedExpense>
}

class ExpenseManagerClientException(override val message: String?) : RuntimeException()

class RetryableExpenseManagerClientException(override val message: String?) : RuntimeException()
