package pl.edu.agh.gem.internal.model.payment

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class AcceptedPayment(
    val creatorId: String,
    val recipientId: String,
    val title: String,
    val amount: Amount,
    val fxData: FxData?,
    val date: Instant,
) {
    fun toBalanceList(): List<Balance> {
        val multiplier = fxData?.exchangeRate ?: BigDecimal.ONE
        val value = amount.value.multiply(multiplier).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()

        return listOf(Balance(creatorId, value), Balance(recipientId, value.negate()))
    }
}

data class Amount(
    val value: BigDecimal,
    val currency: String,
)

data class FxData(
    val targetCurrency: String,
    val exchangeRate: BigDecimal,
)
