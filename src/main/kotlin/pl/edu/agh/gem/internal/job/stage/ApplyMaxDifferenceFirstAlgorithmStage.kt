package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SAVING
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.solver.settlements.MaxDifferenceFirstSettlementsSolver

@Component
class ApplyMaxDifferenceFirstAlgorithmStage : ProcessingStage() {

    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Finding settlements using max difference first algorithm for job: $reconciliationJob" }
        val settlements = MaxDifferenceFirstSettlementsSolver.solve(reconciliationJob.balances)
        val solvedFinancialReconciliationJob = reconciliationJob.copy(
            settlements = reconciliationJob.settlements + settlements,
        )
        return nextStage(solvedFinancialReconciliationJob, SAVING)
    }
}
