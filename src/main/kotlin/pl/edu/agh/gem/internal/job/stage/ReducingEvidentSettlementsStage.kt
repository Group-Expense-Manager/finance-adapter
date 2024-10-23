package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_ZERO_BALANCES
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import java.math.BigDecimal.ZERO

@Component
class ReducingEvidentSettlementsStage : ProcessingStage() {
    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Reducing evident settlements for job: $reconciliationJob" }

        val (debtors, creditors) = reconciliationJob.balances.partition { it.value < ZERO }.let {
            Pair(it.first, it.second.toMutableList())
        }

        val newBalances = mutableListOf<Balance>()
        val settlements = reconciliationJob.settlements.toMutableList()

        debtors.forEach { currentBalance ->
            val matchingBalance = creditors.find { it.value.compareTo(currentBalance.value.negate()) == 0 }

            if (matchingBalance != null) {
                creditors.remove(matchingBalance)
                settlements.add(
                    Settlement(
                        fromUserId = currentBalance.userId,
                        toUserId = matchingBalance.userId,
                        value = currentBalance.value,
                    ),
                )
            } else {
                newBalances.add(currentBalance)
            }
        }

        val reducedJob = reconciliationJob.copy(
            settlements = settlements,
            balances = newBalances + creditors,
        )

        return nextStage(reducedJob, REDUCING_ZERO_BALANCES)
    }
}
