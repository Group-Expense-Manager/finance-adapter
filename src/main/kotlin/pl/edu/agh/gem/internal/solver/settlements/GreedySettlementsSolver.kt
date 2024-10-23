package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal.ZERO

object GreedySettlementsSolver {
    fun solve(userBalances: List<Balance>): List<Settlement> {
        val (debtors, creditors) = userBalances.partition { it.value < ZERO }.let {
            Pair(it.first.map { it.copy(value = it.value.negate()) }.toMutableList(), it.second.toMutableList())
        }

        val settlements = mutableListOf<Settlement>()
        var debtorIndex = 0
        var creditorIndex = 0

        while (debtorIndex < debtors.size && creditorIndex < creditors.size) {
            val debtor = debtors[debtorIndex]
            val creditor = creditors[creditorIndex]

            val transferAmount = minOf(debtor.value, creditor.value)
            settlements.add(Settlement(debtor.userId, creditor.userId, transferAmount))

            debtors[debtorIndex] = debtor.copy(value = debtor.value - transferAmount)
            creditors[creditorIndex] = creditor.copy(value = creditor.value - transferAmount)

            if (debtors[debtorIndex].value.compareTo(ZERO) == 0) {
                debtorIndex++
            }

            if (creditors[creditorIndex].value.compareTo(ZERO) == 0) {
                creditorIndex++
            }
        }

        return settlements
    }
}
