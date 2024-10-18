package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import java.math.BigDecimal

data class BalancesResponse(
    val groupId: String,
    val balances: List<BalancesDto>,
)

data class BalancesDto(
    val currency: String,
    val userBalances: List<UserBalanceDto>,
)

data class UserBalanceDto(
    val userId: String,
    val balance: BigDecimal,
)

fun List<Balances>.toBalancesResponse() = BalancesResponse(
    groupId = first().groupId,
    balances = this.map { it.toBalancesDto() },
)

fun Balances.toBalancesDto() = BalancesDto(
    currency = currency,
    userBalances = users.map { it.toUserBalanceDto() },
)

fun Balance.toUserBalanceDto() = UserBalanceDto(
    userId = userId,
    balance = value,
)
