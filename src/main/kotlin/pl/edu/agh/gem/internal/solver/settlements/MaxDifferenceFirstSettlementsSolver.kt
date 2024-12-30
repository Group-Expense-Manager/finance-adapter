package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal.ZERO
import java.util.PriorityQueue

object MaxDifferenceFirstSettlementsSolver {
    fun solve(userBalances: List<Balance>): List<Settlement> {
        val creditors = PriorityQueue(compareByDescending<Balance> { it.value })
        creditors.addAll(userBalances.filter { it.value > ZERO })

        val debtors = PriorityQueue(compareBy<Balance> { it.value })
        debtors.addAll(userBalances.filter { it.value < ZERO })

        val settlements = mutableListOf<Settlement>()

        while (creditors.isNotEmpty() && debtors.isNotEmpty()) {
            val creditor = creditors.poll()
            val debtor = debtors.poll()
            val amount = minOf(creditor.value, debtor.value.negate())

            settlements.add(Settlement(debtor.userId, creditor.userId, amount))

            val newCreditor = creditor.copy(value = creditor.value - amount)
            val newDebtor = debtor.copy(value = debtor.value + amount)

            if (newCreditor.value > ZERO) {
                creditors.add(newCreditor)
            } else if (newDebtor.value < ZERO) {
                debtors.add(newDebtor)
            }
        }

        return settlements
    }
}
