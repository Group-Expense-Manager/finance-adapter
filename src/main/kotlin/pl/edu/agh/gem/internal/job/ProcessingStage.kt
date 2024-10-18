package pl.edu.agh.gem.internal.job

import mu.KotlinLogging
import pl.edu.agh.gem.internal.model.reconciliation.ReconciliationJob

abstract class ProcessingStage {

    abstract fun process(reconciliationJob: ReconciliationJob): StageResult

    fun nextStage(reconciliationJob: ReconciliationJob, nextState: ReconciliationJobState): StageResult {
        return NextStage(reconciliationJob, nextState)
    }

    fun success(): StageResult {
        return StageSuccess
    }

    fun failure(exception: Exception): StageResult {
        return StageFailure(exception)
    }

    fun retry(): StageResult {
        return StageRetry
    }

    protected companion object {
        val logger = KotlinLogging.logger { }
    }
}

sealed class StageResult

data class NextStage(
    val reconciliationJob: ReconciliationJob,
    val newState: ReconciliationJobState,
) : StageResult()

data object StageSuccess : StageResult()

data class StageFailure(
    val exception: Exception,
) : StageResult()

data object StageRetry : StageResult()
