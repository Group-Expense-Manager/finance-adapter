package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState
import pl.edu.agh.gem.internal.job.StageFailure
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob

@Component
class ErrorStage : ProcessingStage() {
    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.warn { "Error state reached for $reconciliationJob" }
        return StageFailure(ErrorStateException(reconciliationJob.state))
    }
}

class ErrorStateException(state: ReconciliationJobState) : Exception("Error state reached from $state")
