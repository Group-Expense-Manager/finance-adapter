package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob

interface ReconciliationJobRepository {
    fun save(reconciliationJob: ReconciliationJob): ReconciliationJob
    fun findJobToProcessAndLock(): ReconciliationJob?
    fun updateNextProcessAtAndRetry(reconciliationJob: ReconciliationJob): ReconciliationJob?
    fun remove(reconciliationJob: ReconciliationJob)
    fun findById(id: String): ReconciliationJob?
    fun cancelAllJobsForGroupWithCurrency(groupId: String, currency: String)
    fun removeIfCanceled(reconciliationJobId: String): Boolean
}

class MissingReconciliationJobException(reconciliationJob: ReconciliationJob) : RuntimeException(
    "No financial reconciliation job found, $reconciliationJob",
)
