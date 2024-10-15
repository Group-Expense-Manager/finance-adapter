package pl.edu.agh.gem.integration.controller

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.persistence.job.MongoReconciliationJobRepository
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubAcceptedExpenses
import pl.edu.agh.gem.integration.ability.stubAcceptedPayments
import pl.edu.agh.gem.integration.ability.stubGroupManagerGroupData
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.util.DummyData.ANOTHER_USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.createAcceptedExpensesResponse
import pl.edu.agh.gem.util.createAcceptedPaymentsResponse
import pl.edu.agh.gem.util.createCurrenciesDTO
import pl.edu.agh.gem.util.createGroupResponse

class ReconciliationControllerIT(
    private val service: ServiceTestClient,
    @SpyBean private val reconciliationJobRepository: MongoReconciliationJobRepository,
    private val balancesRepository: BalancesRepository,
) : BaseIntegrationSpec({

    should("return OK when reconciliation is generated") {
        // given
        val listOfCurrencies = createCurrenciesDTO("PLN")
        val groupResponse = createGroupResponse(groupCurrencies = listOfCurrencies)
        stubGroupManagerGroupData(groupResponse, GROUP_ID)
        val acceptedPaymentsResponse = createAcceptedPaymentsResponse()
        stubAcceptedPayments(acceptedPaymentsResponse, GROUP_ID)
        val acceptedExpensesResponse = createAcceptedExpensesResponse()
        stubAcceptedExpenses(acceptedExpensesResponse, GROUP_ID)

        // when
        val response = service.generateReconciliation(GROUP_ID, CURRENCY_1)

        // then
        response shouldHaveHttpStatus OK
        verify(reconciliationJobRepository).save(any())
        val balances = balancesRepository.getBalances(GROUP_ID)
        println(balances)
        balances.size shouldBe 1
        balances.map { it.currency } shouldContainExactlyInAnyOrder listOf("PLN")
        balances.find { it.currency == "PLN" }?.also {
            it.users.map { it.userId } shouldContainExactlyInAnyOrder listOf(USER_ID, OTHER_USER_ID, ANOTHER_USER_ID)
            it.users.map { it.value } shouldContainExactlyInAnyOrder listOf("-22.68", "-6.48", "29.16").map { it.toBigDecimal() }
        }
    }
},)
