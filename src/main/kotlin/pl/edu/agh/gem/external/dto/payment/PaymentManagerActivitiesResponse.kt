package pl.edu.agh.gem.external.dto.payment

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class PaymentManagerActivitiesResponse(
    val groupId: String,
    val payments: List<PaymentManagerActivityDto>,
) {
    fun toDomain() = payments.map { it.toActivity() }
}

data class PaymentManagerActivityDto(
    val paymentId: String,
    val creatorId: String,
    val recipientId: String,
    val title: String,
    val amount: AmountDto,
    val fxData: FxDataDto?,
    val status: ActivityStatus,
    val date: Instant,
) {
    fun toActivity() = Activity(
        activityId = paymentId,
        type = PAYMENT,
        creatorId = creatorId,
        title = title,
        value = amount.value.multiply(fxData?.exchangeRate ?: BigDecimal.ONE).setScale(2, RoundingMode.DOWN).stripTrailingZeros(),
        currency = fxData?.targetCurrency ?: amount.currency,
        status = status,
        participantIds = listOf(recipientId),
        date = date,
    )
}
data class AmountDto(
    val value: BigDecimal,
    val currency: String,
)

data class FxDataDto(
    val targetCurrency: String,
    val exchangeRate: BigDecimal,
)
