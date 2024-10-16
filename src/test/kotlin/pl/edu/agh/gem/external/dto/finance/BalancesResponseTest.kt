package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.util.createBalance
import pl.edu.agh.gem.util.createBalances
import pl.edu.agh.gem.util.createCurrencyBalances

class BalancesResponseTest : ShouldSpec({

    should("map Balance to BalanceDto") {
        // given
        val balance = createBalance()

        // when
        val balanceDto = balance.toBalanceDto()

        // then
        balanceDto.also {
            it.userId shouldBe balance.userId
            it.value shouldBe balance.value
        }
    }

    should("map CurrencyBalances to CurrencyBalancesDto") {
        // given
        val currencyBalances = createCurrencyBalances()

        // when
        val currencyBalancesDto = currencyBalances.toCurrencyBalancesDto()

        // then
        currencyBalancesDto.also {
            it.currency shouldBe currencyBalances.currency.toDto()
            it.balances shouldBe currencyBalances.balances.map { balance -> balance.toBalanceDto() }
        }
    }

    should("map Balances to BalancesResponse") {
        // given
        val balances = createBalances()

        // when
        val balancesResponse = balances.toBalancesResponse(GROUP_ID)

        // then
        balancesResponse.also {
            it.groupId shouldBe GROUP_ID
            it.currencyBalances shouldBe balances.map { currencyBalances -> currencyBalances.toCurrencyBalancesDto() }
        }
    }
},)
