package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SAVING
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.solver.settlements.SetPartitionSolver

@Component
class ApplySetPartitionAlgorithmStage : ProcessingStage() {

    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Finding settlements using set partition for job: $reconciliationJob" }
        val settlements = SetPartitionSolver.solve(reconciliationJob.balances)
        val solvedFinancialReconciliationJob = reconciliationJob.copy(
            settlements = reconciliationJob.settlements + settlements,
        )
        return nextStage(solvedFinancialReconciliationJob, SAVING)
    }
}
