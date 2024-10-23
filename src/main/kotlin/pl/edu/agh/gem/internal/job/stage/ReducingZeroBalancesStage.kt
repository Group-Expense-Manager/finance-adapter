package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SELECT_ALGORITHM
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import java.math.BigDecimal.ZERO

@Component
class ReducingZeroBalancesStage : ProcessingStage() {
    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Reducing zero balances for financial reconciliation job: $reconciliationJob" }
        val reducedJob = reconciliationJob.copy(
            balances = reconciliationJob.balances.filterNot {
                it.value.compareTo(ZERO) == 0
            },
        )
        return nextStage(reducedJob, SELECT_ALGORITHM)
    }
}
