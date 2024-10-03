package pl.edu.agh.gem.external.dto.group

import pl.edu.agh.gem.internal.model.group.Currency
import pl.edu.agh.gem.internal.model.group.GroupData
import pl.edu.agh.gem.model.GroupMember
import pl.edu.agh.gem.model.GroupMembers

data class GroupResponse(
    val members: List<MemberDto>,
    val groupCurrencies: List<CurrencyDto>,
) {
    fun toDomain() = GroupData(
        members = GroupMembers(members.map { GroupMember(it.id) }),
        currencies = groupCurrencies.map { Currency(it.code) },
    )
}

data class MemberDto(
    val id: String,
)

data class CurrencyDto(
    val code: String,
)

fun Currency.toDto() = CurrencyDto(
    code = code,
)
