package pl.edu.agh.gem.internal.model.expense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpense
import pl.edu.agh.gem.util.createAcceptedExpenseParticipant
import pl.edu.agh.gem.util.createReportActivityMember

class AcceptedExpenseTest : ShouldSpec({

    should("map to BalanceElements when targetCurrency is null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            baseCurrency = CURRENCY_1,
            targetCurrency = null,
            exchangeRate = null,
        )

        // when
        val balanceElements = expense.toBalanceElements()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Triple(CURRENCY_1, USER_ID, "8".toBigDecimal()),
            Triple(CURRENCY_1, OTHER_USER_ID, "-3".toBigDecimal()),
            Triple(CURRENCY_1, ANOTHER_USER_ID, "-5".toBigDecimal()),
        )
    }

    should("map to BalanceElements when targetCurrency is not null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            baseCurrency = CURRENCY_1,
            targetCurrency = CURRENCY_2,
            exchangeRate = "0.5".toBigDecimal(),
        )

        // when
        val balanceElements = expense.toBalanceElements()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Triple(CURRENCY_2, USER_ID, "4".toBigDecimal()),
            Triple(CURRENCY_2, OTHER_USER_ID, "-1.5".toBigDecimal()),
            Triple(CURRENCY_2, ANOTHER_USER_ID, "-2.5".toBigDecimal()),
        )
    }

    should("map to ReportActivity when targetCurrency is null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            baseCurrency = CURRENCY_1,
            targetCurrency = null,
            exchangeRate = null,
            totalCost = "9".toBigDecimal(),
        )

        // when
        val reportActivity = expense.toReportActivity()

        // then
        reportActivity.also {
            it.title shouldBe expense.title
            it.value shouldBe expense.totalCost
            it.date shouldBe expense.expenseDate
            it.currency shouldBe Currency(expense.baseCurrency)
            it.members.size shouldBe 3
            it.members shouldContainExactlyInAnyOrder listOf(
                createReportActivityMember(USER_ID, "8".toBigDecimal()),
                createReportActivityMember(OTHER_USER_ID, "-3".toBigDecimal()),
                createReportActivityMember(ANOTHER_USER_ID, "-5".toBigDecimal()),
            )
        }
    }

    should("map to ReportActivity when targetCurrency is not null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            baseCurrency = CURRENCY_1,
            targetCurrency = CURRENCY_2,
            exchangeRate = "0.5".toBigDecimal(),
            totalCost = "10".toBigDecimal(),
        )

        // when
        val reportActivity = expense.toReportActivity()

        // then
        reportActivity.also {
            it.title shouldBe expense.title
            it.value shouldBe "5".toBigDecimal()
            it.date shouldBe expense.expenseDate
            it.currency shouldBe expense.targetCurrency?.let { c -> Currency(c) }
            it.members.size shouldBe 3
            it.members shouldContainExactlyInAnyOrder listOf(
                createReportActivityMember(USER_ID, "4".toBigDecimal()),
                createReportActivityMember(OTHER_USER_ID, "-1.5".toBigDecimal()),
                createReportActivityMember(ANOTHER_USER_ID, "-2.5".toBigDecimal()),
            )
        }
    }
},)
