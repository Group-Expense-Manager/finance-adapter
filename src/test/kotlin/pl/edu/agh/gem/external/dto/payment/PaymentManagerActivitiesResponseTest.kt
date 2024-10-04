package pl.edu.agh.gem.external.dto.payment

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.ACCEPTED
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.REJECTED
import pl.edu.agh.gem.internal.model.finance.ActivityType.PAYMENT
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createFxDataDto
import pl.edu.agh.gem.util.createPaymentManagerActivityDto
import java.math.BigDecimal
import java.time.Instant

class PaymentManagerActivitiesResponseTest : ShouldSpec({
    should("map PaymentManagerActivityDto to Activity correctly when fxData is null") {
        // given
        val paymentManagerActivityDto = createPaymentManagerActivityDto(fxData = null)

        // when
        val activity = paymentManagerActivityDto.toActivity()

        // then
        activity.also {
            it.activityId shouldBe paymentManagerActivityDto.paymentId
            it.type shouldBe PAYMENT
            it.creatorId shouldBe paymentManagerActivityDto.creatorId
            it.title shouldBe paymentManagerActivityDto.title
            it.value shouldBe paymentManagerActivityDto.amount.value
            it.currency shouldBe paymentManagerActivityDto.amount.currency
            it.status shouldBe paymentManagerActivityDto.status
            it.participantIds shouldBe listOf(paymentManagerActivityDto.recipientId)
            it.date shouldBe paymentManagerActivityDto.date
        }
    }

    should("map PaymentManagerActivityDto to Activity correctly when fxData is not null") {
        // given
        val paymentManagerActivityDto = createPaymentManagerActivityDto(
            amount = createAmountDto(value = "1.2".toBigDecimal()),
            fxData = createFxDataDto(exchangeRate = "3".toBigDecimal()),
        )

        // when
        val activity = paymentManagerActivityDto.toActivity()

        // then
        activity.also {
            it.activityId shouldBe paymentManagerActivityDto.paymentId
            it.type shouldBe PAYMENT
            it.creatorId shouldBe paymentManagerActivityDto.creatorId
            it.title shouldBe paymentManagerActivityDto.title
            it.value shouldBe "3.6".toBigDecimal()
            it.currency shouldBe paymentManagerActivityDto.fxData?.targetCurrency
            it.status shouldBe paymentManagerActivityDto.status
            it.participantIds shouldBe listOf(paymentManagerActivityDto.recipientId)
            it.date shouldBe paymentManagerActivityDto.date
        }
    }

    should("map PaymentManagerActivitiesResponse to Activities correctly") {
        // given
        val paymentIds = listOf("paymentId1", "paymentId2", "paymentId3")
        val recipientIds = listOf("recipientId1", "recipientId2", "recipientId3")
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val amounts = listOf(
            createAmountDto(value = BigDecimal.ONE, currency = "PLN"),
            AmountDto(value = BigDecimal.TWO, currency = "EUR"),
            AmountDto(value = BigDecimal.TWO, currency = "USD"),
        )
        val fxData = listOf(
            FxDataDto(targetCurrency = "EUR", exchangeRate = "2".toBigDecimal()),
            null,
            FxDataDto(targetCurrency = "PLN", exchangeRate = "3".toBigDecimal()),
        )
        val statuses = listOf(PENDING, ACCEPTED, REJECTED)
        val dates = listOf(
            Instant.ofEpochSecond(1000),
            Instant.ofEpochSecond(2000),
            Instant.ofEpochSecond(3000),
        )
        val payments = paymentIds.mapIndexed { index, paymentId ->
            createPaymentManagerActivityDto(
                paymentId = paymentId,
                recipientId = recipientIds[index],
                creatorId = creatorIds[index],
                title = titles[index],
                amount = amounts[index],
                fxData = fxData[index],
                status = statuses[index],
                date = dates[index],
            )
        }

        val paymentManagerActivitiesResponse = PaymentManagerActivitiesResponse(
            groupId = GROUP_ID,
            payments = payments,
        )

        // when
        val activities = paymentManagerActivitiesResponse.toDomain()

        // then
        activities.also {
            it shouldHaveSize 3
            it.map { activity -> activity.activityId } shouldContainExactly paymentIds
            it.map { activity -> activity.type } shouldContainExactly listOf(PAYMENT, PAYMENT, PAYMENT)
            it.map { activity -> activity.creatorId } shouldContainExactly creatorIds
            it.map { activity -> activity.title } shouldContainExactly titles
            it.map { activity -> activity.value } shouldContainExactly listOf("2".toBigDecimal(), "2".toBigDecimal(), "6".toBigDecimal())
            it.map { activity -> activity.currency } shouldContainExactly listOf("EUR", "EUR", "PLN")
            it.map { activity -> activity.status } shouldContainExactly statuses
            it.map { activity -> activity.participantIds } shouldContainExactly recipientIds
                .map { recipientId -> listOf(recipientId) }
            it.map { activity -> activity.date } shouldContainExactly dates
        }
    }

    should("return empty activities when there are no activities") {
        // given
        val paymentManagerActivitiesResponse = PaymentManagerActivitiesResponse(
            groupId = GROUP_ID,
            payments = listOf(),
        )

        // when
        val activities = paymentManagerActivitiesResponse.toDomain()

        // then
        activities shouldHaveSize 0
    }
},)
