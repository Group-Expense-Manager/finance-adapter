package pl.edu.agh.gem.external.dto.expense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.dto.payment.AmountDto
import pl.edu.agh.gem.external.dto.payment.FxDataDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.ACCEPTED
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.REJECTED
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createExpenseManagerActivityDto
import pl.edu.agh.gem.util.createFxDataDto
import java.math.BigDecimal
import java.time.Instant

class ExpenseManagerActivitiesResponseTest : ShouldSpec({
    should("map ExpenseManagerActivityDto to Activity correctly when fxData is null") {
        // given
        val expenseManagerActivityDto = createExpenseManagerActivityDto(fxData = null)

        // when
        val activity = expenseManagerActivityDto.toActivity()

        // then
        activity.also {
            it.activityId shouldBe expenseManagerActivityDto.expenseId
            it.type shouldBe EXPENSE
            it.creatorId shouldBe expenseManagerActivityDto.creatorId
            it.title shouldBe expenseManagerActivityDto.title
            it.value shouldBe expenseManagerActivityDto.amount.value
            it.currency shouldBe expenseManagerActivityDto.amount.currency
            it.status shouldBe expenseManagerActivityDto.status
            it.participantIds shouldBe expenseManagerActivityDto.participantIds
            it.date shouldBe expenseManagerActivityDto.expenseDate
        }
    }

    should("map ExpenseManagerActivityDto to Activity correctly when fxData is not null") {
        // given
        val expenseManagerActivityDto = createExpenseManagerActivityDto(
            amount = createAmountDto(value = "1.2".toBigDecimal()),
            fxData = createFxDataDto(exchangeRate = "3".toBigDecimal()),
        )

        // when
        val activity = expenseManagerActivityDto.toActivity()

        // then
        activity.also {
            it.activityId shouldBe expenseManagerActivityDto.expenseId
            it.type shouldBe EXPENSE
            it.creatorId shouldBe expenseManagerActivityDto.creatorId
            it.title shouldBe expenseManagerActivityDto.title
            it.value shouldBe "3.6".toBigDecimal()
            it.currency shouldBe expenseManagerActivityDto.fxData?.targetCurrency
            it.status shouldBe expenseManagerActivityDto.status
            it.participantIds shouldBe expenseManagerActivityDto.participantIds
            it.date shouldBe expenseManagerActivityDto.expenseDate
        }
    }

    should("map ExpenseManagerActivitiesResponse to Activities correctly") {
        // given
        val expenseIds = listOf("expenseId1", "expenseId2", "expenseId3")
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
                amount = amounts[index],
                fxData = fxData[index],
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
            it.map { activity -> activity.value } shouldContainExactly listOf("2".toBigDecimal(), "2".toBigDecimal(), "6".toBigDecimal())
            it.map { activity -> activity.currency } shouldContainExactly listOf("EUR", "EUR", "PLN")
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
