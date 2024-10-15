package pl.edu.agh.gem.internal.job

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import pl.edu.agh.gem.config.ReconciliationJobProcessorProperties
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import java.util.concurrent.Executor

class ReconciliationJobFinder(
    private val producerExecutor: Executor,
    private val reconciliationJobRepository: ReconciliationJobRepository,
    private val reconciliationJobProcessorProperties: ReconciliationJobProcessorProperties,
) {
    fun findJobToProcess() = flow {
        while (currentCoroutineContext().isActive) {
            val financialReconciliationJob = findFinancialReconciliationJob()
            financialReconciliationJob?.let {
                emit(it)
                log.info { "Emitted financial reconciliation Job : $it" }
            }
            waitOnEmpty(financialReconciliationJob)
        }
    }.flowOn(producerExecutor.asCoroutineDispatcher())

    private fun findFinancialReconciliationJob(): ReconciliationJob? {
        try {
            return reconciliationJobRepository.findJobToProcessAndLock()
        } catch (e: Exception) {
            log.error("Error while finding financial reconciliation job to process", e)
            return null
        }
    }

    private suspend fun waitOnEmpty(reconciliationJob: ReconciliationJob?) {
        if (reconciliationJob == null) {
            log.info { "No financial reconciliation job to process. Waiting for new job" }
            delay(reconciliationJobProcessorProperties.emptyCandidateDelay)
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
