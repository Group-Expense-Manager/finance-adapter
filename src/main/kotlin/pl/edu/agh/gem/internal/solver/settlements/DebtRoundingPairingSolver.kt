package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settelment.Settlement
import java.math.BigDecimal

object DebtRoundingPairingSolver {
    fun solve(userBalances: List<Balance>): List<Settlement> {
        val users = userBalances.map { it.copy() }
        val creditors = users.filter { it.value > BigDecimal.ZERO }.sortedByDescending { it.value }.toMutableList()
        val debtors = users.filter { it.value < BigDecimal.ZERO }.sortedByDescending { -it.value }.toMutableList()
        val settlements = mutableListOf<Settlement>()

        while (creditors.isNotEmpty() && debtors.isNotEmpty()) {
            val creditor = creditors.removeAt(0)
            val debtor = debtors.removeAt(0)
            val amount = minOf(creditor.value, -debtor.value)

            settlements.add(Settlement(debtor.userId, creditor.userId, amount))

            val newCreditorBalance = creditor.value - amount
            val newDebtorBalance = debtor.value + amount

            if (newCreditorBalance > BigDecimal.ZERO) {
                creditors.add(creditor.copy(value = newCreditorBalance))
            } else if (newDebtorBalance < BigDecimal.ZERO) {
                debtors.add(debtor.copy(value = newDebtorBalance))
            }

            creditors.sortByDescending { it.value }
            debtors.sortByDescending { -it.value }
        }

        return settlements
    }
}
