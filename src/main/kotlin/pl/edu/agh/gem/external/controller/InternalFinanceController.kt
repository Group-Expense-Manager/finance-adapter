package pl.edu.agh.gem.external.controller

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.finance.BalancesResponse
import pl.edu.agh.gem.external.dto.finance.InternalActivitiesResponse
import pl.edu.agh.gem.external.dto.finance.toBalancesResponse
import pl.edu.agh.gem.external.dto.finance.toInternalActivitiesResponse
import pl.edu.agh.gem.internal.service.FinanceService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.INTERNAL

@RestController
@RequestMapping(INTERNAL)
class InternalFinanceController(
    private val financeService: FinanceService,
) {

    @GetMapping("activities/groups/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getActivities(
        @PathVariable groupId: String,
    ): InternalActivitiesResponse {
        return financeService.getActivities(groupId).toInternalActivitiesResponse(groupId)
    }

    @GetMapping("balances/groups/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getBalances(
        @PathVariable groupId: String,
    ): BalancesResponse {
        return financeService.getBalances(groupId).toBalancesResponse()
    }
}
