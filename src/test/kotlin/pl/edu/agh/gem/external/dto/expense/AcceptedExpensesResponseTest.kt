package pl.edu.agh.gem.external.dto.expense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.util.createAcceptedExpenseDto
import pl.edu.agh.gem.util.createAcceptedExpenseParticipantDto
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import java.math.BigDecimal
import java.time.Instant

class AcceptedExpensesResponseTest : ShouldSpec({
    should("map to Domain correctly") {
        // given
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val totalCosts = listOf(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)
        val baseCurrencies = listOf("PLN", "EUR", "USD")
        val targetCurrencies = listOf("EUR", null, "PLN")
        val participants = listOf(
            listOf(
                createAcceptedExpenseParticipantDto(participantId = "participant1", participantCost = "2".toBigDecimal()),
                createAcceptedExpenseParticipantDto(participantId = "participant2", participantCost = "3".toBigDecimal()),
            ),
            listOf(
                createAcceptedExpenseParticipantDto(participantId = "participant3", participantCost = "4".toBigDecimal()),
                createAcceptedExpenseParticipantDto(participantId = "participant4", participantCost = "5".toBigDecimal()),
            ),
            listOf(
                createAcceptedExpenseParticipantDto(participantId = "participant5", participantCost = "6".toBigDecimal()),
                createAcceptedExpenseParticipantDto(participantId = "participant6", participantCost = "7".toBigDecimal()),
            ),
        )
        val exchangeRates = listOf("3.41".toBigDecimal(), null, "4.44".toBigDecimal())
        val expenseDates = listOf(
            Instant.ofEpochSecond(1000),
            Instant.ofEpochSecond(2000),
            Instant.ofEpochSecond(3000),
        )

        val expenses = creatorIds.mapIndexed { index, creatorId ->
            createAcceptedExpenseDto(
                creatorId = creatorId,
                title = titles[index],
                totalCost = totalCosts[index],
                baseCurrency = baseCurrencies[index],
                targetCurrency = targetCurrencies[index],
                exchangeRate = exchangeRates[index],
                participants = participants[index],
                expenseDate = expenseDates[index],
            )
        }

        val acceptedExpensesResponse = createAcceptedExpensesResponse(
            groupId = GROUP_ID,
            expenses = expenses,
        )

        // when
        val acceptedExpenses = acceptedExpensesResponse.toDomain()

        // then
        acceptedExpenses.also {
            it shouldHaveSize 3
            it.map { expense -> expense.creatorId } shouldContainExactly creatorIds
            it.map { expense -> expense.title } shouldContainExactly titles
            it.map { expense -> expense.totalCost } shouldContainExactly totalCosts
            it.map { expense -> expense.baseCurrency } shouldContainExactly baseCurrencies
            it.map { expense -> expense.targetCurrency } shouldContainExactly targetCurrencies
            it.map { expense -> expense.participants } shouldContainExactly participants.map { el -> el.map { p -> p.toDomain() } }
            it.map { expense -> expense.exchangeRate } shouldContainExactly exchangeRates
            it.map { expense -> expense.expenseDate } shouldContainExactly expenseDates
        }
    }
},)
