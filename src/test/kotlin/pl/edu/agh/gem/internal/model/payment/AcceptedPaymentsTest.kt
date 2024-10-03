package pl.edu.agh.gem.internal.model.payment

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedPayment
import pl.edu.agh.gem.util.createAmount
import pl.edu.agh.gem.util.createFxData
import pl.edu.agh.gem.util.createReportActivityMember

class AcceptedPaymentsTest : ShouldSpec({

    should("map to BalanceElements when fxData is null") {
        // given
        val payment = createAcceptedPayment(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmount(
                value = "2".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = null,
        )

        // when
        val balanceElements = payment.toBalanceElements()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Triple(CURRENCY_1, USER_ID, "2".toBigDecimal()),
            Triple(CURRENCY_1, OTHER_USER_ID, "-2".toBigDecimal()),
        )
    }
    should("map to BalanceElements when fxData is not null") {
        // given
        val payment = createAcceptedPayment(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmount(
                value = "2".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = createFxData(
                targetCurrency = CURRENCY_2,
                exchangeRate = "4".toBigDecimal(),
            ),
        )

        // when
        val balanceElements = payment.toBalanceElements()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Triple(CURRENCY_2, USER_ID, "8".toBigDecimal()),
            Triple(CURRENCY_2, OTHER_USER_ID, "-8".toBigDecimal()),
        )
    }

    should("map to ReportActivity when fxData is null") {
        // given
        val payment = createAcceptedPayment(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmount(
                value = "2".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = null,
        )

        // when
        val reportActivity = payment.toReportActivity()

        // then
        reportActivity.also {
            it.title shouldBe payment.title
            it.date shouldBe payment.date
            it.value shouldBe payment.amount.value
            it.currency shouldBe Currency(payment.amount.currency)
            it.members shouldContainExactlyInAnyOrder listOf(
                createReportActivityMember(USER_ID, payment.amount.value),
                createReportActivityMember(OTHER_USER_ID, payment.amount.value.negate()),
            )
        }
    }

    should("map to ReportActivity when fxData is not null") {
        // given
        val payment = createAcceptedPayment(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmount(
                value = "2".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = createFxData(
                targetCurrency = CURRENCY_2,
                exchangeRate = "4".toBigDecimal(),
            ),
        )

        // when
        val reportActivity = payment.toReportActivity()

        // then
        reportActivity.also {
            it.title shouldBe payment.title
            it.date shouldBe payment.date
            it.value shouldBe "8".toBigDecimal()
            it.currency shouldBe payment.fxData?.let { fxData -> Currency(fxData.targetCurrency) }
            it.members shouldContainExactlyInAnyOrder listOf(
                createReportActivityMember(USER_ID, "8".toBigDecimal()),
                createReportActivityMember(OTHER_USER_ID, "-8".toBigDecimal()),
            )
        }
    }
},)
