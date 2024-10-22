package pl.edu.agh.gem.internal.solver.settlements

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settelment.Settlement
import pl.edu.agh.gem.util.getSolverTestData
import java.math.BigDecimal.ZERO

class SettlementSolverTest : ShouldSpec({

    context("generate settlement correctly using greedy algorithm") {
        withData(
            nameFn = { "for balances: $it" },
            getSolverTestData(100),

        ) { balances ->
            // given when
            val settlements = GreedySettlementsSolver.solve(balances)

            // then
            shouldNotThrowAny {
                checkCorrect(balances, settlements)
            }
        }
    }

    context("generate settlement correctly using set partition algorithm") {
        withData(
            nameFn = { "for balances: $it" },
            getSolverTestData(20),

        ) { balances ->
            // given when
            val settlements = SetPartitionSolver.solve(balances)

            // then
            shouldNotThrowAny {
                checkCorrect(balances, settlements)
            }
        }
    }

    context("generate settlement correctly using debt rounding pairing algorithm") {
        withData(
            nameFn = { "for balances: $it" },
            getSolverTestData(100),

        ) { balances ->
            // given when
            val settlements = DebtRoundingPairingSolver.solve(balances)

            // then
            shouldNotThrowAny {
                checkCorrect(balances, settlements)
            }
        }
    }
},)

private fun checkCorrect(balances: List<Balance>, result: List<Settlement>): Boolean {
    val userMap = balances.associate { it.userId to it.value }
    val userMap2 = balances.associate { it.userId to ZERO }.toMutableMap()
    result.forEach {
        userMap2[it.fromUserId] = userMap2[it.fromUserId]?.minus(it.value) ?: it.value.negate()
        userMap2[it.toUserId] = userMap2[it.toUserId]?.plus(it.value) ?: it.value
    }
    return userMap == userMap2
}
