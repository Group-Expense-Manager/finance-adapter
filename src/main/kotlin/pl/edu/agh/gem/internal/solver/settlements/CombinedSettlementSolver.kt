package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settelment.Settlement

object CombinedSettlementSolver {
    private const val BOTTOM_THRESHOLD = 5
    private const val TOP_THRESHOLD = 20

    fun solve(userBalances: List<Balance>): List<Settlement> {
        val settlements = mutableListOf<Settlement>()
        if (userBalances.size <= BOTTOM_THRESHOLD) {
            settlements.addAll(GreedySettlementsSolver.solve(userBalances))
        } else if (userBalances.size <= TOP_THRESHOLD) {
            settlements.addAll(SetPartitionSolver.solve(userBalances))
        } else {
            settlements.addAll(DebtRoundingPairingSolver.solve(userBalances))
        }

        return settlements
    }
}
