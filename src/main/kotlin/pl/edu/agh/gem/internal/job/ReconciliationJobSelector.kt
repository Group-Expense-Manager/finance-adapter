package pl.edu.agh.gem.internal.job

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_DEBT_ROUNDING_PAIRING_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_GREEDY_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.APPLY_SET_PARTITION_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_EVIDENT_SETTLEMENTS
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_ZERO_BALANCES
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SAVING
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SELECT_ALGORITHM
import pl.edu.agh.gem.internal.job.ReconciliationJobState.STARTING
import pl.edu.agh.gem.internal.job.stage.ApplyDebtRoundingPairingAlgorithmStage
import pl.edu.agh.gem.internal.job.stage.ApplyGreedyAlgorithmStage
import pl.edu.agh.gem.internal.job.stage.ApplySetPartitionAlgorithmStage
import pl.edu.agh.gem.internal.job.stage.ErrorStage
import pl.edu.agh.gem.internal.job.stage.ReducingEvidentSettlementsStage
import pl.edu.agh.gem.internal.job.stage.ReducingZeroBalancesStage
import pl.edu.agh.gem.internal.job.stage.SavingStage
import pl.edu.agh.gem.internal.job.stage.SelectAlgorithmStage
import pl.edu.agh.gem.internal.job.stage.StartingStage

@Service
class ReconciliationJobSelector(
    private val startingStage: StartingStage,
    private val reducingEvidentSettlementsStage: ReducingEvidentSettlementsStage,
    private val reducingZeroBalancesStage: ReducingZeroBalancesStage,
    private val selectAlgorithmStage: SelectAlgorithmStage,
    private val applyGreedyAlgorithmStage: ApplyGreedyAlgorithmStage,
    private val applySetPartitionAlgorithmStage: ApplySetPartitionAlgorithmStage,
    private val applyDebtRoundingPairingAlgorithmStage: ApplyDebtRoundingPairingAlgorithmStage,
    private val savingStage: SavingStage,
    private val errorStage: ErrorStage,
) {
    fun select(state: ReconciliationJobState): ProcessingStage {
        return when (state) {
            STARTING -> startingStage
            REDUCING_ZERO_BALANCES -> reducingZeroBalancesStage
            REDUCING_EVIDENT_SETTLEMENTS -> reducingEvidentSettlementsStage
            SELECT_ALGORITHM -> selectAlgorithmStage
            APPLY_GREEDY_ALGORITHM -> applyGreedyAlgorithmStage
            APPLY_SET_PARTITION_ALGORITHM -> applySetPartitionAlgorithmStage
            APPLY_DEBT_ROUNDING_PAIRING_ALGORITHM -> applyDebtRoundingPairingAlgorithmStage
            SAVING -> savingStage
            else -> errorStage
        }
    }
}
