package pl.edu.agh.gem.internal.model.finance.settlement

import java.math.BigDecimal

data class Settlements(
    val settlements: List<Settlement>,
    val groupId: String,
    val currency: String,
    val status: SettlementStatus,
)
data class Settlement(
    val fromUserId: String,
    val toUserId: String,
    val value: BigDecimal,
)

enum class SettlementStatus {
    PENDING,
    SAVED,
}
