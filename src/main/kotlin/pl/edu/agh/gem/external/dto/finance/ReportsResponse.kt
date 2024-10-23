package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.report.Report
import pl.edu.agh.gem.internal.model.finance.report.ReportActivity
import pl.edu.agh.gem.internal.model.finance.report.ReportActivityMember
import java.math.BigDecimal
import java.time.Instant

data class ReportsResponse(
    val groupId: String,
    val reports: List<ReportDto>,
)

fun List<Report>.toReportsResponse() = ReportsResponse(
    groupId = this.first().groupId,
    reports = this.map { it.toReportDto() },
)

data class ReportDto(
    val currency: String,
    val activities: List<ReportActivityDto>,
    val balances: List<UserBalanceDto>,
    val settlements: List<SettlementDto>,
)

fun Report.toReportDto() = ReportDto(
    currency = currency,
    activities = activities.map { it.toReportActivityDto() },
    balances = balances.map { it.toUserBalanceDto() },
    settlements = settlements.map { it.toSettlementDto() },
)

data class ReportActivityDto(
    val title: String,
    val date: Instant,
    val value: BigDecimal,
    val members: List<ReportActivityMemberDto>,
)

fun ReportActivity.toReportActivityDto() = ReportActivityDto(
    title = title,
    date = date,
    value = value,
    members = members.map { it.toReportActivityMemberDto() },
)

data class ReportActivityMemberDto(
    val userId: String,
    val value: BigDecimal,
)

fun ReportActivityMember.toReportActivityMemberDto() = ReportActivityMemberDto(
    userId = userId,
    value = value,
)
