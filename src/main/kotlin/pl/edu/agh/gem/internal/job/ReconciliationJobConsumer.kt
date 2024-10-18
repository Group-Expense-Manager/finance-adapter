package pl.edu.agh.gem.internal.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mu.KotlinLogging
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import java.util.concurrent.Executor

class ReconciliationJobConsumer(
    private val reconciliationJobFinder: ReconciliationJobFinder,
    private val reconciliationJobProcessor: ReconciliationJobProcessor,
) {

    private var job: Job? = null

    fun consume(consumerExecutor: Executor) {
        job = CoroutineScope(consumerExecutor.asCoroutineDispatcher()).launch {
            reconciliationJobFinder.findJobToProcess()
                .collect {
                    launch {
                        processWithExceptionHandling(it)
                    }
                }
        }
    }

    private fun processWithExceptionHandling(reconciliationJob: ReconciliationJob) {
        try {
            reconciliationJobProcessor.processReconciliationJob(reconciliationJob)
        } catch (e: Exception) {
            log.error(e) { "Error while processing financial reconciliation job: $reconciliationJob" }
        }
    }

    fun destroy() {
        job?.also {
            log.info { "Cancelling financial reconciliation job consumer job" }
            it.cancel()
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
