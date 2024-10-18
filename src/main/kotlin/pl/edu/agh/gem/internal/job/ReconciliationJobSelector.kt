package pl.edu.agh.gem.internal.job

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.job.ReconciliationJobState.FIND_SETTLEMENTS
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_EVIDENT_SETTLEMENTS
import pl.edu.agh.gem.internal.job.ReconciliationJobState.REDUCING_ZERO_BALANCES
import pl.edu.agh.gem.internal.job.ReconciliationJobState.SAVING
import pl.edu.agh.gem.internal.job.ReconciliationJobState.STARTING
import pl.edu.agh.gem.internal.job.stage.ErrorStage
import pl.edu.agh.gem.internal.job.stage.FindSettlementsStage
import pl.edu.agh.gem.internal.job.stage.ReducingEvidentSettlementsStage
import pl.edu.agh.gem.internal.job.stage.ReducingZeroBalancesStage
import pl.edu.agh.gem.internal.job.stage.SavingStage
import pl.edu.agh.gem.internal.job.stage.StartingStage

@Service
class ReconciliationJobSelector(
    private val startingStage: StartingStage,
    private val reducingEvidentSettlementsStage: ReducingEvidentSettlementsStage,
    private val reducingZeroBalancesStage: ReducingZeroBalancesStage,
    private val findSettlementsStage: FindSettlementsStage,
    private val savingStage: SavingStage,
    private val errorStage: ErrorStage,
) {
    fun select(state: ReconciliationJobState): ProcessingStage {
        return when (state) {
            STARTING -> startingStage
            REDUCING_ZERO_BALANCES -> reducingZeroBalancesStage
            REDUCING_EVIDENT_SETTLEMENTS -> reducingEvidentSettlementsStage
            FIND_SETTLEMENTS -> findSettlementsStage
            SAVING -> savingStage
            else -> errorStage
        }
    }
}
