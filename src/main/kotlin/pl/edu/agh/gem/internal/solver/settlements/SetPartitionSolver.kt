package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal.ZERO

object SetPartitionSolver {

    private const val MIN_COMPONENT_SIZE = 3

    fun solve(userBalances: List<Balance>): List<Settlement> {
        val settlements = mutableListOf<Settlement>()
        val partitions = getAllPartitions(userBalances)

        for (partition in partitions) {
            settlements.addAll(GreedySettlementsSolver.solve(partition))
        }

        return settlements
    }

    private fun getAllPartitions(elements: List<Balance>): List<List<Balance>> {
        return getAllPartitionsRecursive(listOf(), elements)
    }

    private fun getAllPartitionsRecursive(fixedParts: List<List<Balance>>, suffixElements: List<Balance>): List<List<Balance>> {
        if (suffixElements.size <= MIN_COMPONENT_SIZE) {
            return fixedParts + listOf(suffixElements)
        }

        val twoSubsetPartitions = findAllTwoSubsetPartitions(suffixElements)
            .filter { partition -> partition.first.sumOf { it.value }.compareTo(ZERO) == 0 }

        if (twoSubsetPartitions.isEmpty()) {
            return fixedParts + listOf(suffixElements)
        }

        return twoSubsetPartitions
            .map { getAllPartitionsRecursive(fixedParts + listOf(it.first), it.second) }
            .maxBy { it.size }
    }

    private fun findAllTwoSubsetPartitions(balances: List<Balance>): List<Pair<List<Balance>, List<Balance>>> {
        val result = mutableListOf<Pair<List<Balance>, List<Balance>>>()

        for (pattern in 1 until (1 shl (balances.size - 1))) {
            val (first, second) = listOf(mutableListOf(balances[0]), mutableListOf())

            for (index in 1 until balances.size) {
                if (((pattern shr (index - 1)) and 1) == 0) {
                    first.add(balances[index])
                } else {
                    second.add(balances[index])
                }
            }

            result.add(Pair(first, second))
        }

        return result
    }
}
