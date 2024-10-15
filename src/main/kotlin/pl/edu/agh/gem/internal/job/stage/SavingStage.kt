package pl.edu.agh.gem.internal.job.stage

import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.job.ProcessingStage
import pl.edu.agh.gem.internal.job.StageResult
import pl.edu.agh.gem.internal.model.finance.settelment.SettlementStatus.SAVED
import pl.edu.agh.gem.internal.model.finance.settelment.Settlements
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.persistence.SettlementsRepository

@Component
class SavingStage(
    private val settlementsRepository: SettlementsRepository,
) : ProcessingStage() {
    override fun process(reconciliationJob: ReconciliationJob): StageResult {
        logger.info { "Saving financial reconciliation job: $reconciliationJob" }
        settlementsRepository.save(
            Settlements(
                currency = reconciliationJob.currency,
                groupId = reconciliationJob.groupId,
                settlements = reconciliationJob.settlements,
                status = SAVED,
            ),
        )
        return success()
    }
}
