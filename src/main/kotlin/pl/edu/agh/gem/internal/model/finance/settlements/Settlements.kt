package pl.edu.agh.gem.internal.model.finance.settlements

import pl.edu.agh.gem.internal.model.group.Currency
import java.math.BigDecimal

typealias Settlements = List<CurrencySettlement>

data class CurrencySettlement(
    val currency: Currency,
    val settlements: List<Settlement>,
)

data class Settlement(
    val payerId: String,
    val payeeId: String,
    val value: BigDecimal,
)
