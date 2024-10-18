package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import java.time.Clock
import java.time.Instant

@Service
class ReconciliationService(
    private val reconciliationJobRepository: ReconciliationJobRepository,
    private val clock: Clock,
) {
    fun generateNewSettlement(balances: Balances) {
        reconciliationJobRepository.cancelAllJobsForGroupWithCurrency(balances.groupId, balances.currency)
        reconciliationJobRepository.save(
            ReconciliationJob(
                groupId = balances.groupId,
                currency = balances.currency,
                nextProcessAt = Instant.now(clock),
            ),
        )
    }
}
