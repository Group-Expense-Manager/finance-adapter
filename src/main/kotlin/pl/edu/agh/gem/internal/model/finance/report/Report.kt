package pl.edu.agh.gem.internal.model.finance.report

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlement.Settlement
import java.math.BigDecimal
import java.time.Instant

data class Report(
    val groupId: String,
    val currency: String,
    val activities: List<ReportActivity>,
    val balances: List<Balance>,
    val settlements: List<Settlement>,
)

data class ReportActivity(
    val title: String,
    val date: Instant,
    val value: BigDecimal,
    val members: List<ReportActivityMember>,
)

data class ReportActivityMember(
    val userId: String,
    val value: BigDecimal,
)
