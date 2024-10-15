package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_EVIDENT_SETTLEMENTS
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob

@Component
class StartingStage : ProcessingStage() {
    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Starting financial reconciliation job: $reconciliationJob" }
        return nextStage(reconciliationJob, REDUCING_EVIDENT_SETTLEMENTS)
    }
}
