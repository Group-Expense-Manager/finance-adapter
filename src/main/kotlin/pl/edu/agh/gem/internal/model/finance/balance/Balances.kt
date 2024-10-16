package pl.edu.agh.gem.internal.model.finance.balance

import pl.edu.agh.gem.internal.model.group.Currency
import java.math.BigDecimal

typealias Balances = List<CurrencyBalances>

data class CurrencyBalances(
    val currency: Currency,
    val balances: List<Balance>,
)

fun Map<String, Map<String, BigDecimal>>.toBalances() =
    this.map {
        CurrencyBalances(
            currency = Currency(it.key),
            balances = it.value.map { entry -> Balance(userId = entry.key, value = entry.value) },
        )
    }

data class Balance(
    val userId: String,
    val value: BigDecimal,
)
