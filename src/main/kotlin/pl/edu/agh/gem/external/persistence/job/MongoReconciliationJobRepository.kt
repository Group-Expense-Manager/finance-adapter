package pl.edu.agh.gem.external.persistence.job

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.config.ReconciliationJobProcessorProperties
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.persistence.MissingReconciliationJobException
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import java.time.Clock
import java.time.Duration

@Repository
class MongoReconciliationJobRepository(
    private val mongoOperations: MongoOperations,
    private val reconciliationJobProcessorProperties: ReconciliationJobProcessorProperties,
    private val clock: Clock,
) : ReconciliationJobRepository {
    override fun save(reconciliationJob: ReconciliationJob): ReconciliationJob {
        return mongoOperations.save(reconciliationJob.toEntity()).toDomain()
    }

    override fun findJobToProcessAndLock(): ReconciliationJob? {
        val query = Query.query(Criteria.where(ReconciliationJobEntity::nextProcessAt.name).lte(clock.instant()))
        val update = Update()
            .set(ReconciliationJobEntity::nextProcessAt.name, clock.instant().plus(reconciliationJobProcessorProperties.lockTime))
        val options = FindAndModifyOptions.options().returnNew(false).upsert(false)
        return mongoOperations.findAndModify(query, update, options, ReconciliationJobEntity::class.java)?.toDomain()
    }

    override fun updateNextProcessAtAndRetry(reconciliationJob: ReconciliationJob): ReconciliationJob {
        val query = Query.query(Criteria.where(ReconciliationJobEntity::id.name).isEqualTo(reconciliationJob.id))
        val update = Update()
            .set(ReconciliationJobEntity::nextProcessAt.name, clock.instant().plus(getDelay(reconciliationJob.retry)))
            .set(ReconciliationJobEntity::retry.name, reconciliationJob.retry + 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(false)
        mongoOperations.findAll(ReconciliationJobEntity::class.java)
        return mongoOperations.findAndModify(query, update, options, ReconciliationJobEntity::class.java)?.toDomain()
            ?: throw MissingReconciliationJobException(reconciliationJob)
    }

    override fun remove(reconciliationJob: ReconciliationJob) {
        val query = Query.query(Criteria.where(ReconciliationJobEntity::id.name).isEqualTo(reconciliationJob.id))
        mongoOperations.remove(query, ReconciliationJobEntity::class.java)
    }

    override fun findById(id: String): ReconciliationJob? {
        return mongoOperations.findById(id, ReconciliationJobEntity::class.java)?.toDomain()
    }

    override fun cancelAllJobsForGroupWithCurrency(groupId: String, currency: String) {
        val query = Query.query(
            Criteria.where(ReconciliationJobEntity::groupId.name)
                .isEqualTo(groupId)
                .and(ReconciliationJobEntity::currency.name)
                .isEqualTo(currency),
        )
        val update = Update()
            .set(ReconciliationJobEntity::canceled.name, true)
        mongoOperations.updateMulti(query, update, ReconciliationJobEntity::class.java)
    }

    override fun removeIfCanceled(reconciliationJobId: String): Boolean {
        val query = Query.query(
            Criteria.where(ReconciliationJobEntity::id.name)
                .isEqualTo(reconciliationJobId)
                .and(ReconciliationJobEntity::canceled.name)
                .isEqualTo(true),
        )
        return mongoOperations.remove(query, ReconciliationJobEntity::class.java).deletedCount > 0
    }

    private fun getDelay(retry: Long): Duration {
        return reconciliationJobProcessorProperties
            .retryDelays
            .getOrNull(retry.toInt())
            ?: reconciliationJobProcessorProperties.retryDelays.last()
    }
}
