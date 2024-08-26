package pl.edu.agh.gem.external.dto.expense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.ACCEPTED
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.REJECTED
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.util.createExpenseManagerActivityDto
import java.math.BigDecimal
import java.time.Instant

class ExpenseManagerActivitiesResponseTest : ShouldSpec({
    should("map ExpenseManagerActivityDTO to Activity correctly") {
        // given
        val expenseManagerActivityDTO = createExpenseManagerActivityDto()

        // when
        val activity = expenseManagerActivityDTO.toActivity()

        // then
        activity.also {
            it.activityId shouldBe expenseManagerActivityDTO.expenseId
            it.type shouldBe EXPENSE
            it.creatorId shouldBe expenseManagerActivityDTO.creatorId
            it.title shouldBe expenseManagerActivityDTO.title
            it.sum shouldBe expenseManagerActivityDTO.cost
            it.baseCurrency shouldBe expenseManagerActivityDTO.baseCurrency
            it.targetCurrency shouldBe expenseManagerActivityDTO.targetCurrency
            it.status shouldBe expenseManagerActivityDTO.status
            it.participantIds shouldBe expenseManagerActivityDTO.participantIds
            it.date shouldBe expenseManagerActivityDTO.expenseDate
        }
    }

    should("map ExpenseManagerActivitiesResponse to Activities correctly") {
        // given
        val expenseIds = listOf("expenseId1", "expenseId2", "expenseId3")
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val costs = listOf(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)
        val baseCurrencies = listOf("PLN", "EUR", "USD")
        val targetCurrencies = listOf("EUR", null, "PLN")
        val statuses = listOf(PENDING, ACCEPTED, REJECTED)
        val participantIds = listOf(
            listOf("participant1", "participant2"),
            listOf("participant3", "participant4"),
            listOf("participant5", "participant6"),
        )
        val expenseDates = listOf(
            Instant.ofEpochSecond(1000),
            Instant.ofEpochSecond(2000),
            Instant.ofEpochSecond(3000),
        )
        val expenses = expenseIds.mapIndexed { index, expenseId ->
            createExpenseManagerActivityDto(
                expenseId = expenseId,
                creatorId = creatorIds[index],
                title = titles[index],
                cost = costs[index],
                baseCurrency = baseCurrencies[index],
                targetCurrency = targetCurrencies[index],
                status = statuses[index],
                participantIds = participantIds[index],
                expenseDate = expenseDates[index],
            )
        }

        val expenseManagerActivitiesResponse = ExpenseManagerActivitiesResponse(
            groupId = GROUP_ID,
            expenses = expenses,
        )

        // when
        val activities = expenseManagerActivitiesResponse.toDomain()

        // then
        activities.also {
            it shouldHaveSize 3
            it.map { activity -> activity.activityId } shouldContainExactly expenseIds
            it.map { activity -> activity.type } shouldContainExactly listOf(EXPENSE, EXPENSE, EXPENSE)
            it.map { activity -> activity.creatorId } shouldContainExactly creatorIds
            it.map { activity -> activity.title } shouldContainExactly titles
            it.map { activity -> activity.sum } shouldContainExactly costs
            it.map { activity -> activity.baseCurrency } shouldContainExactly baseCurrencies
            it.map { activity -> activity.targetCurrency } shouldContainExactly targetCurrencies
            it.map { activity -> activity.status } shouldContainExactly statuses
            it.map { activity -> activity.participantIds } shouldContainExactly participantIds
            it.map { activity -> activity.date } shouldContainExactly expenseDates
        }
    }

    should("return empty activities when there are no activities") {
        // given
        val expenseManagerActivitiesResponse = ExpenseManagerActivitiesResponse(
            groupId = GROUP_ID,
            expenses = listOf(),
        )

        // when
        val activities = expenseManagerActivitiesResponse.toDomain()

        // then
        activities shouldHaveSize 0
    }
},)
