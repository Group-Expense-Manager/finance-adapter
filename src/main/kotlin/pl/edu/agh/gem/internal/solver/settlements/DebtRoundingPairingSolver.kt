package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal.ZERO

object DebtRoundingPairingSolver {
    fun solve(userBalances: List<Balance>): List<Settlement> {
        val users = userBalances.map { it.copy() }
        val creditors = users.filter { it.value > ZERO }.sortedByDescending { it.value }.toMutableList()
        val debtors = users.filter { it.value < ZERO }.sortedByDescending { -it.value }.toMutableList()
        val settlements = mutableListOf<Settlement>()

        while (creditors.isNotEmpty() && debtors.isNotEmpty()) {
            val creditor = creditors.removeAt(0)
            val debtor = debtors.removeAt(0)
            val amount = minOf(creditor.value, debtor.value.negate())

            settlements.add(Settlement(debtor.userId, creditor.userId, amount))

            val newCreditorBalance = creditor.value - amount
            val newDebtorBalance = debtor.value + amount

            if (newCreditorBalance > ZERO) {
                creditors.add(creditor.copy(value = newCreditorBalance))
            } else if (newDebtorBalance < ZERO) {
                debtors.add(debtor.copy(value = newDebtorBalance))
            }

            creditors.sortByDescending { it.value }
            debtors.sortBy { it.value }
        }

        return settlements
    }
}
