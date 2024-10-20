package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.createBalance
import pl.edu.agh.gem.util.createBalances
import java.math.BigDecimal

class BalancesResponseTest : ShouldSpec({

    should("map user-balance to UserBalanceDto") {
        // given
        val balance = createBalance()

        // when
        val userBalanceDto = balance.toUserBalanceDto()

        // then
        userBalanceDto.also {
            it.userId shouldBe USER_ID
            it.value shouldBe BigDecimal.ONE
        }
    }

    should("map currency-user-balances to BalancesDto") {
        // given
        val balances = createBalances()

        // when
        val currencyBalancesDto = balances.toBalancesDto()

        // then
        currencyBalancesDto.also {
            it.currency shouldBe CURRENCY_1
            it.userBalances shouldBe balances.users.map { userBalance -> userBalance.toUserBalanceDto() }
        }
    }

    should("map Balances to BalancesResponse") {
        // given
        val balances = listOf(createBalances())

        // when
        val balancesResponse = balances.toBalancesResponse()

        // then
        balancesResponse.also {
            it.groupId shouldBe GROUP_ID
            it.balances shouldBe balances.map { currencyBalances -> currencyBalances.toBalancesDto() }
        }
    }
},)
