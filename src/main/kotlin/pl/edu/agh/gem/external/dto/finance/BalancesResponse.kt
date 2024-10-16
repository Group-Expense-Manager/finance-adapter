package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.external.dto.group.CurrencyDto
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.model.finance.balance.CurrencyBalances
import java.math.BigDecimal

data class BalancesResponse(
    val groupId: String,
    val currencyBalances: List<CurrencyBalancesDto>,
)

data class CurrencyBalancesDto(
    val currency: CurrencyDto,
    val balances: List<BalanceDto>,
)

data class BalanceDto(
    val userId: String,
    val value: BigDecimal,
)

fun Balances.toBalancesResponse(groupId: String) = BalancesResponse(
    groupId = groupId,
    currencyBalances = this.map { it.toCurrencyBalancesDto() },
)

fun CurrencyBalances.toCurrencyBalancesDto() = CurrencyBalancesDto(
    currency = currency.toDto(),
    balances = balances.map { it.toBalanceDto() },
)

fun Balance.toBalanceDto() = BalanceDto(
    userId = userId,
    value = value,
)
