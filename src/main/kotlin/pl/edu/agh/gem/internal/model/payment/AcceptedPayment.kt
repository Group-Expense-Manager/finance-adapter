package pl.edu.agh.gem.internal.model.payment

import pl.edu.agh.gem.internal.model.finance.report.ReportActivity
import pl.edu.agh.gem.internal.model.finance.report.ReportActivityMember
import pl.edu.agh.gem.internal.model.group.Currency
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

    fun toReportActivity(): ReportActivity {
        val currency = fxData?.targetCurrency ?: amount.currency
        val multiplier = fxData?.exchangeRate ?: BigDecimal.ONE

        val value = amount.value.multiply(multiplier).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
        val creatorReportActivityMember = ReportActivityMember(
            userId = creatorId,
            value = value,
        )

        val recipientReportActivityMember = ReportActivityMember(
            userId = recipientId,
            value = value.negate(),
        )

        return ReportActivity(
            title = title,
            date = date,
            value = value,
            currency = Currency(code = currency),
            members = listOf(creatorReportActivityMember, recipientReportActivityMember),
        )
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
