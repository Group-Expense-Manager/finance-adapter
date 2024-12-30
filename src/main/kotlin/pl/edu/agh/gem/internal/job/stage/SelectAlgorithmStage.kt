package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_GREEDY_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_MAX_DIFFERENCE_FIRST_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_SET_PARTITION_ALGORITHM
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob

@Component
class SelectAlgorithmStage : ProcessingStage() {

    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Selecting algorithm for job: $reconciliationJob" }
        return if (reconciliationJob.balances.size <= BOTTOM_THRESHOLD) {
            nextStage(reconciliationJob, APPLY_GREEDY_ALGORITHM)
        } else if (reconciliationJob.balances.size <= TOP_THRESHOLD) {
            nextStage(reconciliationJob, APPLY_SET_PARTITION_ALGORITHM)
        } else {
            nextStage(reconciliationJob, APPLY_MAX_DIFFERENCE_FIRST_ALGORITHM)
        }
    }

    companion object {
        private const val BOTTOM_THRESHOLD = 5
        private const val TOP_THRESHOLD = 20
    }
}
