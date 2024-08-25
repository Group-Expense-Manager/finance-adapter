package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.sort.ActivityMerger

@Service
class FinanceService(
    private val expenseManagerClient: ExpenseManagerClient,
    private val paymentManagerClient: PaymentManagerClient,
) {
    fun getActivities(groupId: String, filterOptions: FilterOptions): List<Activity> {
        return when (filterOptions.type) {
            EXPENSE -> expenseManagerClient.getActivities(groupId, filterOptions.toClientFilterOptions())
            PAYMENT -> paymentManagerClient.getActivities(groupId, filterOptions.toClientFilterOptions())
            else -> {
                val activityMerger = ActivityMerger(filterOptions)
                val expenseActivities = expenseManagerClient.getActivities(groupId, filterOptions.toClientFilterOptions())
                val paymentActivities = paymentManagerClient.getActivities(groupId, filterOptions.toClientFilterOptions())
                activityMerger.merge(expenseActivities, paymentActivities)
            }
        }
    }
}
