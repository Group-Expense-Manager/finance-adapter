package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.sort.ActivityMerger
import java.math.BigDecimal

@Service
class FinanceService(
    private val expenseManagerClient: ExpenseManagerClient,
    private val paymentManagerClient: PaymentManagerClient,
    private val groupManagerClient: GroupManagerClient,
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

    private fun buildBalancesMap(groupId: String): MutableMap<String, MutableMap<String, BigDecimal>> {
        val groupData = groupManagerClient.getGroup(groupId)
        val currencies = groupData.currencies.map { it.code }
        val memberIds = groupData.members.members.map { it.id }

        return currencies.associateWith { _ ->
            memberIds.associateWith { BigDecimal.ZERO }.toMutableMap()
        }.toMutableMap()
    }

    fun getBalances(groupId: String): Balances {
        val balances = buildBalancesMap(groupId)

        val expenseBalanceElements = expenseManagerClient.getAcceptedExpenses(groupId).flatMap { it.toBalanceElements() }
        val paymentBalanceElements = paymentManagerClient.getAcceptedPayments(groupId).flatMap { it.toBalanceElements() }

        (expenseBalanceElements + paymentBalanceElements).forEach { (currency, userId, value) ->
            balances[currency]?.let { innerMap ->
                innerMap[userId]?.let { currentValue ->
                    innerMap[userId] = currentValue + value
                }
            }
        }

        return balances
    }
}
