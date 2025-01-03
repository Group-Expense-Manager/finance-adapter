package pl.edu.agh.gem.external.controller

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.external.dto.finance.BalancesResponse
import pl.edu.agh.gem.external.dto.finance.ExternalActivitiesResponse
import pl.edu.agh.gem.external.dto.finance.SettlementsResponse
import pl.edu.agh.gem.external.dto.finance.toBalancesResponse
import pl.edu.agh.gem.external.dto.finance.toExternalActivitiesResponse
import pl.edu.agh.gem.external.dto.finance.toSettlementsResponse
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy
import pl.edu.agh.gem.internal.service.FinanceService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping(EXTERNAL)
class ExternalFinanceController(
    private val financeService: FinanceService,
    private val groupManagerClient: GroupManagerClient,
) {

    @GetMapping("activities/groups/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getActivities(
        @GemUserId userId: String,
        @PathVariable groupId: String,
        @RequestParam title: String?,
        @RequestParam status: ActivityStatus?,
        @RequestParam isCreator: Boolean?,
        @RequestParam type: ActivityType?,
        @RequestParam currency: String?,
        @RequestParam sortedBy: SortedBy?,
        @RequestParam sortOrder: SortOrder?,
    ): ExternalActivitiesResponse {
        userId.checkIfUserHaveAccess(groupId)
        val filterOptions = FilterOptions.create(
            userId = userId,
            title = title,
            status = status,
            isCreator = isCreator,
            type = type,
            currency = currency,
            sortedBy = sortedBy,
            sortOrder = sortOrder,
        )
        return financeService.getActivities(groupId, filterOptions).toExternalActivitiesResponse(groupId)
    }

    @GetMapping("balances/groups/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getBalances(
        @GemUserId userId: String,
        @PathVariable groupId: String,
    ): BalancesResponse {
        userId.checkIfUserHaveAccess(groupId)
        return financeService.getBalances(groupId).toBalancesResponse()
    }

    @GetMapping("settlements/groups/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getSettlements(
        @GemUserId userId: String,
        @PathVariable groupId: String,
    ): SettlementsResponse {
        userId.checkIfUserHaveAccess(groupId)
        return financeService.getSettlements(groupId).toSettlementsResponse()
    }

    private fun String.checkIfUserHaveAccess(groupId: String) {
        groupManagerClient.getGroups(this).find { it.groupId == groupId } ?: throw UserWithoutGroupAccessException(this)
    }
}
