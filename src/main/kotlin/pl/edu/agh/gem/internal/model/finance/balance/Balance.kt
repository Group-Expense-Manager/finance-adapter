package pl.edu.agh.gem.internal.model.finance.balance

import java.math.BigDecimal

data class Balances(
    val users: List<Balance>,
    val groupId: String,
    val currency: String,
)
data class Balance(
    val userId: String,
    val value: BigDecimal,
)
