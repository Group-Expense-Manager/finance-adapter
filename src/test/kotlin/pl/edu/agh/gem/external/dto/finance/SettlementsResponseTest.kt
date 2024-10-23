package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.util.createSettlement
import pl.edu.agh.gem.util.createSettlements

class SettlementsResponseTest : ShouldSpec({

    should("map Settlement to SettlementDto") {
        // given
        val settlement = createSettlement()

        // when
        val settlementDto = settlement.toSettlementDto()

        // then
        settlementDto.also {
            it.fromUserId shouldBe settlement.fromUserId
            it.toUserId shouldBe settlement.toUserId
            it.value shouldBe settlement.value
        }
    }

    should("map Settlements  to SettlementsDto") {
        // given
        val currencySettlement = createSettlements()

        // when
        val settlementsDto = currencySettlement.toSettlementsDto()

        // then
        settlementsDto.also {
            it.currency shouldBe currencySettlement.currency
            it.settlements shouldBe currencySettlement.settlements.map { settlement -> settlement.toSettlementDto() }
        }
    }

    should("map List<Settlements> to SettlementsResponse") {
        // given
        val settlementsList = listOf(createSettlements())
        // when
        val settlementsResponse = settlementsList.toSettlementsResponse()

        // then
        settlementsResponse.also {
            it.groupId shouldBe settlementsList.first().groupId
            it.settlements shouldBe settlementsList.map { settlements -> settlements.toSettlementsDto() }
        }
    }
},)
