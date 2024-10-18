package pl.edu.agh.gem.internal.job

import mu.KotlinLogging
import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository

@Service
class ReconciliationJobProcessor(
    private val reconciliationJobSelector: ReconciliationJobSelector,
    private val reconciliationJobRepository: ReconciliationJobRepository,
) {
    fun processReconciliationJob(reconciliationJob: ReconciliationJob) {
        when (val nextState = reconciliationJobSelector.select(reconciliationJob.state).process(reconciliationJob)) {
            is NextStage -> handleNextStage(nextState)
            is StageSuccess -> handleStateSuccess(reconciliationJob)
            is StageFailure -> handleStateFailure(reconciliationJob)
                .also {
                    log.error(nextState.exception) { "Failure occurred on job $reconciliationJob" }
                }
            is StageRetry -> handleStateRetry(reconciliationJob)
        }
    }

    private fun handleStateSuccess(reconciliationJob: ReconciliationJob) {
        log.info {
            "Success on job with groupId ${reconciliationJob.groupId} for currency ${reconciliationJob.currency}"
        }
        reconciliationJobRepository.remove(reconciliationJob)
    }

    private fun handleStateFailure(reconciliationJob: ReconciliationJob) {
        log.error { "Failure occurred on job with groupId ${reconciliationJob.groupId} for currency ${reconciliationJob.currency}" }
        reconciliationJobRepository.remove(reconciliationJob)
    }

    private fun handleStateRetry(reconciliationJob: ReconciliationJob) {
        if (reconciliationJobRepository.removeIfCanceled(reconciliationJob.id)) {
            log.info { "ReconciliationJobJob $reconciliationJob was canceled" }
        } else {
            log.warn { "Retry for on job with groupId ${reconciliationJob.groupId} for currency ${reconciliationJob.currency}" }
            reconciliationJobRepository.updateNextProcessAtAndRetry(reconciliationJob)
        }
    }

    private fun handleNextStage(nextStage: NextStage) {
        if (reconciliationJobRepository.removeIfCanceled(nextStage.reconciliationJob.id)) {
            log.info { "ReconciliationJobJob ${nextStage.reconciliationJob} was canceled" }
        } else {
            reconciliationJobRepository.save(
                nextStage.reconciliationJob.copy(
                    state = nextStage.newState,
                ),
            )
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
