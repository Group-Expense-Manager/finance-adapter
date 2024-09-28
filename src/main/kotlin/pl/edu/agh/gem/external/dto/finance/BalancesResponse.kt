package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.balance.Balances
import java.math.BigDecimal

data class BalancesResponse(
    val groupId: String,
    val balances: List<CurrencyBalancesDto>,
)

data class CurrencyBalancesDto(
    val currency: String,
    val userBalances: List<UserBalanceDto>,
)

data class UserBalanceDto(
    val userId: String,
    val balance: BigDecimal,
)

fun Balances.toBalancesResponse(groupId: String) = BalancesResponse(
    groupId = groupId,
    balances = this.map { it.toCurrencyBalancesDto() },
)

fun Map.Entry<String, Map<String, BigDecimal>>.toCurrencyBalancesDto() = CurrencyBalancesDto(
    currency = key,
    userBalances = value.map { it.toUserBalanceDto() },
)

fun Map.Entry<String, BigDecimal>.toUserBalanceDto() = UserBalanceDto(
    userId = key,
    balance = value,
)
