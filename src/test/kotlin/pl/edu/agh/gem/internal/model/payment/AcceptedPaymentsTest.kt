package pl.edu.agh.gem.internal.model.payment

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedPayment
import pl.edu.agh.gem.util.createAmount
import pl.edu.agh.gem.util.createFxData
import pl.edu.agh.gem.util.createReportActivityMember

class AcceptedPaymentsTest : ShouldSpec({

    should("map to BalanceElements") {
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
        val balanceElements = payment.toBalanceList()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Balance(USER_ID, "2".toBigDecimal()),
            Balance(OTHER_USER_ID, "-2".toBigDecimal()),
        )
    }

    should("map to ReportActivity") {
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
            it.members shouldContainExactlyInAnyOrder listOf(
                createReportActivityMember(USER_ID, "8".toBigDecimal()),
                createReportActivityMember(OTHER_USER_ID, "-8".toBigDecimal()),
            )
        }
    }
},)
