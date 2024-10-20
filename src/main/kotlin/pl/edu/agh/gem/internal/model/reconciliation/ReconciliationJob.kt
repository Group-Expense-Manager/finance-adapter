package pl.edu.agh.gem.internal.model.reconciliation

import pl.edu.agh.gem.internal.job.ReconciliationJobState
import pl.edu.agh.gem.internal.job.ReconciliationJobState.STARTING
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settelment.Settlement
import java.time.Instant
import java.util.UUID.randomUUID

data class ReconciliationJob(
    val id: String = randomUUID().toString(),
    val groupId: String,
    val currency: String,
    val state: ReconciliationJobState = STARTING,
    val balances: List<Balance>,
    val settlements: List<Settlement> = listOf(),
    val nextProcessAt: Instant,
    val retry: Long = 0,
    val canceled: Boolean = false,
)
