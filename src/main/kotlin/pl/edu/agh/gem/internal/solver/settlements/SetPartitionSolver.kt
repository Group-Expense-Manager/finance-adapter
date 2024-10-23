package pl.edu.agh.gem.internal.solver.settlements

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal.ZERO

object SetPartitionSolver {

    private const val MIN_SET_SIZE = 2
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

        val tuples = findPartitionsWithZeroSum(getTuplePartitions(suffixElements))
        if (tuples.isEmpty()) {
            return fixedParts + listOf(suffixElements)
        }

        var maxLen = -1
        var bestResult: List<List<Balance>> = mutableListOf()

        for (suffixPartition in tuples) {
            val subPartitions = getAllPartitionsRecursive(fixedParts + listOf(suffixPartition.first), suffixPartition.second)
            if (subPartitions.size > maxLen) {
                maxLen = subPartitions.size
                bestResult = subPartitions
            }
        }

        return bestResult
    }

    private fun getTuplePartitions(elements: List<Balance>): List<Pair<List<Balance>, List<Balance>>> {
        val result = mutableListOf<Pair<List<Balance>, List<Balance>>>()

        if (elements.size < MIN_SET_SIZE) {
            return result
        }

        for (pattern in 1 until (1 shl (elements.size - 1))) {
            val resultSets = mutableListOf(mutableListOf(), mutableListOf<Balance>())
            resultSets[0].add(elements[0])

            for (index in 1 until elements.size) {
                resultSets[(pattern shr (index - 1)) and 1].add(elements[index])
            }

            result.add(Pair(resultSets[0], resultSets[1]))
        }

        return result
    }

    private fun findPartitionsWithZeroSum(partitions: List<Pair<List<Balance>, List<Balance>>>): List<Pair<List<Balance>, List<Balance>>> {
        return partitions.filter { partition -> partition.first.sumOf { it.value }.compareTo(ZERO) == 0 }
    }
}
