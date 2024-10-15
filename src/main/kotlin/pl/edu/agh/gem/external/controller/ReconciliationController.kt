package pl.edu.agh.gem.external.controller

import mu.KotlinLogging
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.reconciliation.GenerateReconciliationRequest
import pl.edu.agh.gem.internal.service.FinanceService
import pl.edu.agh.gem.internal.service.ReconciliationService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.INTERNAL

@RestController
@RequestMapping(INTERNAL)
class ReconciliationController(
    private val financeService: FinanceService,
    private val reconciliationService: ReconciliationService,
) {

    @PostMapping("generate/groups/{groupId}", consumes = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun generateReconciliation(
        @RequestBody generateReconciliationRequest: GenerateReconciliationRequest,
        @PathVariable groupId: String,
    ) {
        financeService.blockSettlements(groupId, generateReconciliationRequest.currency)
        val balances = financeService.fetchBalances(groupId, generateReconciliationRequest.currency)
        
        logger.info { "Balances fetched for groupId: $groupId and currency: ${generateReconciliationRequest.currency}: $balances" }
        
        financeService.saveBalances(balances)
        reconciliationService.generateNewSettlement(balances)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
