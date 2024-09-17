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
import pl.edu.agh.gem.util.createPaymentManagerActivityDto
import java.math.BigDecimal
import java.time.Instant

class PaymentManagerActivitiesResponseTest : ShouldSpec({
    should("map PaymentManagerActivityDTO to Activity correctly") {
        // given
        val paymentManagerActivityDTO = createPaymentManagerActivityDto()

        // when
        val activity = paymentManagerActivityDTO.toActivity()

        // then
        activity.also {
            it.activityId shouldBe paymentManagerActivityDTO.paymentId
            it.type shouldBe PAYMENT
            it.creatorId shouldBe paymentManagerActivityDTO.creatorId
            it.title shouldBe paymentManagerActivityDTO.title
            it.value shouldBe paymentManagerActivityDTO.amount.value
            it.baseCurrency shouldBe paymentManagerActivityDTO.amount.currency
            it.targetCurrency shouldBe paymentManagerActivityDTO.targetCurrency
            it.status shouldBe paymentManagerActivityDTO.status
            it.participantIds shouldBe listOf(paymentManagerActivityDTO.creatorId, paymentManagerActivityDTO.recipientId)
            it.date shouldBe paymentManagerActivityDTO.date
        }
    }

    should("map PaymentManagerActivitiesResponse to Activities correctly") {
        // given
        val paymentIds = listOf("paymentId1", "paymentId2", "paymentId3")
        val recipientIds = listOf("recipientId1", "recipientId2", "recipientId3")
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val amounts = listOf(
            AmountDto(value = BigDecimal.ONE, currency = "PLN"),
            AmountDto(value = BigDecimal.TWO, currency = "EUR"),
            AmountDto(value = BigDecimal.TEN, currency = "USD"),
        )
        val targetCurrencies = listOf("EUR", null, "PLN")
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
                targetCurrency = targetCurrencies[index],
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
            it.map { activity -> activity.value } shouldContainExactly amounts.map { amount -> amount.value }
            it.map { activity -> activity.baseCurrency } shouldContainExactly amounts.map { amount -> amount.currency }
            it.map { activity -> activity.targetCurrency } shouldContainExactly targetCurrencies
            it.map { activity -> activity.status } shouldContainExactly statuses
            it.map { activity -> activity.participantIds } shouldContainExactly creatorIds
                .mapIndexed { index, creatorId -> listOf(creatorId, recipientIds[index]) }
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
