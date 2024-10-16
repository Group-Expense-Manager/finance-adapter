package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.external.dto.group.CurrencyDto
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.internal.model.finance.settlements.CurrencySettlement
import pl.edu.agh.gem.internal.model.finance.settlements.Settlement
import pl.edu.agh.gem.internal.model.finance.settlements.Settlements
import java.math.BigDecimal

data class SettlementsResponse(
    val groupId: String,
    val currencySettlements: List<CurrencySettlementDto>,
)

fun Settlements.toSettlementsResponse(groupId: String) = SettlementsResponse(
    groupId = groupId,
    currencySettlements = this.map { it.toDto() },
)

data class CurrencySettlementDto(
    val currency: CurrencyDto,
    val settlements: List<SettlementDto>,
)

fun CurrencySettlement.toDto() = CurrencySettlementDto(
    currency = currency.toDto(),
    settlements = settlements.map { it.toDto() },
)

data class SettlementDto(
    val payerId: String,
    val payeeId: String,
    val value: BigDecimal,
)

fun Settlement.toDto() = SettlementDto(
    payerId = payerId,
    payeeId = payeeId,
    value = value,
)
