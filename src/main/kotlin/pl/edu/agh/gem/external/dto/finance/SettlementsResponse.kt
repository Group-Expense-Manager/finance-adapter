package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import pl.edu.agh.gem.internal.model.finance.settlement.Settlements
import java.math.BigDecimal

data class SettlementsResponse(
    val groupId: String,
    val settlements: List<SettlementsDto>,
)

fun List<Settlements>.toSettlementsResponse() = SettlementsResponse(
    groupId = first().groupId,
    settlements = this.map { it.toSettlementsDto() },
)

data class SettlementsDto(
    val currency: String,
    val settlements: List<SettlementDto>,
)

fun Settlements.toSettlementsDto() = SettlementsDto(
    currency = currency,
    settlements = settlements.map { it.toSettlementDto() },
)

data class SettlementDto(
    val fromUserId: String,
    val toUserId: String,
    val value: BigDecimal,
)

fun Settlement.toSettlementDto() = SettlementDto(
    fromUserId = fromUserId,
    toUserId = toUserId,
    value = value,
)
