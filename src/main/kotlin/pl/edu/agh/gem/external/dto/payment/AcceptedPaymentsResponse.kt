package pl.edu.agh.gem.external.dto.payment

import pl.edu.agh.gem.internal.model.payment.AcceptedPayment
import java.time.Instant

data class AcceptedPaymentsResponse(
    val groupId: String,
    val payments: List<AcceptedPaymentDto>,
) {
    fun toDomain() = payments.map { it.toDomain() }
}

data class AcceptedPaymentDto(
    val creatorId: String,
    val recipientId: String,
    val title: String,
    val amount: AmountDto,
    val fxData: FxDataDto?,
    val date: Instant,
) {
    fun toDomain() = AcceptedPayment(
        creatorId = creatorId,
        recipientId = recipientId,
        title = title,
        amount = amount.toDomain(),
        fxData = fxData?.toDomain(),
        date = date,
    )
}
