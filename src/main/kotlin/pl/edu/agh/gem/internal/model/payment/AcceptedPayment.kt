package pl.edu.agh.gem.internal.model.payment

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
    fun toBalanceElements(): List<Triple<String, String, BigDecimal>> {
        val currency = fxData?.targetCurrency ?: amount.currency
        val multiplier = fxData?.exchangeRate ?: BigDecimal.ONE
        val value = amount.value.multiply(multiplier).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()

        val creatorBalanceElement = Triple(currency, creatorId, value)
        val recipientBalanceElement = Triple(currency, recipientId, value.negate())
        return listOf(creatorBalanceElement, recipientBalanceElement)
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
