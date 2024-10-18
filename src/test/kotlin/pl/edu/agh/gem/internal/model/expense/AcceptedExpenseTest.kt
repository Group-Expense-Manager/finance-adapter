package pl.edu.agh.gem.internal.model.expense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpense
import pl.edu.agh.gem.util.createAcceptedExpenseParticipant
import pl.edu.agh.gem.util.createAmount
import pl.edu.agh.gem.util.createFxData

class AcceptedExpenseTest : ShouldSpec({

    should("map to BalanceList when fxData is null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            amount = createAmount(
                value = "9".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = null,
        )

        // when
        val balanceElements = expense.toBalanceList()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Balance(USER_ID, "8".toBigDecimal()),
            Balance(OTHER_USER_ID, "-3".toBigDecimal()),
            Balance(ANOTHER_USER_ID, "-5".toBigDecimal()),
        )
    }

    should("map to BalanceList when fxData is not null") {
        // given
        val firstParticipant = createAcceptedExpenseParticipant(participantId = OTHER_USER_ID, participantCost = "3".toBigDecimal())
        val secondParticipant = createAcceptedExpenseParticipant(participantId = ANOTHER_USER_ID, participantCost = "5".toBigDecimal())
        val expense = createAcceptedExpense(
            creatorId = USER_ID,
            participants = listOf(firstParticipant, secondParticipant),
            amount = createAmount(
                value = "9".toBigDecimal(),
                currency = CURRENCY_1,
            ),
            fxData = createFxData(
                targetCurrency = CURRENCY_2,
                exchangeRate = "0.5".toBigDecimal(),
            ),
        )

        // when
        val balanceElements = expense.toBalanceList()

        // then
        balanceElements shouldContainExactlyInAnyOrder listOf(
            Balance(USER_ID, "4".toBigDecimal()),
            Balance(OTHER_USER_ID, "-1.5".toBigDecimal()),
            Balance(ANOTHER_USER_ID, "-2.5".toBigDecimal()),
        )
    }
},)
