package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.ExpenseManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.PaymentManagerClient
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.internal.sort.ActivityMerger
import java.math.BigDecimal

@Service
class FinanceService(
    private val expenseManagerClient: ExpenseManagerClient,
    private val paymentManagerClient: PaymentManagerClient,
    private val groupManagerClient: GroupManagerClient,
    private val balancesRepository: BalancesRepository,
    private val settlementsRepository: SettlementsRepository,
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

    fun blockSettlements(groupId: String, currency: String) {
        settlementsRepository.blockSettlements(groupId, currency)
    }

    fun saveBalances(balances: Balances) {
        balancesRepository.save(balances)
    }

    fun getBalances(groupId: String): List<Balances> {
        val balances = balancesRepository.getBalances(groupId).toMutableList()
        val groupDetails = groupManagerClient.getGroup(groupId)
        groupDetails.currencies.forEach { currency ->
            if (balances.none { it.currency == currency.code }) {
                balances += fetchBalances(groupId, currency.code)
            }
        }
        return balances.map { balance ->
            if (groupDetails.members.all { member -> balance.users.any { it.userId == member.id } }) {
                balance
            } else {
                val zeroBalanceList = groupDetails.members
                    .filter { member -> !balance.users.any { it.userId == member.id } }
                    .map { Balance(userId = it.id, value = BigDecimal.ZERO) }
                balance.copy(users = balance.users + zeroBalanceList)
            }
        }
    }

    fun fetchBalances(groupId: String, currency: String): Balances {
        val expenseBalanceList = expenseManagerClient.getAcceptedExpenses(groupId, currency)
            .flatMap { it.toBalanceList() }
        val paymentBalanceList = paymentManagerClient.getAcceptedPayments(groupId, currency)
            .flatMap { it.toBalanceList() }

        val zeroBalanceList = groupManagerClient.getGroup(groupId).members
            .filter { member -> !expenseBalanceList.any { it.userId == member.id } && !paymentBalanceList.any { it.userId == member.id } }
            .map { Balance(userId = it.id, value = BigDecimal.ZERO) }

        val userBalances = (expenseBalanceList + paymentBalanceList + zeroBalanceList)
            .groupBy { it.userId }
            .mapValues { it.value.sumOf { balance -> balance.value } }
            .map { (userId, value) -> Balance(userId = userId, value = value) }

        return Balances(
            groupId = groupId,
            currency = currency,
            users = userBalances,
        )
    }
}
