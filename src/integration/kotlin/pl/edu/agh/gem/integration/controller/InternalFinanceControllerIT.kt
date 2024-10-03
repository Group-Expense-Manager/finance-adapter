package pl.edu.agh.gem.integration.controller

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.finance.ReportResponse
import pl.edu.agh.gem.external.dto.finance.toDto
import pl.edu.agh.gem.external.dto.group.CurrencyDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubAcceptedExpenses
import pl.edu.agh.gem.integration.ability.stubAcceptedPayments
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createAcceptedExpenseDto
import pl.edu.agh.gem.util.createAcceptedExpenseParticipantDto
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import pl.edu.agh.gem.util.createAcceptedPaymentDto
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import pl.edu.agh.gem.util.createAmountDto
import pl.edu.agh.gem.util.createReportActivityMember

class InternalFinanceControllerIT(
    private val service: ServiceTestClient,
) : BaseIntegrationSpec({
    should("get report") {
        val acceptedExpenseDto = createAcceptedExpenseDto(
            creatorId = USER_ID,
            participants = listOf(
                createAcceptedExpenseParticipantDto(
                    participantId = OTHER_USER_ID,
                    participantCost = "2".toBigDecimal(),
                ),
            ),
            baseCurrency = CURRENCY_1,
            targetCurrency = CURRENCY_2,
            totalCost = "3".toBigDecimal(),
            exchangeRate = "3".toBigDecimal(),
        )
        val acceptedPaymentDto = createAcceptedPaymentDto(
            creatorId = USER_ID,
            recipientId = OTHER_USER_ID,
            amount = createAmountDto(
                value = "3".toBigDecimal(),
                currency = CURRENCY_2,
            ),
            fxData = null,
        )
        stubAcceptedExpenses(
            createAcceptedExpensesResponse(
                expenses = listOf(
                    acceptedExpenseDto,
                ),
            ),
            GROUP_ID,
        )
        stubAcceptedPayments(
            createAcceptedPaymentsResponse(
                payments = listOf(
                    acceptedPaymentDto,
                ),
            ),
            GROUP_ID,
        )
        // when
        val response = service.getReport(GROUP_ID)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ReportResponse> {
            groupId shouldBe GROUP_ID
            activities.first().also { first ->
                first.title shouldBe acceptedExpenseDto.title
                first.value shouldBe "9".toBigDecimal()
                first.date shouldBe acceptedExpenseDto.expenseDate
                first.currency shouldBe acceptedExpenseDto.targetCurrency?.let { CurrencyDto(it) }
                first.members.size shouldBe 2
                first.members shouldContainExactlyInAnyOrder listOf(
                    createReportActivityMember(USER_ID, "6".toBigDecimal()),
                    createReportActivityMember(OTHER_USER_ID, "-6".toBigDecimal()),
                ).map { it.toDto() }
            }
            activities.last().also { last ->
                last.title shouldBe acceptedPaymentDto.title
                last.date shouldBe acceptedPaymentDto.date
                last.value shouldBe "3".toBigDecimal()
                last.currency shouldBe CurrencyDto(acceptedPaymentDto.amount.currency)
                last.members shouldContainExactlyInAnyOrder listOf(
                    createReportActivityMember(USER_ID, "3".toBigDecimal()),
                    createReportActivityMember(OTHER_USER_ID, "-3".toBigDecimal()),
                ).map { it.toDto() }
            }
        }
    }
},)
