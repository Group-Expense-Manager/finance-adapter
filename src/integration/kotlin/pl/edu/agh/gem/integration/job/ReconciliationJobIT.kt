package pl.edu.agh.gem.integration.job

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.time.delay
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.SpyBean
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.finance.settlement.SettlementStatus.SAVED
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.util.createBalance
import pl.edu.agh.gem.util.createReconciliationJob
import java.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ReconciliationJobIT(
    @SpyBean private val clock: Clock,
    private val reconciliationJobRepository: ReconciliationJobRepository,
    private val settlementsRepository: SettlementsRepository,
) : BaseIntegrationSpec({

    should("process reconciliation job successfully") {
        // given
        val startedTime = testClock.instant()
        whenever(clock.instant()).thenAnswer { FIXED_TIME.plusSeconds(elapsedSeconds(startedTime)) }

        val reconciliationJob = createReconciliationJob(
            groupId = "groupId",
            currency = "USD",
            nextProcessAt = FIXED_TIME,
            balances = listOf(
                createBalance("userId1", "10".toBigDecimal()),
                createBalance("userId2", "50".toBigDecimal()),
                createBalance("userId3", "-30".toBigDecimal()),
                createBalance("userId4", "-30".toBigDecimal()),
                createBalance("userId5", "100".toBigDecimal()),
                createBalance("userId6", "-100".toBigDecimal()),
            ),
        )

        // when
        reconciliationJobRepository.save(reconciliationJob)

        // then
        waitTillReconciliationJob(reconciliationJobRepository, reconciliationJob.id)
        val settlements = settlementsRepository.getSettlements(reconciliationJob.groupId)
        settlements.size shouldBe 1
        settlements.first().also {
            it.groupId shouldBe reconciliationJob.groupId
            it.currency shouldBe reconciliationJob.currency
            it.status shouldBe SAVED
            it.settlements.size shouldBe 4
            it.settlements shouldContainExactlyInAnyOrder listOf(
                Settlement("userId6", "userId5", "100".toBigDecimal()),
                Settlement("userId3", "userId1", "10".toBigDecimal()),
                Settlement("userId3", "userId2", "20".toBigDecimal()),
                Settlement("userId4", "userId2", "30".toBigDecimal()),
            )
        }
    }

    should("canceled job should not process") {
        // given
        val startedTime = testClock.instant()
        whenever(clock.instant()).thenAnswer { FIXED_TIME.plusSeconds(elapsedSeconds(startedTime)) }

        val reconciliationJob = createReconciliationJob(
            canceled = true,
            groupId = "groupId",
            currency = "USD",
            nextProcessAt = FIXED_TIME,
            balances = listOf(
                createBalance("userId1", "10".toBigDecimal()),
                createBalance("userId2", "50".toBigDecimal()),
                createBalance("userId3", "-30".toBigDecimal()),
                createBalance("userId4", "-30".toBigDecimal()),
                createBalance("userId5", "100".toBigDecimal()),
                createBalance("userId6", "-100".toBigDecimal()),
            ),
        )

        // when
        reconciliationJobRepository.save(reconciliationJob)

        // then
        waitTillReconciliationJob(reconciliationJobRepository, reconciliationJob.id)
        val settlements = settlementsRepository.getSettlements(reconciliationJob.groupId)
        settlements.shouldBeEmpty()
    }
},)

private suspend fun waitTillReconciliationJob(reconciliationJobRepository: ReconciliationJobRepository, reconciliationJobId: String) {
    while (true) {
        delay(1L.seconds.toJavaDuration())
        val reconciliationJob = reconciliationJobRepository.findById(reconciliationJobId)
        if (reconciliationJob == null) {
            break
        }
    }
}
