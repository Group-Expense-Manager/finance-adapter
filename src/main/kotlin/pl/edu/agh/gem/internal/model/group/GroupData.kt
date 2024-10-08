package pl.edu.agh.gem.internal.model.group

import pl.edu.agh.gem.model.GroupMembers

data class GroupData(
    val members: GroupMembers,
    val currencies: Currencies,
)

typealias Currencies = List<Currency>

data class Currency(
    val code: String,
)
