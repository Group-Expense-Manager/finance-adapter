package pl.edu.agh.gem.internal.model.finance.report

import pl.edu.agh.gem.internal.model.group.Currency
import java.math.BigDecimal
import java.time.Instant

data class Report(
    val activities: List<ReportActivity>,
)

data class ReportActivity(
    val title: String,
    val date: Instant,
    val value: BigDecimal,
    val currency: Currency,
    val members: List<ReportActivityMember>,
)

data class ReportActivityMember(
    val userId: String,
    val value: BigDecimal,
)
