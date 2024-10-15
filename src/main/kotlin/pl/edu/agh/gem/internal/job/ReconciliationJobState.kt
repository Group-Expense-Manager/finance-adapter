package pl.edu.agh.gem.internal.job

enum class ReconciliationJobState {
    STARTING,
    REDUCING_EVIDENT_SETTLEMENTS,
    REDUCING_ZERO_BALANCES,
    FIND_SETTLEMENTS,
    SAVING,
    ERROR,
}
