package pl.edu.agh.gem.external.dto.payment

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.util.createAcceptedPaymentDto
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import java.math.BigDecimal
import java.time.Instant

class AcceptedPaymentsResponseTest : ShouldSpec({
    should("map to Domain correctly") {
        // given
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val recipientIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val amounts = listOf(
            AmountDto(value = BigDecimal.ONE, currency = "PLN"),
            AmountDto(value = BigDecimal.TWO, currency = "EUR"),
            AmountDto(value = BigDecimal.TEN, currency = "USD"),
        )
        val fxData = listOf(
            FxDataDto(targetCurrency = "EUR", exchangeRate = "3.41".toBigDecimal()),
            null,
            FxDataDto(targetCurrency = "PLN", exchangeRate = "4.44".toBigDecimal()),
        )

        val dates = listOf(
            Instant.ofEpochSecond(1000),
            Instant.ofEpochSecond(2000),
            Instant.ofEpochSecond(3000),
        )

        val payments = creatorIds.mapIndexed { index, creatorId ->
            createAcceptedPaymentDto(
                creatorId = creatorId,
                recipientId = recipientIds[index],
                title = titles[index],
                amount = amounts[index],
                fxData = fxData[index],
                date = dates[index],
            )
        }

        val acceptedPaymentsResponse = createAcceptedPaymentsResponse(
            groupId = GROUP_ID,
            payments = payments,
        )

        // when
        val acceptedPayments = acceptedPaymentsResponse.toDomain()

        // then
        acceptedPayments.also {
            it shouldHaveSize 3
            it.map { payment -> payment.creatorId } shouldContainExactly creatorIds
            it.map { payment -> payment.recipientId } shouldContainExactly recipientIds
            it.map { payment -> payment.title } shouldContainExactly titles
            it.map { payment -> payment.amount } shouldContainExactly amounts.map { amount -> amount.toDomain() }
            it.map { payment -> payment.fxData } shouldContainExactly fxData.map { fxData -> fxData?.toDomain() }
            it.map { payment -> payment.date } shouldContainExactly dates
        }
    }
},)
