package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions

@Service
class FinanceService(
    private val expenseManagerClient: ExpenseManagerClient,
) {
    fun getActivities(groupId: String, filterOptions: FilterOptions): List<Activity> {
        return when (filterOptions.type) {
            PAYMENT -> listOf()
            EXPENSE -> expenseManagerClient.getActivities(groupId, filterOptions.toExpenseFilterOptions())
            null -> expenseManagerClient.getActivities(groupId, filterOptions.toExpenseFilterOptions()) + listOf()
        }
    }
}
