package pl.edu.agh.gem.external.persistence.job

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.external.persistence.balance.BalanceEntity
import pl.edu.agh.gem.external.persistence.balance.toDomain
import pl.edu.agh.gem.external.persistence.balance.toEntity
import pl.edu.agh.gem.external.persistence.settlements.SettlementEntity
import pl.edu.agh.gem.external.persistence.settlements.toDomain
import pl.edu.agh.gem.external.persistence.settlements.toEntity
import pl.edu.agh.gem.internal.job.ReconciliationJobState
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import java.time.Instant

@Document("jobs")
data class ReconciliationJobEntity(
    @Id
    val id: String,
    val groupId: String,
    val currency: String,
    val state: ReconciliationJobState,
    val balances: List<BalanceEntity>,
    val settlements: List<SettlementEntity>,
    val nextProcessAt: Instant,
    val retry: Long = 0,
    val canceled: Boolean = false,
)

fun ReconciliationJobEntity.toDomain(): ReconciliationJob {
    return ReconciliationJob(
        id = id,
        groupId = groupId,
        currency = currency,
        state = state,
        balances = balances.map { it.toDomain() },
        settlements = settlements.map { it.toDomain() },
        nextProcessAt = nextProcessAt,
        retry = retry,
        canceled = canceled,
    )
}

fun ReconciliationJob.toEntity(): ReconciliationJobEntity {
    return ReconciliationJobEntity(
        id = id,
        groupId = groupId,
        currency = currency,
        state = state,
        balances = balances.map { it.toEntity() },
        settlements = settlements.map { it.toEntity() },
        nextProcessAt = nextProcessAt,
        retry = retry,
        canceled = canceled,
    )
}
